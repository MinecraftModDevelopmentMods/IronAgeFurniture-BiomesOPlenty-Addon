package zone.moddev.mc.iafbopaddon.mixin;

import java.util.List;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Access to the native 26.2 constructor that retains the selected bundle item. */
@Mixin(BundleContents.class)
public interface BundleContentsInvoker {
    @Invoker("<init>")
    static BundleContents iafbopaddon$create(List<ItemStackTemplate> items, int selectedItem) {
        throw new AssertionError("Bundle constructor invoker was not transformed");
    }
}
