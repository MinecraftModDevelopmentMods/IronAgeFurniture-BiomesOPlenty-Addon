package zone.moddev.mc.iafbopaddon.compat;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import zone.moddev.mc.iafbopaddon.IAFBiomesOPlentyAddon;
import zone.moddev.mc.iafbopaddon.world.MigrationWorldState;

/**
 * Idempotently converts legacy BOP padded benches and item stacks after their
 * owning chunk or inventory becomes available. This intentionally avoids
 * unsupported injection into Mojang's DataFixer registry.
 */
public final class LegacyPaddedBenchMigration {
    private static final Logger SERIALIZATION_LOGGER = LogUtils.getLogger();
    private static final Queue<LevelChunk> PENDING_CHUNKS = new ConcurrentLinkedQueue<>();
    private static final LongAdder RUN_BLOCKS = new LongAdder();
    private static final LongAdder RUN_ITEMS = new LongAdder();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::rawChunkLoaded);
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::chunkLoaded);
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::serverTick);
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::playerLogin);
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::entityJoin);
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::serverAboutToStart);
        NeoForge.EVENT_BUS.addListener(LegacyPaddedBenchMigration::serverStopping);
    }

    static void rawChunkLoaded(ChunkDataEvent.Load event) {
        for (CompoundTag entity : event.getData().entities()) migrateStacksInNbt(entity, null);
        for (CompoundTag blockEntity : event.getData().blockEntities()) migrateStacksInNbt(blockEntity, null);
    }

    static void chunkLoaded(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof Level level)
                || !(event.getChunk() instanceof LevelChunk chunk) || level.isClientSide()) return;
        PENDING_CHUNKS.add(chunk);
    }

    static void serverTick(ServerTickEvent.Post event) {
        LevelChunk chunk;
        while ((chunk = PENDING_CHUNKS.poll()) != null) migrateChunk(chunk);
    }

    static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        MigrationWorldState state = MigrationWorldState.get(player.level());
        migrateInventory(player.getInventory(), state);
        migrateInventory(player.getEnderChestInventory(), state);
    }

    static void entityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.getEntity() instanceof Player) return;
        MigrationWorldState state = MigrationWorldState.get(event.getLevel());
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            StackResult result = migrateStack(itemEntity.getItem(), state);
            if (result.changed()) itemEntity.setItem(result.stack());
            return;
        }
        Entity entity = event.getEntity();
        try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(
                entity.problemPath(), SERIALIZATION_LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(problems, entity.registryAccess());
            entity.saveWithoutId(output);
            CompoundTag serialized = output.buildResult();
            if (migrateStacksInNbt(serialized, state)) {
                entity.load(TagValueInput.create(problems, entity.registryAccess(), serialized));
            }
        }
    }

    private static void serverAboutToStart(ServerAboutToStartEvent event) {
        RUN_BLOCKS.reset();
        RUN_ITEMS.reset();
    }

    private static void serverStopping(ServerStoppingEvent event) {
        PENDING_CHUNKS.clear();
        long blocks = RUN_BLOCKS.sum();
        long items = RUN_ITEMS.sum();
        if (blocks + items > 0L) {
            IAFBiomesOPlentyAddon.LOGGER.info(
                    "Legacy padded-bench migration converted {} blocks and {} items this run",
                    blocks, items);
        }
    }

    private static void migrateChunk(LevelChunk chunk) {
        Level level = chunk.getLevel();
        if (level.isClientSide()) return;
        MigrationWorldState state = MigrationWorldState.get(level);
        boolean changed = migrateChunkInventories(chunk, state);
        long blocks = migrateChunkBlocks(chunk);
        if (blocks > 0L) {
            state.recordChunk();
            state.recordBlocks(blocks);
            RUN_BLOCKS.add(blocks);
            changed = true;
        }
        if (changed) chunk.markUnsaved();
    }

    private static long migrateChunkBlocks(LevelChunk chunk) {
        long converted = 0L;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = chunk.getMinY(); y <= chunk.getMaxY(); ++y) {
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) {
                    cursor.set(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
                    BlockState oldState = chunk.getBlockState(cursor);
                    Identifier oldId = BuiltInRegistries.BLOCK.getKey(oldState.getBlock());
                    LegacyPaddedBenchMappings.LegacyId legacy = LegacyPaddedBenchMappings.parseLegacy(oldId);
                    if (legacy == null || !LegacyPaddedBenchRegistry.isCompatibilityBlock(oldState.getBlock())) continue;
                    String color = "red";
                    BlockEntity blockEntity = chunk.getBlockEntity(cursor);
                    if (blockEntity instanceof LegacyPaddedBenchBlockEntity padded) color = padded.color();
                    Identifier targetId = LegacyPaddedBenchMappings.modernId(legacy.wood(), legacy.back(), color);
                    Block target = BuiltInRegistries.BLOCK.getValue(targetId);
                    if (target == null || target.defaultBlockState().isAir()) {
                        IAFBiomesOPlentyAddon.LOGGER.error("Cannot migrate {} at {}: target {} is missing",
                                oldId, cursor, targetId);
                        continue;
                    }
                    BlockState replacement = copySharedProperties(oldState, target.defaultBlockState());
                    chunk.removeBlockEntity(cursor);
                    ChunkMigrationAccess.setBlockState(chunk, cursor, replacement);
                    ++converted;
                }
            }
        }
        return converted;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static BlockState copySharedProperties(BlockState source, BlockState target) {
        BlockState result = target;
        for (Property property : source.getProperties()) {
            if (result.hasProperty(property)) result = result.setValue(property, source.getValue(property));
        }
        return result;
    }

    private static boolean migrateChunkInventories(LevelChunk chunk, MigrationWorldState state) {
        boolean changed = false;
        for (BlockEntity blockEntity : new ArrayList<>(chunk.getBlockEntities().values())) {
            if (blockEntity instanceof LegacyPaddedBenchBlockEntity) continue;
            CompoundTag serialized = blockEntity.saveWithFullMetadata(chunk.getLevel().registryAccess());
            if (migrateStacksInNbt(serialized, state)) {
                try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(
                        blockEntity.problemPath(), SERIALIZATION_LOGGER)) {
                    blockEntity.loadWithComponents(TagValueInput.create(
                            problems, chunk.getLevel().registryAccess(), serialized));
                }
                blockEntity.setChanged();
                changed = true;
            }
        }
        return changed;
    }

    private static void migrateInventory(Container inventory, MigrationWorldState state) {
        boolean changed = false;
        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            StackResult result = migrateStack(inventory.getItem(slot), state);
            if (result.changed()) {
                inventory.setItem(slot, result.stack());
                changed = true;
            }
        }
        if (changed) inventory.setChanged();
    }

    static StackResult migrateStack(ItemStack stack, MigrationWorldState state) {
        if (stack == null || stack.isEmpty()) return new StackResult(ItemStack.EMPTY, false);
        ItemStack working = stack;
        boolean changed = false;

        ItemContainerContents container = working.get(DataComponents.CONTAINER);
        if (container != null) {
            List<ItemStack> migrated = new ArrayList<>();
            boolean nestedChanged = false;
            for (ItemStack nested : container.itemCopies().toList()) {
                StackResult result = migrateStack(nested, state);
                migrated.add(result.stack());
                nestedChanged |= result.changed();
            }
            if (nestedChanged) {
                working = working.copy();
                working.set(DataComponents.CONTAINER, container.copyWithContents(migrated.stream()));
                changed = true;
            }
        }
        BundleContents bundle = working.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            List<ItemStack> migrated = new ArrayList<>();
            boolean nestedChanged = false;
            for (ItemStack nested : bundle.itemCopies().toList()) {
                StackResult result = migrateStack(nested, state);
                migrated.add(result.stack());
                nestedChanged |= result.changed();
            }
            if (nestedChanged) {
                if (!changed) working = working.copy();
                working.set(DataComponents.BUNDLE_CONTENTS, bundle.copyWithContents(migrated.stream()));
                changed = true;
            }
        }

        CustomData custom = working.get(DataComponents.CUSTOM_DATA);
        if (custom == null || !custom.contains(LegacyPaddedBenchBlockEntity.COLOR_TAG)) {
            return new StackResult(working, changed);
        }
        CompoundTag customTag = custom.copyTag();
        String color = LegacyPaddedBenchMappings.normalizedColor(
                customTag.getStringOr(LegacyPaddedBenchBlockEntity.COLOR_TAG, "red"));
        Identifier current = BuiltInRegistries.ITEM.getKey(working.getItem());
        Identifier targetId = LegacyPaddedBenchMappings.recolorModernRed(current, color);
        if (targetId == null) {
            LegacyPaddedBenchMappings.LegacyId legacy = LegacyPaddedBenchMappings.parseLegacy(current);
            if (legacy != null) targetId = LegacyPaddedBenchMappings.modernId(legacy.wood(), legacy.back(), color);
        }
        if (targetId == null) return new StackResult(working, changed);
        Item target = BuiltInRegistries.ITEM.getValue(targetId);
        if (target == null || target == Items.AIR) return new StackResult(working, changed);

        ItemStack migrated = working.transmuteCopy(target, working.getCount());
        customTag.remove(LegacyPaddedBenchBlockEntity.COLOR_TAG);
        if (customTag.isEmpty()) migrated.remove(DataComponents.CUSTOM_DATA);
        else migrated.set(DataComponents.CUSTOM_DATA, CustomData.of(customTag));
        recordItems(state, stack.getCount());
        return new StackResult(migrated, true);
    }

    public static boolean migrateStacksInNbt(Tag tag, MigrationWorldState state) {
        boolean changed = false;
        if (tag instanceof CompoundTag compound) {
            if (compound.contains("id") && hasStackCount(compound)) {
                Identifier current = Identifier.tryParse(compound.getStringOr("id", ""));
                String color = extractColor(compound);
                LegacyPaddedBenchMappings.LegacyId legacy = LegacyPaddedBenchMappings.parseLegacy(current);
                Identifier target = legacy == null
                        ? LegacyPaddedBenchMappings.recolorModernRed(current, color)
                        : LegacyPaddedBenchMappings.modernId(legacy.wood(), legacy.back(), color);
                if (target != null && (legacy != null || containsColor(compound))) {
                    compound.putString("id", target.toString());
                    removeColor(compound);
                    recordItems(state, stackCount(compound));
                    changed = true;
                }
            }
            for (String key : new ArrayList<>(compound.keySet())) {
                Tag child = compound.get(key);
                if (child != null) changed |= migrateStacksInNbt(child, state);
            }
        } else if (tag instanceof ListTag list) {
            for (Tag child : list) changed |= migrateStacksInNbt(child, state);
        }
        return changed;
    }

    static String extractColor(CompoundTag stack) {
        CompoundTag owner = colorOwner(stack);
        return owner == null ? "red" : LegacyPaddedBenchMappings.normalizedColor(
                owner.getStringOr(LegacyPaddedBenchBlockEntity.COLOR_TAG, "red"));
    }

    private static boolean containsColor(CompoundTag stack) {
        return colorOwner(stack) != null;
    }

    private static CompoundTag colorOwner(CompoundTag stack) {
        if (stack.contains(LegacyPaddedBenchBlockEntity.COLOR_TAG)) return stack;
        CompoundTag legacyTag = stack.getCompoundOrEmpty("tag");
        if (legacyTag.contains(LegacyPaddedBenchBlockEntity.COLOR_TAG)) return legacyTag;
        CompoundTag components = stack.getCompoundOrEmpty("components");
        CompoundTag custom = components.getCompoundOrEmpty("minecraft:custom_data");
        return custom.contains(LegacyPaddedBenchBlockEntity.COLOR_TAG) ? custom : null;
    }

    private static void removeColor(CompoundTag stack) {
        CompoundTag owner = colorOwner(stack);
        if (owner != null) owner.remove(LegacyPaddedBenchBlockEntity.COLOR_TAG);
    }

    private static void recordItems(MigrationWorldState state, long count) {
        if (count <= 0L) return;
        if (state != null) state.recordItems(count);
        RUN_ITEMS.add(count);
    }

    private static boolean hasStackCount(CompoundTag stack) {
        return stack.contains("Count") || stack.contains("count");
    }

    private static int stackCount(CompoundTag stack) {
        return stack.contains("count")
                ? stack.getIntOr("count", 0) : stack.getByteOr("Count", (byte) 0) & 255;
    }

    record StackResult(ItemStack stack, boolean changed) {
    }

    private LegacyPaddedBenchMigration() {
    }
}
