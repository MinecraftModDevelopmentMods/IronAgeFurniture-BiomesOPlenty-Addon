package zone.moddev.mc.iafbopaddon.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import zone.moddev.mc.ironagefurniture.api.blocks.furniture.Bench;

/** Itemless compatibility block for an old padded bench registry ID. */
final class LegacyPaddedBench extends Bench implements EntityBlock {
    LegacyPaddedBench(String name) {
        super(1, 10, SoundType.WOOD, name);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LegacyPaddedBenchBlockEntity(pos, state);
    }
}
