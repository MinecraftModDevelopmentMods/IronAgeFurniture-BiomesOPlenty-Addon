# Changelog

## 0.4.0.2603002

- Shows the bare wooden frame in inventories when a shield chair has no shield attached.
- Adds wingbacks, thrones, wooden beds and canopy beds, including doubles, in all sixteen colours for the fourteen supported wood families.
- Adds shield storage using the core's shield-chair behaviour and renderers.
- Organises furniture in the core's Chairs, Benches and Beds tabs, with translated names and English fallbacks.
- Requires Biomes O' Plenty `26.3.0.0.15` or newer, GlitchCore `26.3.0.0.5` or newer, and TerraBlender `26.3.0.0.9` or newer for Minecraft 26.3.
- Requires the matching Iron Age Furniture `0.4.0.2603002` core.
- Stops older, incompatible biome dependency combinations at the loader's dependency check instead of allowing them to fail later during startup.

## 0.3.0.2603002

First release for Minecraft 26.3 on NeoForge.

- Adds 546 Iron Age Furniture blocks and items made from all 14 supported Biomes O' Plenty wood families.
- Adds matching crafting recipes, recipe-book unlocks, names and models.
- Supports furniture saved by earlier versions that included Biomes O' Plenty integration.
- Preserves red padded benches from existing 1.10 and 1.12 worlds and recognises all sixteen colours used by the newer legacy format.
- Replaces furniture made from retired Biomes O' Plenty woods with documented visual alternatives during migration.
- Loads legacy padded-bench compatibility entries without missing-model warnings while they are being upgraded.
