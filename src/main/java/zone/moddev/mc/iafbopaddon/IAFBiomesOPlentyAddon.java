package zone.moddev.mc.iafbopaddon;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
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

    public IAFBiomesOPlentyAddon(IEventBus modBus, ModContainer container) {
        ModBOPBlocks.REGISTER.register(modBus);
        ModBOPBlocks.ITEMS.register(modBus);
        LegacyPaddedBenchRegistry.register(modBus);
        modBus.addListener(this::populateCreativeTab);
        LegacyPaddedBenchMigration.register();
        LOGGER.info("Iron Age Furniture Biomes O' Plenty Add-on is loading");
    }

    private void populateCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ModCreativeTab.FURNITURE.getKey())) {
            ModBOPBlocks.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
