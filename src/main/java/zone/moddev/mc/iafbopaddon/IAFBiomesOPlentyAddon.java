package zone.moddev.mc.iafbopaddon;

import com.mojang.logging.LogUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
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
        registerGameTests(modBusGroup);
        LOGGER.info("Iron Age Furniture Biomes O' Plenty Add-on is loading");
    }

    private static void registerGameTests(BusGroup modBusGroup) {
        try {
            Class<?> bootstrap = Class.forName(
                    "zone.moddev.mc.iafbopaddon.gametest.GameTestBootstrap");
            bootstrap.getMethod("register", BusGroup.class).invoke(null, modBusGroup);
        } catch (ClassNotFoundException exception) {
            String enabledNamespaces = System.getProperty("forge.enabledGameTestNamespaces", "");
            if (Arrays.stream(enabledNamespaces.split(","))
                    .map(String::trim)
                    .anyMatch(MOD_ID::equals)) {
                throw new IllegalStateException(
                        "GameTest source set is missing from the development run", exception);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Could not register add-on GameTests", exception);
        }
    }

    private void populateCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ModCreativeTab.FURNITURE.getKey())) {
            ModBOPBlocks.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
