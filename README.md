# Iron Age Furniture: Biomes O' Plenty Add-on

Bring the woods of Biomes O' Plenty into Iron Age Furniture. This add-on lets you craft the familiar Iron Age Furniture range in every supported Biomes O' Plenty 26.3 wood type.

## What this add-on includes

- The original 546 furniture items across 14 Biomes O' Plenty wood families, with their saved identities preserved
- Wingback chairs, thrones, wooden beds and canopy beds, including doubles, in all sixteen colours
- Shield chairs that preserve the complete shield item, including its name, damage, enchantments and banner design
- Recipes and recipe-book unlocks for every included item
- Compatibility support for older Iron Age Furniture worlds

The add-on uses Biomes O' Plenty's installed textures, so resource packs that replace those wood textures can also change the matching furniture. No Biomes O' Plenty assets are included in this project.

Furniture appears alongside the core mod's Chairs, Benches and Beds tabs. The core supplies lighting through its separate Lights tab; the add-on does not register another set of lights.

## Crafting the new furniture

- A vanilla bed and a matching plank make a wooden bed of the same colour.
- A wooden bed, matching plank and matching carpet make a canopy bed.
- Two identical single beds make the matching double bed.
- A classic chair, matching plank and carpet make a wingback chair.
- A wingback chair, matching plank and matching carpet make a throne.

Chair upgrades are shapeless. Beds can still be recoloured with carpet, but those colour-change recipes stay out of the recipe book so the normal construction recipes are easier to find.

## Requirements

Install all of the following mods:

- Minecraft 26.3
- NeoForge 26.3
- Iron Age Furniture `0.4.0.2603002` (the exact version required by this candidate)
- Biomes O' Plenty `26.3.0.0.15` or a newer 26.3 release
- GlitchCore `26.3.0.0.5` or newer for 26.3
- TerraBlender `26.3.0.0.9` or newer for 26.3

The Phase 4 candidate targets NeoForge `26.3.0.77` through the `26.3.0` series and builds against `26.3.0.83`. Packaged dedicated-server probes have passed on both `.77` and `.83` with BOP `26.3.0.0.15`, GlitchCore `26.3.0.0.5` and TerraBlender `26.3.0.0.9`. The `.83` client has checked the visible furniture models and all 26 core/add-on shield-chair renderers, including live updates to two separate clients. All 2,496 core/add-on Phase 4 construction recipes pass native recipe-book autofill and normal/shift crafting. A separate-process save test preserves complete candle and shield storage alongside the furniture across two restarts. This remains a local candidate; publication is a separate step.

Older BOP `26.3.0.0.2` is no longer supported by this candidate: it fails during world startup with current dependencies, including in an installation without Iron Age Furniture. Update the complete dependency stack together rather than mixing it with the earlier GlitchCore and TerraBlender releases.

This is an add-on, not a replacement for either parent mod. Minecraft will show a clear dependency error if Iron Age Furniture or Biomes O' Plenty is missing or incompatible.

## Updating an existing world

The add-on can recognise Biomes O' Plenty furniture saved by older Iron Age Furniture versions, including the red-only padded benches from the current 1.10 and 1.12 releases and the newer sixteen-colour format.

Always back up a world before upgrading it. Very old Minecraft worlds may need to pass through intermediate Minecraft versions before 26.3 can open them. Once Minecraft has successfully loaded the world, the add-on converts the furniture it recognises and preserves bench colour wherever that information is available.

Some Biomes O' Plenty woods no longer exist. Their furniture is changed to the closest practical visual alternative instead of disappearing. The full list and recommended upgrade steps are in the [legacy migration guide](docs/LEGACY_MIGRATION.md).

## For contributors

The project builds with Java 25 and the checked-in Gradle wrapper. Run `gradlew.bat build` on Windows or `./gradlew build` on Linux and macOS.

Before the matching core is published, local development can use `-PlocalCoreJar=<absolute path outside this checkout>` and `-PlocalCoreSha256=<verified SHA-256>`. This checks the jar name, version and checksum. Publishing is deliberately disabled in this mode; normal release builds still require the pinned released dependency.

For Eclipse, import the project as an existing Gradle project. The checked-in Gradle configuration selects an installed Adoptium Java 25 runtime even when Eclipse itself is running on Java 21. Running `prepareEclipse` refreshes the generated Eclipse settings and launch files for the local machine.

Release builds contain deterministic main, source and Javadoc jars. The build also checks the pinned Iron Age Furniture dependency, generated furniture catalog, resources, metadata and release artifacts.
