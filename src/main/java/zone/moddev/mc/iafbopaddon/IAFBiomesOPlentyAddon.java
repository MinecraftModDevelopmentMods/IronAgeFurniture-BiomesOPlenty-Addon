package zone.moddev.mc.iafbopaddon;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
        BusGroup modBus = context.getModBusGroup();
        ModBOPBlocks.REGISTER.register(modBus);
        zone.moddev.mc.iafbopaddon.init.PhaseFourBopFurniture.REGISTER.register(modBus);
        ModBOPBlocks.ITEMS.register(modBus);
        LegacyPaddedBenchRegistry.register(modBus);
        BuildCreativeModeTabContentsEvent.BUS.addListener(this::populateCreativeTab);
        LegacyPaddedBenchMigration.register();
        registerGameTests(modBus);
        LOGGER.info("Iron Age Furniture Biomes O' Plenty Add-on is loading");
    }

    private static void registerGameTests(BusGroup bus) {
        try {
            Class<?> bootstrap = Class.forName("zone.moddev.mc.iafbopaddon.gametest.GameTestBootstrap");
            bootstrap.getMethod("register", BusGroup.class).invoke(null, bus);
        } catch (ClassNotFoundException absent) {
            if (java.util.Arrays.asList(System.getProperty("forge.enabledGameTestNamespaces", "").split(",")).contains(MOD_ID))
                throw new IllegalStateException("GameTest source set is missing from the development run", absent);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Could not register add-on GameTests", failure);
        }
    }

    private void populateCreativeTab(BuildCreativeModeTabContentsEvent event) {
        ModBOPBlocks.ITEMS.getEntries().forEach(holder -> {
            var item = holder.get();
            if (ModCreativeTab.visible(item) && event.getTabKey().equals(ModCreativeTab.keyFor(item))) event.accept(item);
        });
    }

}
