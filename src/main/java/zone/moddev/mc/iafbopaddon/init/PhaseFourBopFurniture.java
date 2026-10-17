package zone.moddev.mc.iafbopaddon.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import zone.moddev.mc.ironagefurniture.init.PhaseFourFurniture;

/** The add-on owns BOP registrations; the shared core supplies their behaviour. */
public final class PhaseFourBopFurniture {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, "ironagefurniture");
    public static final String[] WOODS = {"dead", "fir", "hellbark", "jacaranda", "magic", "mahogany", "maple",
            "origin_oak", "palm", "pine", "redwood", "umbran", "willow", "empyreal"};
    static {
        for (String wood : WOODS) PhaseFourFurniture.registerWood("biomesoplenty_" + wood, REGISTER, ModBOPBlocks.ITEMS);
    }
    private PhaseFourBopFurniture() { }
}
