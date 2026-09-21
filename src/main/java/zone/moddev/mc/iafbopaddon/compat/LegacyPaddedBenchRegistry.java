package zone.moddev.mc.iafbopaddon.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers hidden compatibility blocks and item aliases without consuming visible items. */
public final class LegacyPaddedBenchRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            BuiltInRegistries.BLOCK, LegacyPaddedBenchMappings.NAMESPACE);
    private static final DeferredRegister<Item> ITEM_ALIASES = DeferredRegister.create(
            BuiltInRegistries.ITEM, LegacyPaddedBenchMappings.NAMESPACE);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, LegacyPaddedBenchMappings.NAMESPACE);
    private static final List<DeferredHolder<Block, Block>> COMPATIBILITY_BLOCKS = new ArrayList<>();

    static {
        for (String wood : LegacyPaddedBenchMappings.LEGACY_BOP_WOODS) {
            if (!"cherry".equals(wood)) {
                registerBlock(wood, false);
                registerBlock(wood, true);
                registerItemAlias(wood, false);
                registerItemAlias(wood, true);
            }
        }
        // Core owns the old BOP-cherry aliases and points them at these
        // intermediate no-colour vanilla-cherry IDs. Register only the next
        // link here so NeoForge never sees a duplicate alias key.
        registerBlock("cherry", false);
        registerBlock("cherry", true);
        registerIntermediateCherryItemAlias(false);
        registerIntermediateCherryItemAlias(true);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LegacyPaddedBenchBlockEntity>>
            PADDED_BENCH_COLOUR = BLOCK_ENTITIES.register("padded_bench_colour", () ->
                    new BlockEntityType<>(LegacyPaddedBenchBlockEntity::new,
                            Set.copyOf(COMPATIBILITY_BLOCKS.stream().map(DeferredHolder::get).toList())));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEM_ALIASES.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    public static boolean isCompatibilityBlock(Block block) {
        return COMPATIBILITY_BLOCKS.stream().anyMatch(holder -> holder.get() == block);
    }

    private static void registerBlock(String wood, boolean back) {
        Identifier id = LegacyPaddedBenchMappings.compatibilityBlockId(wood, back);
        COMPATIBILITY_BLOCKS.add(BLOCKS.register(id.getPath(), () -> back
                ? new LegacyPaddedBackBench(id.getPath()) : new LegacyPaddedBench(id.getPath())));
    }

    private static void registerItemAlias(String wood, boolean back) {
        ITEM_ALIASES.addAlias(LegacyPaddedBenchMappings.originalLegacyId(wood, back),
                LegacyPaddedBenchMappings.modernId(wood, back, "red"));
    }

    private static void registerIntermediateCherryItemAlias(boolean back) {
        ITEM_ALIASES.addAlias(LegacyPaddedBenchMappings.compatibilityBlockId("cherry", back),
                LegacyPaddedBenchMappings.modernId("cherry", back, "red"));
    }

    private LegacyPaddedBenchRegistry() {
    }
}
