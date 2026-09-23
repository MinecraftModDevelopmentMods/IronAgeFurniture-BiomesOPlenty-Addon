package zone.moddev.mc.iafbopaddon.compat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.Identifier;

/** Pure legacy-ID and colour mapping used by registry and runtime migration. */
public final class LegacyPaddedBenchMappings {
    public static final String NAMESPACE = "ironagefurniture";
    public static final List<String> COLORS = List.of(
            "red", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "white", "black");
    public static final List<String> LEGACY_BOP_WOODS = List.of(
            "cherry", "ebony", "ethereal", "eucalyptus", "fir", "hellbark", "jacaranda",
            "magic", "mahogany", "mangrove", "palm", "pine", "redwood", "sacred_oak",
            "umbran", "willow");
    private static final Set<String> COLORS_SET = Set.copyOf(COLORS);
    private static final Map<String, TargetWood> RETIRED_BOP_WOODS = Map.of(
            "ebony", new TargetWood("hellbark", true),
            "ethereal", new TargetWood("warped", false),
            "eucalyptus", new TargetWood("origin_oak", true),
            "mangrove", new TargetWood("pale_oak", false),
            "sacred_oak", new TargetWood("origin_oak", true));
    private static final Map<String, TargetWood> TARGET_WOODS = Map.ofEntries(
            Map.entry("cherry", new TargetWood("cherry", false)),
            Map.entry("ebony", new TargetWood("hellbark", true)),
            Map.entry("ethereal", new TargetWood("warped", false)),
            Map.entry("eucalyptus", new TargetWood("origin_oak", true)),
            Map.entry("mangrove", new TargetWood("pale_oak", false)),
            Map.entry("sacred_oak", new TargetWood("origin_oak", true)),
            Map.entry("fir", new TargetWood("fir", true)),
            Map.entry("hellbark", new TargetWood("hellbark", true)),
            Map.entry("jacaranda", new TargetWood("jacaranda", true)),
            Map.entry("magic", new TargetWood("magic", true)),
            Map.entry("mahogany", new TargetWood("mahogany", true)),
            Map.entry("palm", new TargetWood("palm", true)),
            Map.entry("pine", new TargetWood("pine", true)),
            Map.entry("redwood", new TargetWood("redwood", true)),
            Map.entry("umbran", new TargetWood("umbran", true)),
            Map.entry("willow", new TargetWood("willow", true)));

    public static boolean validColor(String color) {
        return COLORS_SET.contains(color);
    }

    public static String normalizedColor(String color) {
        return validColor(color) ? color : "red";
    }

    public static Identifier compatibilityBlockId(String wood, boolean back) {
        if ("cherry".equals(wood)) {
            return id(prefix(back) + "single_cherry");
        }
        return id(prefix(back) + "single_biomesoplenty_" + wood);
    }

    public static Identifier originalLegacyId(String wood, boolean back) {
        return id(prefix(back) + "single_biomesoplenty_" + wood);
    }

    public static Identifier modernId(String wood, boolean back, String color) {
        TargetWood target = TARGET_WOODS.get(wood);
        if (target == null) return null;
        String suffix = target.biomesOPlenty()
                ? "_biomesoplenty_" + target.wood() : "_" + target.wood();
        return id(prefix(back) + normalizedColor(color) + "_single" + suffix);
    }

    public static Identifier recolorModernRed(Identifier current, String color) {
        if (current == null || !NAMESPACE.equals(current.getNamespace()) || !validColor(color)) return null;
        String path = current.getPath();
        String regular = "chair_wood_ironage_bench_padded_red_single_";
        String back = "chair_wood_ironage_bench_back_padded_red_single_";
        if (path.startsWith(back)) return id(path.replaceFirst("_red_single_", "_" + color + "_single_"));
        if (path.startsWith(regular)) return id(path.replaceFirst("_red_single_", "_" + color + "_single_"));
        return null;
    }

    /**
     * Maps any furniture form made from a retired BOP wood to its documented
     * visual fallback. Cherry is deliberately left to the parent mod, which
     * owns the complete BOP-cherry-to-vanilla-cherry mapping.
     */
    public static Identifier retiredFurnitureTarget(Identifier legacyId) {
        if (legacyId == null || !NAMESPACE.equals(legacyId.getNamespace())) return null;
        String path = legacyId.getPath();
        for (Map.Entry<String, TargetWood> entry : RETIRED_BOP_WOODS.entrySet()) {
            String suffix = "_biomesoplenty_" + entry.getKey();
            if (!path.endsWith(suffix)) continue;
            TargetWood target = entry.getValue();
            String targetSuffix = target.biomesOPlenty()
                    ? "_biomesoplenty_" + target.wood() : "_" + target.wood();
            return id(path.substring(0, path.length() - suffix.length()) + targetSuffix);
        }
        return null;
    }

    public static LegacyId parseLegacy(Identifier id) {
        if (id == null || !NAMESPACE.equals(id.getNamespace())) return null;
        String path = id.getPath();
        for (String wood : LEGACY_BOP_WOODS) {
            for (boolean back : new boolean[] { false, true }) {
                if (originalLegacyId(wood, back).getPath().equals(path)) return new LegacyId(wood, back);
            }
        }
        for (boolean back : new boolean[] { false, true }) {
            if (compatibilityBlockId("cherry", back).getPath().equals(path)) {
                return new LegacyId("cherry", back);
            }
        }
        return null;
    }

    private static String prefix(boolean back) {
        return back ? "chair_wood_ironage_bench_back_padded_" : "chair_wood_ironage_bench_padded_";
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, path);
    }

    public record LegacyId(String wood, boolean back) {
    }

    private record TargetWood(String wood, boolean biomesOPlenty) {
    }

    private LegacyPaddedBenchMappings() {
    }
}
