# Updating older worlds

Version `0.4.0.2602002` targets Minecraft 26.2 on NeoForge. Work on backed-up copies at or below that Minecraft version. A Minecraft 26.3 world must not be opened in 26.2.

Iron Age Furniture included Biomes O' Plenty furniture directly in some older Minecraft versions. This Phase 4 branch supplies that furniture through the separate add-on instead.

The add-on can restore recognised furniture when an older world reaches Minecraft 26.2. This includes padded benches and padded back benches from the 1.10 and 1.12 versions of Iron Age Furniture.

## Before you start

1. Make a complete backup of the original world.
2. Work on a copy until you have checked the result in game.
3. Follow Minecraft's normal world-upgrade path. The furniture fixtures described below loaded directly, but other old worlds may need intermediate Minecraft versions before 26.2 can read them.
4. Install the matching Minecraft 26.2 NeoForge Iron Age Furniture core, this add-on, Biomes O' Plenty, GlitchCore and TerraBlender before opening the copy.
5. Visit the areas containing old furniture so Minecraft loads and updates those chunks, then save and reopen the world once.

The add-on reports a short migration summary in the game log. Loading the world again is safe; furniture that has already been updated is left alone.

## Padded bench colours

The older Phase 2 versions stored every padded bench as red. Those benches remain red after migration.

Phase 3 and Phase 4 legacy formats can contain sixteen upholstery colours:

- Red, orange, magenta, light blue, yellow, lime, pink and gray
- Light gray, cyan, purple, blue, brown, green, white and black

When colour information is present, the add-on keeps it. If that information is absent or damaged, the bench becomes red so that old worlds retain their original appearance.

Bench direction and joined shape are retained wherever the old and new blocks share the same property. Waterlogged furniture also remains waterlogged when the saved state contains that information.

## Furniture made from retired woods

Several old Biomes O' Plenty woods are no longer available. Furniture made from them is changed to the following visual alternative:

| Retired wood | Replacement furniture |
|---|---|
| Cherry | Vanilla cherry |
| Ebony | Biomes O' Plenty hellbark |
| Ethereal | Vanilla warped |
| Eucalyptus | Biomes O' Plenty origin oak |
| Mangrove | Vanilla pale oak |
| Sacred oak | Biomes O' Plenty origin oak |

These are visual compatibility choices made by Iron Age Furniture. They do not mean that Biomes O' Plenty renamed one wood to another. Furniture made from a Biomes O' Plenty wood that still exists keeps its historical identity.

## What this add-on cannot do

This migration covers Iron Age Furniture blocks and items only. It does not convert Biomes O' Plenty terrain, plants or other contents of an old world, and it cannot make Minecraft 26.2 open a world format that Minecraft itself cannot read. The availability of a present-day biome mod does not restore every block or item from its historical releases.

If Minecraft refuses to open the world, return to the untouched backup and use the normal Minecraft upgrade sequence. Do not continue experimenting on the only copy of a valued world.

## Phase 4 verification

The local `0.4.0.2602002` core and add-on passed first and second loads of fourteen original furniture fixtures from Minecraft 1.10, 1.12 and 1.14–1.17 on NeoForge 26.2.0.45-beta. The tested copies loaded directly through the compatibility bridge; no intermediate Minecraft save was needed for those particular cases. Original source-world fingerprints remained unchanged. Bed direction, upholstery, joined bench shapes, stored items and repeated-load behaviour were checked.

A separate freshly generated Minecraft 26.2 test world passed padded-bench migration and repeated disk loads with 16,384 placed blocks, nested container items and selected bundles. Unchanged slots, item components and bundle selection were retained; the second load performed no further conversions.

This is furniture compatibility testing, not a promise that the rest of an old modpack can be converted. The Sylvester copy passed its furniture checks, but contained many unavailable foreign blocks and items. That is not approval to convert the complete original modpack. Base Metals and the original Furniture Mod have no qualifying 26.2 provider release in the checked catalogs. Missing BYG or Immersive Engineering furniture and unrelated old biome terrain are outside this add-on's coverage. Keep the original installation and backup until you have checked your own copied world.

The stable BOP `26.1.2.0.40` jar explicitly targets Minecraft 26.2, despite the retained numeric prefix in its version. The newer 26.2 BOP `26.2.0.0.28` build is also supported. Use the actual 26.2 files and their required dependencies, not jars for Minecraft 26.3 or another loader.
