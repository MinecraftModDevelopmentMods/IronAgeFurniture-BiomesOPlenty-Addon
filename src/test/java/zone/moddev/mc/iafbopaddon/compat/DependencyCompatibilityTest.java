package zone.moddev.mc.iafbopaddon.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Test;

/** Keeps the stable BOP release installable without admitting untested older builds. */
class DependencyCompatibilityTest {
    @Test
    void acceptsStableBopAndNewerBuildsForTheSameMinecraftVersion() throws Exception {
        VersionRange range = bopRange();
        for (String version : List.of("26.3.0.0.7", "26.3.0.0.15", "26.3.0.0.99")) {
            assertTrue(range.containsVersion(new DefaultArtifactVersion(version)), version);
        }
    }

    @Test
    void rejectsEarlierBuildsAndTheNextMinecraftVersion() throws Exception {
        VersionRange range = bopRange();
        for (String version : List.of("26.3.0.0.2", "26.3.0.0.6", "26.4.0.0.1")) {
            assertFalse(range.containsVersion(new DefaultArtifactVersion(version)), version);
        }
    }

    @Test
    void retainsTheTestedCoreAndSupportingLibraries() throws IOException {
        Properties properties = properties();
        assertEquals("[0.4.0.2603001]", properties.getProperty("ironagefurniture_version_range"));
        assertEquals("[26.3.0.0.5,26.4)", properties.getProperty("glitchcore_version_range"));
        assertEquals("[26.3.0.0.9,26.4)", properties.getProperty("terrablender_version_range"));
    }

    private static VersionRange bopRange() throws IOException, InvalidVersionSpecificationException {
        return VersionRange.createFromVersionSpec(properties().getProperty("biomesoplenty_version_range"));
    }

    private static Properties properties() throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of("gradle.properties"))) {
            properties.load(reader);
        }
        return properties;
    }
}
