package zone.moddev.mc.iafbopaddon.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import zone.moddev.mc.iafbopaddon.IAFBiomesOPlentyAddon;

/** Persistent aggregate statistics for the idempotent legacy compatibility migration. */
public final class MigrationWorldState extends SavedData {
    public static final int MIGRATION_VERSION = 1;
    private static final SavedDataType<MigrationWorldState> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(IAFBiomesOPlentyAddon.MOD_ID, "legacy_padded_bench_migration"),
            MigrationWorldState::new,
            CompoundTag.CODEC.xmap(MigrationWorldState::new, MigrationWorldState::saveTag),
            DataFixTypes.SAVED_DATA_MAP_DATA);

    private long migratedChunks;
    private long migratedBlocks;
    private long migratedItems;

    public MigrationWorldState() {
    }

    private MigrationWorldState(CompoundTag tag) {
        migratedChunks = tag.getLongOr("migrated_chunks", 0L);
        migratedBlocks = tag.getLongOr("migrated_blocks", 0L);
        migratedItems = tag.getLongOr("migrated_items", 0L);
    }

    public static MigrationWorldState get(Level level) {
        if (!level.isClientSide() && level.dimension() != Level.OVERWORLD && level.getServer() != null) {
            ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) level = overworld;
        }
        if (!(level instanceof ServerLevel serverLevel)) return new MigrationWorldState();
        return serverLevel.getDataStorage().computeIfAbsent(TYPE);
    }

    public void recordChunk() {
        ++migratedChunks;
        setDirty();
    }

    public void recordBlocks(long count) {
        if (count > 0L) {
            migratedBlocks += count;
            setDirty();
        }
    }

    public void recordItems(long count) {
        if (count > 0L) {
            migratedItems += count;
            setDirty();
        }
    }

    public long migratedChunks() {
        return migratedChunks;
    }

    public long migratedBlocks() {
        return migratedBlocks;
    }

    public long migratedItems() {
        return migratedItems;
    }

    private CompoundTag saveTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("migration_version", MIGRATION_VERSION);
        tag.putLong("migrated_chunks", migratedChunks);
        tag.putLong("migrated_blocks", migratedBlocks);
        tag.putLong("migrated_items", migratedItems);
        return tag;
    }
}
