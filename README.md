# Iron Age Furniture: Biomes O' Plenty Add-on

Bring the woods of Biomes O' Plenty into Iron Age Furniture. This add-on lets you craft the familiar Iron Age Furniture range in every supported Biomes O' Plenty 26.3 wood type.

## What this add-on includes

- 546 furniture blocks and items across 14 Biomes O' Plenty wood families
- Chairs, stools, benches and the full matching furniture range
- Recipes and recipe-book unlocks for every included item
- Compatibility support for older Iron Age Furniture worlds

The add-on uses Biomes O' Plenty's installed textures, so resource packs that replace those wood textures can also change the matching furniture. No Biomes O' Plenty assets are included in this project.

## Requirements

Install all of the following mods:

- Minecraft 26.3
- Forge `66.0.2` or newer for Minecraft 26.3
- Iron Age Furniture `0.3.0.2603001` or a newer compatible Forge 0.3 release
- Biomes O' Plenty `26.3.0.0.2` or a newer 26.3 release
- The GlitchCore and TerraBlender versions required by Biomes O' Plenty

For Forge testing, TerraBlender `26.3.0.0.5` is the current recommended build. TerraBlender `26.3.0.0.6` has a confirmed server-start crash in its own surface-rule update code on Forge 66.0.2; use `26.3.0.0.5` until TerraBlender publishes a corrected build.

This is an add-on, not a replacement for either parent mod. Minecraft will show a clear dependency error if Iron Age Furniture or Biomes O' Plenty is missing or incompatible.

NeoForge players should use the separate `0.3.0.2603002` build. Furniture keeps the same saved registry names on both loaders, so a backed-up 26.3 world can move between the matching Forge and NeoForge installations.

## Updating an existing world

The add-on can recognise Biomes O' Plenty furniture saved by older Iron Age Furniture versions, including the red-only padded benches from the current 1.10 and 1.12 releases and the newer sixteen-colour format.

Always back up a world before upgrading it. Very old Minecraft worlds may need to pass through intermediate Minecraft versions before 26.3 can open them. Once Minecraft has successfully loaded the world, the add-on converts the furniture it recognises and preserves bench colour wherever that information is available.

Some Biomes O' Plenty woods no longer exist. Their furniture is changed to the closest practical visual alternative instead of disappearing. The full list and recommended upgrade steps are in the [legacy migration guide](docs/LEGACY_MIGRATION.md).

## For contributors

The project builds with Java 25 and the checked-in Gradle wrapper. Run `gradlew.bat build` on Windows or `./gradlew build` on Linux and macOS.

For Eclipse, import the project as an existing Gradle project. The checked-in Gradle configuration selects an installed Adoptium Java 25 runtime even when Eclipse itself is running on Java 21. Running `prepareEclipse` refreshes the generated Eclipse settings and launch files for the local machine.

Release builds contain deterministic main, source and Javadoc jars. The build also checks the pinned Iron Age Furniture dependency, generated furniture catalog, resources, metadata and release artifacts.
