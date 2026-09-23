package zone.moddev.mc.iafbopaddon.compat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

/** Registers hidden compatibility blocks and item aliases without consuming visible items. */
public final class LegacyPaddedBenchRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS, LegacyPaddedBenchMappings.NAMESPACE);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES, LegacyPaddedBenchMappings.NAMESPACE);
    private static final List<RegistryObject<Block>> COMPATIBILITY_BLOCKS = new ArrayList<>();
    private static final Map<Identifier, Identifier> ITEM_REMAPS = new LinkedHashMap<>();

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
        // link here so Forge never sees a duplicate missing-mapping key.
        registerBlock("cherry", false);
        registerBlock("cherry", true);
        registerIntermediateCherryItemAlias(false);
        registerIntermediateCherryItemAlias(true);
    }

    public static final RegistryObject<BlockEntityType<LegacyPaddedBenchBlockEntity>>
            PADDED_BENCH_COLOUR = BLOCK_ENTITIES.register("padded_bench_colour", () ->
                    new BlockEntityType<>(LegacyPaddedBenchBlockEntity::new,
                            Set.copyOf(COMPATIBILITY_BLOCKS.stream().map(RegistryObject::get).toList())));

    public static void register(BusGroup modBusGroup) {
        BLOCKS.register(modBusGroup);
        BLOCK_ENTITIES.register(modBusGroup);
        MissingMappingsEvent.BUS.addListener(LegacyPaddedBenchRegistry::onMissingMappings);
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
        ITEM_REMAPS.put(LegacyPaddedBenchMappings.originalLegacyId(wood, back),
                LegacyPaddedBenchMappings.modernId(wood, back, "red"));
    }

    private static void registerIntermediateCherryItemAlias(boolean back) {
        ITEM_REMAPS.put(LegacyPaddedBenchMappings.compatibilityBlockId("cherry", back),
                LegacyPaddedBenchMappings.modernId("cherry", back, "red"));
    }

    private static void onMissingMappings(MissingMappingsEvent event) {
        for (MissingMappingsEvent.Mapping<Block> mapping : event.getMappings(
                ForgeRegistries.Keys.BLOCKS, LegacyPaddedBenchMappings.NAMESPACE)) {
            Identifier targetId = LegacyPaddedBenchMappings.retiredFurnitureTarget(mapping.getKey());
            if (targetId == null) continue;
            Block target = ForgeRegistries.BLOCKS.getValue(targetId);
            if (target != null) mapping.remap(target);
        }
        for (MissingMappingsEvent.Mapping<Item> mapping : event.getMappings(
                ForgeRegistries.Keys.ITEMS, LegacyPaddedBenchMappings.NAMESPACE)) {
            Identifier targetId = ITEM_REMAPS.get(mapping.getKey());
            if (targetId == null) {
                targetId = LegacyPaddedBenchMappings.retiredFurnitureTarget(mapping.getKey());
            }
            if (targetId == null) continue;
            Item target = ForgeRegistries.ITEMS.getValue(targetId);
            if (target != null) mapping.remap(target);
        }
    }

    private LegacyPaddedBenchRegistry() {
    }
}
