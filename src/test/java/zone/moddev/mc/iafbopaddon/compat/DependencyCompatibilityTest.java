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

/** Keeps compatible updates installable without admitting older or next-series dependencies. */
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
    void acceptsTheCoreBaselineAndCompatiblePhaseFourUpdates() throws Exception {
        VersionRange range = range("ironagefurniture_version_range");
        for (String version : List.of("0.4.1.2603002", "0.4.2.2603002", "0.4.12.2603002")) {
            assertTrue(range.containsVersion(new DefaultArtifactVersion(version)), version);
        }
        assertFalse(VersionRange.createFromVersionSpec("[0.4.1.2603002]")
                .containsVersion(new DefaultArtifactVersion("0.4.2.2603002")),
                "An exact build pin must not be reused as the runtime compatibility range");
    }

    @Test
    void rejectsOlderCoreApisAndTheNextFeatureSeries() throws Exception {
        VersionRange range = range("ironagefurniture_version_range");
        for (String version : List.of("0.3.0.2603002", "0.4.0.2603002", "0.4.1.2603001",
                "0.5.0.2603002", "1.0.0.2603002")) {
            assertFalse(range.containsVersion(new DefaultArtifactVersion(version)), version);
        }
    }

    @Test
    void keepsTheSupportedNeoForgeSeriesWhileUpdatingTheBuildApi() throws Exception {
        Properties properties = properties();
        assertEquals("26.3.0.99", properties.getProperty("neo_version"));
        VersionRange range = range("neo_version_range");
        for (String version : List.of("26.3.0.77", "26.3.0.99")) {
            assertTrue(range.containsVersion(new DefaultArtifactVersion(version)), version);
        }
        for (String version : List.of("26.3.0.76", "26.3.1", "26.4.0.1")) {
            assertFalse(range.containsVersion(new DefaultArtifactVersion(version)), version);
        }
    }

    @Test
    void retainsTheVerifiedCompileBaselineAndSupportingLibraries() throws IOException {
        Properties properties = properties();
        assertEquals("[0.4.1.2603002,0.5)", properties.getProperty("ironagefurniture_version_range"));
        assertEquals("0.4.1.2603002", properties.getProperty("ironagefurniture_version"));
        assertEquals("6e709746b60749de35d45233da0ee01fbba5013c389361f9e2edfc8de8eb87df",
                properties.getProperty("ironagefurniture_sha256"));
        assertEquals("[26.3.0.0.5,26.4)", properties.getProperty("glitchcore_version_range"));
        assertEquals("[26.3.0.0.9,26.4)", properties.getProperty("terrablender_version_range"));
    }

    private static VersionRange bopRange() throws IOException, InvalidVersionSpecificationException {
        return range("biomesoplenty_version_range");
    }

    private static VersionRange range(String property) throws IOException, InvalidVersionSpecificationException {
        return VersionRange.createFromVersionSpec(properties().getProperty(property));
    }

    private static Properties properties() throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of("gradle.properties"))) {
            properties.load(reader);
        }
        return properties;
    }
}
