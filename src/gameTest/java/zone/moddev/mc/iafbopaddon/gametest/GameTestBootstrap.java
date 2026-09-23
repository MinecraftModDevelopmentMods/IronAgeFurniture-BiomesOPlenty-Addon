package zone.moddev.mc.iafbopaddon.gametest;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.gametest.ForgeGameTestHooks.TestReference;
import net.minecraftforge.registries.RegisterEvent;
import zone.moddev.mc.iafbopaddon.compat.LegacyPaddedBenchMigrationGameTests;
import zone.moddev.mc.iafbopaddon.IAFBiomesOPlentyAddon;

/** Registers test-only functions without including them in the release jar. */
public final class GameTestBootstrap {
    private static final Map<Identifier, TestReference> TESTS = Map.copyOf(
            ForgeGameTestHooks.gatherTests(LegacyPaddedBenchMigrationGameTests.class, null));

    private GameTestBootstrap() {
    }

    public static void register(BusGroup modBusGroup) {
        IAFBiomesOPlentyAddon.LOGGER.info("Registering {} add-on GameTests: {}",
                TESTS.size(), TESTS.keySet());
        RegisterEvent.getBus(modBusGroup).addListener(GameTestBootstrap::registerTestFunctions);
    }

    private static void registerTestFunctions(RegisterEvent event) {
        if (event.getRegistryKey() != Registries.TEST_FUNCTION) return;
        TESTS.forEach((id, reference) ->
                event.register(Registries.TEST_FUNCTION, id, reference::consumer));
    }
}
