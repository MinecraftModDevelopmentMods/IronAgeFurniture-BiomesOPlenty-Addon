package zone.moddev.mc.iafbopaddon.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Reads the stable colour payload written by the 1.10 and 1.12 candidates. */
public final class LegacyPaddedBenchBlockEntity extends BlockEntity {
    public static final String COLOR_TAG = "Color";
    private String color = "red";

    public LegacyPaddedBenchBlockEntity(BlockPos pos, BlockState state) {
        super(LegacyPaddedBenchRegistry.PADDED_BENCH_COLOUR.get(), pos, state);
    }

    public String color() {
        return LegacyPaddedBenchMappings.validColor(color) ? color : "red";
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        color = input.getStringOr(COLOR_TAG, "red");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString(COLOR_TAG, color());
    }
}
