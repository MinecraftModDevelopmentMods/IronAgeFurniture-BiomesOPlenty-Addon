package zone.moddev.mc.iafbopaddon.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class LegacyPaddedBenchMappingsTest {
    @Test
    void preservesAllSixteenStableColourNames() {
        assertEquals(16, LegacyPaddedBenchMappings.COLORS.size());
        for (String color : LegacyPaddedBenchMappings.COLORS) {
            assertTrue(LegacyPaddedBenchMappings.validColor(color));
            assertEquals(color, LegacyPaddedBenchMappings.normalizedColor(color));
        }
        assertEquals("red", LegacyPaddedBenchMappings.normalizedColor("not_a_colour"));
    }

    @Test
    void appliesOnlyTheDocumentedRetiredWoodFallbacks() {
        Map<String, String> expected = Map.of(
                "cherry", "chair_wood_ironage_bench_padded_blue_single_cherry",
                "ebony", "chair_wood_ironage_bench_padded_blue_single_biomesoplenty_hellbark",
                "ethereal", "chair_wood_ironage_bench_padded_blue_single_warped",
                "eucalyptus", "chair_wood_ironage_bench_padded_blue_single_biomesoplenty_origin_oak",
                "mangrove", "chair_wood_ironage_bench_padded_blue_single_pale_oak",
                "sacred_oak", "chair_wood_ironage_bench_padded_blue_single_biomesoplenty_origin_oak");
        expected.forEach((wood, path) -> assertEquals(path,
                LegacyPaddedBenchMappings.modernId(wood, false, "blue").getPath()));
        assertEquals("chair_wood_ironage_bench_back_padded_blue_single_biomesoplenty_fir",
                LegacyPaddedBenchMappings.modernId("fir", true, "blue").getPath());
        assertNull(LegacyPaddedBenchMappings.modernId("invented", false, "red"));
    }

    @Test
    void migratesRedOnlyAndColouredRawStacksIdempotently() {
        CompoundTag redOnly = stack(
                "ironagefurniture:chair_wood_ironage_bench_padded_single_biomesoplenty_fir", 3);
        assertTrue(LegacyPaddedBenchMigration.migrateStacksInNbt(redOnly, null));
        assertEquals("ironagefurniture:chair_wood_ironage_bench_padded_red_single_biomesoplenty_fir",
                redOnly.getStringOr("id", ""));
        assertFalse(LegacyPaddedBenchMigration.migrateStacksInNbt(redOnly, null));

        CompoundTag coloured = stack(
                "ironagefurniture:chair_wood_ironage_bench_back_padded_single_biomesoplenty_ebony", 2);
        CompoundTag legacyTag = new CompoundTag();
        legacyTag.putString("Color", "purple");
        legacyTag.putString("Preserved", "yes");
        coloured.put("tag", legacyTag);
        assertTrue(LegacyPaddedBenchMigration.migrateStacksInNbt(coloured, null));
        assertEquals("ironagefurniture:chair_wood_ironage_bench_back_padded_purple_single_biomesoplenty_hellbark",
                coloured.getStringOr("id", ""));
        assertFalse(coloured.getCompoundOrEmpty("tag").contains("Color"));
        assertEquals("yes", coloured.getCompoundOrEmpty("tag").getStringOr("Preserved", ""));
        assertFalse(LegacyPaddedBenchMigration.migrateStacksInNbt(coloured, null));
    }

    @Test
    void migratesComponentColourAndNestedContainerStacks() {
        CompoundTag outer = stack("minecraft:shulker_box", 1);
        CompoundTag components = new CompoundTag();
        ListTag contents = new ListTag();
        CompoundTag nested = stack(
                "ironagefurniture:chair_wood_ironage_bench_padded_red_single_biomesoplenty_willow", 5);
        CompoundTag nestedComponents = new CompoundTag();
        CompoundTag customData = new CompoundTag();
        customData.putString("Color", "cyan");
        customData.putInt("Other", 7);
        nestedComponents.put("minecraft:custom_data", customData);
        nested.put("components", nestedComponents);
        contents.add(nested);
        components.put("minecraft:container", contents);
        outer.put("components", components);

        assertTrue(LegacyPaddedBenchMigration.migrateStacksInNbt(outer, null));
        CompoundTag migrated = outer.getCompoundOrEmpty("components")
                .getListOrEmpty("minecraft:container").getCompoundOrEmpty(0);
        assertEquals("ironagefurniture:chair_wood_ironage_bench_padded_cyan_single_biomesoplenty_willow",
                migrated.getStringOr("id", ""));
        CompoundTag migratedCustom = migrated.getCompoundOrEmpty("components")
                .getCompoundOrEmpty("minecraft:custom_data");
        assertFalse(migratedCustom.contains("Color"));
        assertEquals(7, migratedCustom.getIntOr("Other", 0));
    }

    @Test
    void preservesCoreCherryAliasIntermediate() {
        Identifier intermediate = LegacyPaddedBenchMappings.compatibilityBlockId("cherry", true);
        assertEquals("chair_wood_ironage_bench_back_padded_single_cherry", intermediate.getPath());
        assertEquals("cherry", LegacyPaddedBenchMappings.parseLegacy(intermediate).wood());
        assertEquals("chair_wood_ironage_bench_back_padded_black_single_cherry",
                LegacyPaddedBenchMappings.modernId("cherry", true, "black").getPath());
    }

    @Test
    void leavesTheOriginalCherryAliasOwnedByCore() throws Exception {
        String registry = java.nio.file.Files.readString(java.nio.file.Path.of(
                "src/main/java/zone/moddev/mc/iafbopaddon/compat/LegacyPaddedBenchRegistry.java"));
        assertTrue(registry.contains("if (!\"cherry\".equals(wood))"));
        assertTrue(registry.contains("registerIntermediateCherryItemAlias(false)"));
    }

    private static CompoundTag stack(String id, int count) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putInt("count", count);
        return tag;
    }
}
