package zone.moddev.mc.iafbopaddon.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import zone.moddev.mc.ironagefurniture.api.blocks.furniture.BackBench;

/** Itemless compatibility block for an old padded back-bench registry ID. */
final class LegacyPaddedBackBench extends BackBench implements EntityBlock {
    LegacyPaddedBackBench(String name) {
        super(1, 10, SoundType.WOOD, name);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LegacyPaddedBenchBlockEntity(pos, state);
    }
}
