package zone.moddev.mc.iafbopaddon.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.junit.jupiter.api.Test;

/** Exercises binary fixtures emitted by the real 1.10 and 1.12 runtimes. */
class LegacyMigrationFixtureTest {
    private static final List<String> VERSIONS = List.of("1.10", "1.12");

    @Test
    void releasedRedOnlyFixturesBecomeRedAndAreIdempotent() throws IOException {
        for (String version : VERSIONS) {
            CompoundTag fixture = read(version, "iaf-" + version + "-red-only.nbt");
            assertEquals(32, fixture.getListOrEmpty("PlayerInventory").size());
            assertEquals(32, fixture.getListOrEmpty("PlacedBlocks").size());
            assertTrue(fixture.getListOrEmpty("TileEntities").isEmpty());

            assertTrue(LegacyPaddedBenchMigration.migrateStacksInNbt(fixture, null));
            for (CompoundTag stack : fixture.getListOrEmpty("PlayerInventory").compoundStream().toList()) {
                assertTrue(stack.getStringOr("id", "").contains("_padded_red_single_"));
                assertFalse(stack.getCompoundOrEmpty("tag").contains("Color"));
            }
            assertFalse(LegacyPaddedBenchMigration.migrateStacksInNbt(fixture, null));
        }
    }

    @Test
    void multicolourFixturesRetainAllColoursAcrossNestedStorage() throws IOException {
        for (String version : VERSIONS) {
            CompoundTag fixture = read(version, "iaf-" + version + "-multicolour.nbt");
            ListTag inventory = fixture.getListOrEmpty("PlayerInventory");
            assertEquals(512, inventory.size());
            assertEquals(512, fixture.getListOrEmpty("PlacedBlocks").size());
            assertEquals(512, fixture.getListOrEmpty("TileEntities").size());

            assertTrue(LegacyPaddedBenchMigration.migrateStacksInNbt(fixture, null));
            for (CompoundTag stack : inventory.compoundStream().toList()) {
                String id = stack.getStringOr("id", "");
                int colourMatches = 0;
                for (String colour : LegacyPaddedBenchMappings.COLORS) {
                    if (id.contains("_padded_" + colour + "_single_")) colourMatches++;
                }
                assertEquals(1, colourMatches, id);
                assertFalse(stack.getCompoundOrEmpty("tag").contains("Color"));
            }

            CompoundTag nested = fixture.getListOrEmpty("NestedContainers").getCompoundOrEmpty(0)
                    .getCompoundOrEmpty("tag").getCompoundOrEmpty("BlockEntityTag");
            assertEquals(512, nested.getListOrEmpty("Items").size());
            assertFalse(LegacyPaddedBenchMigration.migrateStacksInNbt(fixture, null));
        }
    }

    private static CompoundTag read(String version, String name) throws IOException {
        String resource = "/fixtures/" + version + "/" + name;
        try (InputStream input = LegacyMigrationFixtureTest.class.getResourceAsStream(resource)) {
            if (input == null) throw new IOException("Missing fixture " + resource);
            return NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
        }
    }
}
