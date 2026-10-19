package zone.moddev.mc.iafbopaddon.mixin;

import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Preserves native slot templates, including holes and malformed saved payloads. */
@Mixin(ItemContainerContents.class)
public interface ItemContainerContentsAccess {
    @Accessor("items")
    List<Optional<ItemStackTemplate>> iafbopaddon$items();

    @Invoker("<init>")
    static ItemContainerContents iafbopaddon$create(List<Optional<ItemStackTemplate>> items) {
        throw new AssertionError("Container constructor invoker was not transformed");
    }
}
