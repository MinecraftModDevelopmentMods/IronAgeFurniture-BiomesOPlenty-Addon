# Updating older worlds

Iron Age Furniture included Biomes O' Plenty furniture directly in some older Minecraft versions. From Minecraft 26.3 onward, that furniture is supplied by this add-on instead.

The add-on can restore recognised furniture when an older world reaches Minecraft 26.3. This includes padded benches and padded back benches from the 1.10 and 1.12 versions of Iron Age Furniture.

## Before you start

1. Make a complete backup of the original world.
2. Work on a copy until you have checked the result in game.
3. Follow Minecraft's normal world-upgrade path. A world from 1.10 or 1.12 may need to be opened in one or more intermediate Minecraft versions before 26.3 can read it.
4. Install Iron Age Furniture, Biomes O' Plenty, this add-on, and all required dependencies before opening the world in 26.3.
5. Visit the areas containing old furniture so Minecraft loads and updates those chunks, then save and reopen the world once.

The add-on reports a short migration summary in the game log. Loading the world again is safe; furniture that has already been updated is left alone.

## Padded bench colours

The currently released 1.10 and 1.12 furniture stored every padded bench as red. Those benches remain red after migration.

The newer legacy format supports sixteen upholstery colours:

- Red, orange, magenta, light blue, yellow, lime, pink and gray
- Light gray, cyan, purple, blue, brown, green, white and black

When colour information is present, the add-on keeps it. If that information is absent or damaged, the bench becomes red so that existing worlds retain the appearance used by the released versions.

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

This migration covers Iron Age Furniture blocks and items only. It does not convert Biomes O' Plenty terrain, plants or other contents of an old world, and it cannot make Minecraft 26.3 open a world format that Minecraft itself cannot read.

If Minecraft refuses to open the world, return to the untouched backup and use the normal Minecraft upgrade sequence. Do not continue experimenting on the only copy of a valued world.
