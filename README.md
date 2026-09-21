# Iron Age Furniture: Biomes O' Plenty Add-on

Version `0.3.0.2603002` adds furniture made from every supported Biomes O' Plenty 26.3 wood to Iron Age Furniture on NeoForge.

## Requirements

- Minecraft 26.3
- NeoForge 26.3
- Iron Age Furniture `0.3.0.2603002` or newer within the 0.3 series
- Biomes O' Plenty `26.3.0.0.2` or newer within the 26.3 series
- Biomes O' Plenty's required GlitchCore and TerraBlender dependencies

The add-on is deliberately separate from the core mod. Its loader identity is `iafbopaddon`, while furniture retains the historical `ironagefurniture` registry and resource IDs so existing worlds can be migrated safely.

The add-on references Biomes O' Plenty textures at runtime and does not redistribute any Biomes O' Plenty assets.

## Legacy migration

Compatibility handling covers the current red-only padded benches from legacy 1.10/1.12 releases and the forthcoming sixteen-colour format. Retired wood families use documented visual fallbacks; this does not claim that the upstream woods were renamed.

See [Legacy migration](docs/LEGACY_MIGRATION.md) for the exact colour contract, fallback table, and whole-world upgrade boundary.

## Development

Use the checked-in Gradle wrapper and Java 25. Portable daemon-JVM criteria make Gradle and Buildship switch from Eclipse's Java 21 runtime to an installed Adoptium Java 25 before this build is evaluated. `prepareEclipse` then records the exact local Java 25 installation and shared Gradle cache in the generated project preferences.

The build verifies the released Iron Age Furniture dependency against its pinned SHA-256 and emits deterministic main, sources and Javadoc jars.
