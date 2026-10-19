# Iron Age Furniture: Biomes O' Plenty Add-on

Bring the woods of Biomes O' Plenty into Iron Age Furniture. Version `0.4.0.2602002` provides Phase 4 furniture for Minecraft 26.2 on NeoForge. Back up existing worlds and check upgrades on disposable copies before updating.

## What this add-on includes

- The original 546 furniture items across 14 Biomes O' Plenty wood families, with their saved identities preserved
- Wingback chairs, thrones, wooden beds and canopy beds, including doubles, in all sixteen colours
- Shield chairs that preserve the complete shield item, including its name, damage, enchantments and banner design, and show a bare wooden frame when empty
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

These requirements apply to add-on `0.4.0.2602002`.

Install all of the following mods:

- Minecraft 26.2
- NeoForge `26.2.0.45-beta` in the `26.2.0` series
- Iron Age Furniture `0.4.0.2602002` (the exact matching core version)
- The Minecraft 26.2 NeoForge build of Biomes O' Plenty `26.1.2.0.40`, or a newer build for Minecraft 26.2
- GlitchCore `26.2.0.0.0` or newer for 26.2
- TerraBlender `26.2.0.0.1` or newer for 26.2

The stable BOP jar is named `BiomesOPlenty-neoforge-26.2-26.1.2.0.40.jar`. Its retained numeric version starts with `26.1.2`, but its published Minecraft metadata and filename explicitly target 26.2. Do not substitute a jar whose Minecraft target is actually 26.1.2.

The dependency range accepts that stable release and newer 26.2 builds, including BOP `26.2.0.0.28`; it does not require a BOP beta. The required GlitchCore and TerraBlender releases may themselves be labelled beta. Install the complete matching stack, not jars for Forge or Minecraft 26.3.

This is an add-on, not a replacement for either parent mod. Minecraft will show a clear dependency error if Iron Age Furniture or Biomes O' Plenty is missing or incompatible.

## Updating an existing world

The add-on can recognise Biomes O' Plenty furniture saved by older Iron Age Furniture versions, including the red-only padded benches from the current 1.10 and 1.12 releases and the newer sixteen-colour format.

Always back up a world before upgrading it. Very old Minecraft worlds may need intermediate Minecraft versions before 26.2 can open them. The add-on converts furniture it recognises and preserves bench colour where that information is available. Do not open a Minecraft 26.3 world in 26.2: this is a downward code port, not world-downgrade support.

Some Biomes O' Plenty woods no longer exist. Their furniture is changed to the closest practical visual alternative instead of disappearing. The full list and recommended upgrade steps are in the [legacy migration guide](docs/LEGACY_MIGRATION.md).

## For contributors

The project builds with Java 25 and the checked-in Gradle wrapper. Run `gradlew.bat build` on Windows or `./gradlew build` on Linux and macOS.

Before the matching core is published, local development can use `-PlocalCoreJar=<absolute path outside this checkout>` and `-PlocalCoreSha256=<verified SHA-256>`. This checks the jar name, version and checksum. Publishing is deliberately disabled in this mode; normal release builds still require the pinned released dependency.

For Eclipse, import the project as an existing Gradle project. The checked-in Gradle configuration selects an installed Adoptium Java 25 runtime even when Eclipse itself is running on Java 21. Running `prepareEclipse` refreshes the generated Eclipse settings and launch files for the local machine.

Release builds contain deterministic main, source and Javadoc jars. The build also checks the pinned Iron Age Furniture dependency, generated furniture catalog, resources, metadata and release artifacts.
