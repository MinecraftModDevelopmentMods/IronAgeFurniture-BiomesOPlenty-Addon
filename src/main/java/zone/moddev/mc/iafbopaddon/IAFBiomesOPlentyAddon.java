package zone.moddev.mc.iafbopaddon;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import zone.moddev.mc.iafbopaddon.init.ModBOPBlocks;
import zone.moddev.mc.iafbopaddon.compat.LegacyPaddedBenchMigration;
import zone.moddev.mc.iafbopaddon.compat.LegacyPaddedBenchRegistry;
import zone.moddev.mc.ironagefurniture.init.ModCreativeTab;

/**
 * Loader identity for the Biomes O' Plenty add-on. Furniture remains in the
 * historical {@code ironagefurniture} registry and resource namespace so
 * existing worlds retain their persistent identities.
 */
@Mod(IAFBiomesOPlentyAddon.MOD_ID)
public final class IAFBiomesOPlentyAddon {
    public static final String MOD_ID = "iafbopaddon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IAFBiomesOPlentyAddon(FMLJavaModLoadingContext context) {
        BusGroup modBusGroup = context.getModBusGroup();
        ModBOPBlocks.REGISTER.register(modBusGroup);
        ModBOPBlocks.ITEMS.register(modBusGroup);
        LegacyPaddedBenchRegistry.register(modBusGroup);
        BuildCreativeModeTabContentsEvent.BUS.addListener(this::populateCreativeTab);
        LegacyPaddedBenchMigration.register();
        LOGGER.info("Iron Age Furniture Biomes O' Plenty Add-on is loading");
    }

    private void populateCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ModCreativeTab.FURNITURE.getKey())) {
            ModBOPBlocks.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
