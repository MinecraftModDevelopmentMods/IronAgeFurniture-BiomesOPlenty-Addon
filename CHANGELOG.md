# Changelog

## 0.4.0.2602002

- Port the released NeoForge 26.3 Phase 4 add-on to Minecraft 26.2, keeping the add-on separate from the core.
- Preserve the fourteen wood families, historical registry/resource identities, sixteen-colour furniture, complete shield storage and documented retired-wood substitutions.
- Require the matching Iron Age Furniture `0.4.0.2602002` core and native NeoForge 26.2 dependencies.
- Add translated names for hidden furniture parts and older compatibility entries in all seventeen language files, including names used by HUD mods.
- Accept the stable Minecraft 26.2 BOP build `26.1.2.0.40` and newer 26.2 builds, including `26.2.0.0.28`, without forcing a BOP beta.
- Keep GlitchCore `26.2.0.0.0` and TerraBlender `26.2.0.0.1` minimum requirements.
- Preserve nested containers, bundle selection, padded-bench colours and joined shapes through first and repeated world loads on the native 26.2 runtime.

## Earlier NeoForge 26.3 history

## 0.4.1.2603002

- Supports the stable Biomes O' Plenty `26.3.0.0.7` release, so a BOP beta is no longer required.
- Keeps support for newer BOP builds for Minecraft 26.3, including `26.3.0.0.15`.
- Continues to use Iron Age Furniture `0.4.0.2603002`, GlitchCore `26.3.0.0.5` or newer, and TerraBlender `26.3.0.0.9` or newer. Furniture, recipes and existing-world compatibility are unchanged.

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
