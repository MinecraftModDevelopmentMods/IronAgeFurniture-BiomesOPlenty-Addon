package zone.moddev.mc.iafbopaddon.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Proves the add-on retains every registry ID from the integrated 26.3 core candidate. */
class LegacyIntegratedCoreRegistryContractTest {
    private static final String BASELINE_COMMIT = "f8548258f";
    private static final String BASELINE_IDS_SHA256 =
            "0bf429380fba461be6d5d9ceac8a44ea52dcfcfe34360deda1f332cd652d8548";
    private static final Pattern REGISTRATION = Pattern.compile("register\\(\"([^\"]+)\"");

    @Test
    void retainsTheExactIntegratedCoreFurnitureRegistry() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/zone/moddev/mc/iafbopaddon/init/ModBOPBlocks.java"));
        List<String> ids = REGISTRATION.matcher(source).results()
                .map(result -> result.group(1)).distinct().sorted().toList();
        assertEquals(546, ids.size(), "registration count from " + BASELINE_COMMIT);
        byte[] bytes = (String.join("\n", ids) + "\n").getBytes(StandardCharsets.UTF_8);
        assertEquals(BASELINE_IDS_SHA256,
                HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)),
                "registry IDs differ from integrated-core baseline " + BASELINE_COMMIT);
    }
}
