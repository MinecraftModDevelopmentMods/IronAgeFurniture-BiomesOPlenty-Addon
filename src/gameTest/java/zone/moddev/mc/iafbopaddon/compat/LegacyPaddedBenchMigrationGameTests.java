package zone.moddev.mc.iafbopaddon.compat;

import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.gametest.GameTestPrefix;
import zone.moddev.mc.iafbopaddon.IAFBiomesOPlentyAddon;
import zone.moddev.mc.iafbopaddon.init.ModBOPBlocks;
import zone.moddev.mc.ironagefurniture.api.blocks.base.FurnitureBlock;
import zone.moddev.mc.ironagefurniture.api.blocks.furniture.BackBench;
import zone.moddev.mc.ironagefurniture.api.enumerations.BenchType;

/** Runtime proof for placed legacy padded-bench migration. */
@GameTestNamespace(IAFBiomesOPlentyAddon.MOD_ID)
@GameTestPrefix("migration")
public final class LegacyPaddedBenchMigrationGameTests {
    private static final String INTEGRATED_CORE_IDS_SHA256 =
            "0bf429380fba461be6d5d9ceac8a44ea52dcfcfe34360deda1f332cd652d8548";
    private static final List<Direction> FACINGS = List.of(
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    private static final List<BenchType> JOINS = List.of(
            BenchType.SINGLE, BenchType.LEFT, BenchType.MIDDLE, BenchType.RIGHT);

    private LegacyPaddedBenchMigrationGameTests() {
    }

    @GameTest(structure = "forge:empty8x4x8", maxTicks = 200)
    public static void placedLegacyBenchesPreserveColourAndState(GameTestHelper helper) {
        List<Expected> expected = new ArrayList<>();
        Set<LevelChunk> chunks = new LinkedHashSet<>();
        int index = 0;
        for (String wood : LegacyPaddedBenchMappings.LEGACY_BOP_WOODS) {
            for (boolean back : List.of(false, true)) {
                String color = LegacyPaddedBenchMappings.COLORS.get(index % 16);
                addLegacyBlock(helper, expected, chunks, index++, wood, back, color);
            }
        }
        addLegacyBlock(helper, expected, chunks, index++, "fir", false, null);
        addLegacyBlock(helper, expected, chunks, index, "sacred_oak", true, "not_a_colour");

        long converted = chunks.stream()
                .mapToLong(LegacyPaddedBenchMigration::migrateChunkBlocks).sum();
        require(helper, converted == expected.size(),
                "expected " + expected.size() + " converted blocks, found " + converted);

        for (Expected entry : expected) {
            BlockState actual = helper.getLevel().getBlockState(entry.pos());
            Identifier actualId = BuiltInRegistries.BLOCK.getKey(actual.getBlock());
            require(helper, entry.targetId().equals(actualId),
                    "wrong target at " + entry.pos() + ": " + actualId);
            require(helper, actual.getValue(FurnitureBlock.DIRECTION) == entry.facing(),
                    "facing changed at " + entry.pos());
            require(helper, actual.getValue(BackBench.TYPE) == entry.join(),
                    "bench join changed at " + entry.pos());
            require(helper, actual.getValue(FurnitureBlock.WATERLOGGED) == entry.waterlogged(),
                    "waterlogging changed at " + entry.pos());
            require(helper, helper.getLevel().getBlockEntity(entry.pos()) == null,
                    "legacy colour block entity remained at " + entry.pos());
        }

        long secondPass = chunks.stream()
                .mapToLong(LegacyPaddedBenchMigration::migrateChunkBlocks).sum();
        require(helper, secondPass == 0L,
                "migration was not idempotent; second pass converted " + secondPass);
        helper.succeed();
    }

    @GameTest(structure = "forge:empty8x4x8")
    public static void nestedRuntimeStacksPreserveDataAndColour(GameTestHelper helper) {
        Identifier redId = LegacyPaddedBenchMappings.modernId("willow", true, "red");
        ItemStack legacy = new ItemStack(BuiltInRegistries.ITEM.getValue(redId), 5);
        CompoundTag custom = new CompoundTag();
        custom.putString(LegacyPaddedBenchBlockEntity.COLOR_TAG, "cyan");
        custom.putString("Preserved", "yes");
        legacy.set(DataComponents.CUSTOM_DATA, CustomData.of(custom));

        ItemStack container = new ItemStack(Items.CHEST);
        container.set(DataComponents.CONTAINER,
                ItemContainerContents.EMPTY.copyWithContents(List.of(legacy).stream()));
        LegacyPaddedBenchMigration.StackResult result =
                LegacyPaddedBenchMigration.migrateStack(container, null);
        require(helper, result.changed(), "component-backed nested stack was not migrated");
        ItemStack migrated = result.stack().get(DataComponents.CONTAINER)
                .itemCopies().findFirst().orElse(ItemStack.EMPTY);
        require(helper, LegacyPaddedBenchMappings.modernId("willow", true, "cyan").equals(
                        BuiltInRegistries.ITEM.getKey(migrated.getItem())),
                "nested stack kept the wrong colour");
        require(helper, migrated.getCount() == 5, "nested stack count changed");
        CompoundTag migratedData = migrated.getOrDefault(
                DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        require(helper, !migratedData.contains(LegacyPaddedBenchBlockEntity.COLOR_TAG),
                "legacy Color data remained on nested stack");
        require(helper, "yes".equals(migratedData.getStringOr("Preserved", "")),
                "unrelated nested custom data was lost");
        require(helper, !LegacyPaddedBenchMigration.migrateStack(result.stack(), null).changed(),
                "nested stack migration was not idempotent");
        helper.succeed();
    }

    @GameTest(structure = "forge:empty8x4x8")
    public static void integratedCoreRegistryContractIsPresentAtRuntime(GameTestHelper helper) {
        List<Identifier> blockIds = ModBOPBlocks.REGISTER.getEntries().stream()
                .map(entry -> entry.getId()).sorted().toList();
        List<Identifier> itemIds = ModBOPBlocks.ITEMS.getEntries().stream()
                .map(entry -> entry.getId()).sorted().toList();
        require(helper, blockIds.size() == 546, "runtime block registry count changed");
        require(helper, blockIds.equals(itemIds), "runtime block and item IDs differ");
        require(helper, INTEGRATED_CORE_IDS_SHA256.equals(registryHash(blockIds)),
                "runtime registry IDs differ from the integrated-core baseline");

        List<String> forms = new ArrayList<>(List.of(
                "chair_wood_ironage_classic",
                "chair_wood_ironage_shield",
                "chair_wood_ironage_stool_short",
                "chair_wood_ironage_stool_tall",
                "chair_wood_ironage_bench_single",
                "chair_wood_ironage_bench_back_single",
                "chair_wood_ironage_bench_log_single"));
        for (String color : LegacyPaddedBenchMappings.COLORS) {
            forms.add("chair_wood_ironage_bench_padded_" + color + "_single");
            forms.add("chair_wood_ironage_bench_back_padded_" + color + "_single");
        }
        for (String oldWood : List.of("ebony", "ethereal", "eucalyptus", "mangrove", "sacred_oak")) {
            for (String form : forms) {
                Identifier oldId = Identifier.fromNamespaceAndPath(
                        LegacyPaddedBenchMappings.NAMESPACE,
                        form + "_biomesoplenty_" + oldWood);
                Identifier target = LegacyPaddedBenchMappings.retiredFurnitureTarget(oldId);
                require(helper, target != null
                                && BuiltInRegistries.BLOCK.containsKey(target)
                                && BuiltInRegistries.ITEM.containsKey(target),
                        "missing retired-wood target for " + oldId + ": " + target);
            }
        }
        helper.succeed();
    }

    @GameTest(structure = "forge:empty8x4x8", maxTicks = 200)
    public static void convertedBlocksRemainStableAfterChunkSave(GameTestHelper helper) {
        BlockPos origin = new BlockPos(1025, 80, 1025);
        Set<LevelChunk> chunks = new LinkedHashSet<>();
        List<Expected> expected = new ArrayList<>();
        addLegacyBlockAt(helper, expected, chunks, origin.offset(0, 0, 0),
                0, "ebony", false, "purple");
        addLegacyBlockAt(helper, expected, chunks, origin.offset(1, 0, 0),
                1, "fir", true, null);
        addLegacyBlockAt(helper, expected, chunks, origin.offset(2, 0, 0),
                2, "sacred_oak", true, "not_a_colour");
        long converted = chunks.stream()
                .mapToLong(LegacyPaddedBenchMigration::migrateChunkBlocks).sum();
        require(helper, converted == expected.size(),
                "save probe did not migrate every seeded block");
        chunks.forEach(LevelChunk::markUnsaved);
        helper.getLevel().getChunkSource().save(true);
        verifyExpected(helper, expected);

        long afterSaveChanges = chunks.stream()
                .mapToLong(LegacyPaddedBenchMigration::migrateChunkBlocks).sum();
        require(helper, afterSaveChanges == 0L,
                "saved chunks repeated " + afterSaveChanges + " migrations");
        helper.succeed();
    }

    private static void addLegacyBlock(GameTestHelper helper, List<Expected> expected,
            Set<LevelChunk> chunks, int index, String wood, boolean back, String color) {
        BlockPos pos = helper.absolutePos(new BlockPos(1 + index % 6, 2, 1 + index / 6));
        addLegacyBlockAt(helper, expected, chunks, pos, index, wood, back, color);
    }

    private static void addLegacyBlockAt(GameTestHelper helper, List<Expected> expected,
            Set<LevelChunk> chunks, BlockPos pos, int index, String wood,
            boolean back, String color) {
        Identifier legacyId = LegacyPaddedBenchMappings.compatibilityBlockId(wood, back);
        Block legacyBlock = BuiltInRegistries.BLOCK.getValue(legacyId);
        require(helper, legacyBlock != null && LegacyPaddedBenchRegistry.isCompatibilityBlock(legacyBlock),
                "missing compatibility block " + legacyId);

        Direction facing = FACINGS.get(index % FACINGS.size());
        BenchType join = JOINS.get(index % JOINS.size());
        boolean waterlogged = (index & 1) == 0;
        BlockState legacyState = legacyBlock.defaultBlockState()
                .setValue(FurnitureBlock.DIRECTION, facing)
                .setValue(BackBench.TYPE, join)
                .setValue(FurnitureBlock.WATERLOGGED, waterlogged);
        LevelChunk chunk = helper.getLevel().getChunkAt(pos);
        ChunkMigrationAccess.setBlockState(chunk, pos, legacyState);
        LegacyPaddedBenchBlockEntity blockEntity =
                new LegacyPaddedBenchBlockEntity(pos, legacyState);
        chunk.setBlockEntity(blockEntity);
        if (color != null) loadColour(helper, blockEntity, color);
        chunks.add(chunk);

        String normalized = LegacyPaddedBenchMappings.normalizedColor(
                color == null ? "red" : color);
        expected.add(expectedAt(pos, index, wood, back, normalized));
    }

    private static Expected expectedAt(BlockPos pos, int index, String wood,
            boolean back, String color) {
        String normalized = LegacyPaddedBenchMappings.normalizedColor(
                color == null ? "red" : color);
        return new Expected(pos.immutable(),
                LegacyPaddedBenchMappings.modernId(wood, back, normalized),
                FACINGS.get(index % FACINGS.size()), JOINS.get(index % JOINS.size()),
                (index & 1) == 0);
    }

    private static void verifyExpected(GameTestHelper helper, List<Expected> expected) {
        for (Expected entry : expected) {
            BlockState actual = helper.getLevel().getBlockState(entry.pos());
            require(helper, entry.targetId().equals(BuiltInRegistries.BLOCK.getKey(actual.getBlock())),
                    "persisted target changed at " + entry.pos());
            require(helper, actual.getValue(FurnitureBlock.DIRECTION) == entry.facing(),
                    "persisted facing changed at " + entry.pos());
            require(helper, actual.getValue(BackBench.TYPE) == entry.join(),
                    "persisted bench join changed at " + entry.pos());
            require(helper, actual.getValue(FurnitureBlock.WATERLOGGED) == entry.waterlogged(),
                    "persisted waterlogging changed at " + entry.pos());
            require(helper, helper.getLevel().getBlockEntity(entry.pos()) == null,
                    "persisted compatibility block entity remained at " + entry.pos());
        }
    }

    private static void loadColour(GameTestHelper helper,
            LegacyPaddedBenchBlockEntity blockEntity, String color) {
        CompoundTag data = new CompoundTag();
        data.putString(LegacyPaddedBenchBlockEntity.COLOR_TAG, color);
        try (ProblemReporter.ScopedCollector problems = new ProblemReporter.ScopedCollector(
                blockEntity.problemPath(), LogUtils.getLogger())) {
            blockEntity.loadWithComponents(TagValueInput.create(
                    problems, helper.getLevel().registryAccess(), data));
        }
    }

    private static void require(GameTestHelper helper, boolean condition, String message) {
        if (!condition) helper.fail(message);
    }

    private static String registryHash(List<Identifier> ids) {
        try {
            byte[] bytes = (String.join("\n", ids.stream().map(Identifier::getPath).toList())
                    + "\n").getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private record Expected(BlockPos pos, Identifier targetId, Direction facing,
            BenchType join, boolean waterlogged) {
    }
}
