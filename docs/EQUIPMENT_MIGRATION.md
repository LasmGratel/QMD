# Equipment migration

## Scope

This slice restores the five tungsten-carbide tools, basic and advanced drills, and the four-piece HEV suit for Minecraft 1.21.1 and NeoForge 21.1.x. Item IDs remain flattened under `qmd`: `sword_tungsten_carbide`, `pickaxe_tungsten_carbide`, `shovel_tungsten_carbide`, `axe_tungsten_carbide`, `hoe_tungsten_carbide`, `drill_basic`, `drill_advanced`, `helm_hev`, `chest_hev`, `legs_hev`, and `boots_hev`.

The behavior reference is the preserved 1.12 implementation:

- `legacy-src/main/java/lach_01298/qmd/item/QMDItems.java`
- `legacy-src/main/java/lach_01298/qmd/item/ItemDrill.java`
- `legacy-src/main/java/lach_01298/qmd/item/QMDArmour.java`
- `legacy-src/main/java/lach_01298/qmd/item/ItemHEVSuit.java`
- `legacy-src/main/java/lach_01298/qmd/ArmourBonusHandler.java`
- `legacy-src/main/java/lach_01298/qmd/recipes/QMDCraftingRecipeHandler.java`
- `legacy-src/main/java/lach_01298/qmd/recipes/RecipesArmourDyes.java`
- `legacy-src/main/java/lach_01298/qmd/config/QMDConfig.java`

## Tools and drills

The tungsten-carbide tier retains mining level 3, durability 3122, mining speed 8, attack-damage bonus 3, and enchantability 12. Its repair material is `qmd:ingot_tungsten_carbide`. The sword, pickaxe, shovel, axe, and hoe use the normal 1.21.1 tool actions and durability rules. Both mirrored axe and hoe patterns are retained.

The drills remain single-stack, tier-3, bidirectional FE items. The basic drill stores 250,000 FE, mines at level 3 and speed 8, and uses radius 1 for a 3 by 3 plane. The advanced drill stores 2,500,000 FE, mines at level 4 and speed 12, and uses radius 2 for a 5 by 5 plane. Each successfully mined nonzero-hardness block costs 100 FE. The initially targeted block uses the ordinary tool break path; additional blocks lie in the plane perpendicular to the hit face, must be no harder than twice the target, and consume energy only when actually removed. Creative mining does not consume energy. Without enough energy, the drill falls back to hand speed and stone-level harvesting. Fortune and Silk Touch remain valid drill enchantments, and stored energy drives the durability bar rather than item damage.

## HEV suit

Every HEV piece is a single-stack, tier-3, bidirectional FE item with a 1,000,000 FE default capacity. The unpowered armor values are feet 1, legs 3, chest 4, and head 1 with zero toughness. While charged they rise to feet 4, legs 7, chest 9, and head 4 with toughness 4. Absorbing ordinary damage costs 100 FE per point of incoming damage. The suit is not physically damaged by that protection path.

Legacy radiation resistance values are helmet 20, chestplate 30, leggings 20, and boots 20. The corresponding special-damage fractions are 0.2, 0.4, 0.2, and 0.2 for radiation, sulphuric acid, acid burn, corium burn, and hot-coolant burn. HEV pieces reject additional Neohaul shielding upgrades because their shielding is intrinsic. Particle and projectile damage continues through the armor attribute and charged-protection path rather than a placeholder immunity.

Charged equipment retains the `ArmourBonusHandler` effects:

- leggings add 0.2 vertical velocity to a jump for 100 FE and provide a 25 percent movement-speed multiplier while energy remains;
- a sprinting jump with the chestplate adds a one-block horizontal impulse for 250 FE;
- boots provide step assist while charged and reduce a fall by four blocks with half damage multiplier, consuming 100 FE for falls over three blocks;
- the helmet rejects poison applications for 1,000 FE and reduces stronger or longer Wither to amplifier 0 for 80 ticks for 1,000 FE.

The legacy default undyed color is `0xFF8B2E`. The dynamic `qmd:armour_dye` recipe accepts exactly one HEV piece plus one or more dye items, returns one copy, preserves its energy and other data components, and applies vanilla brightness-preserving color averaging. It is dynamic, fits any grid with at least two slots, and returns normal container items.

## Configuration defaults

`EquipmentConfig` keeps the two legacy registration switches without removing existing configuration:

| Setting | Default | Meaning |
| --- | ---: | --- |
| `equipment.enable_tungsten_carbide_tools` | `true` | Enables the five tungsten-carbide tools and their recipes/creative entries. |
| `equipment.enable_drills` | `true` | Enables both drills and their recipes/creative entries. |

The migrated implementation retains the legacy numerical defaults as equipment constants: 100 FE per drill block; drill capacities 250,000 and 2,500,000 FE; radii 1 and 2; HEV capacities of 1,000,000 FE per piece; armor arrays `[4,7,9,4]` charged and `[1,3,4,1]` unpowered; radiation resistance `[20,30,20,20]`; toughness 4 charged and 0 unpowered; and HEV action costs `[100,100,250,100,1000]`.

## Recipes and resources

`QmdEquipmentRecipes` owns all equipment crafting declarations. It retains the legacy translation-key-derived IDs `qmd:item.qmd.<item>`, including `qmd:item.qmd.axe_tungsten_carbide_1` and `qmd:item.qmd.hoe_tungsten_carbide_1` for the mirrored patterns. Tool recipes use `#c:ingots/tungsten_carbide`; the drill and HEV patterns retain the original ingredients and output count of one. Tool and drill recipes carry `qmd:equipment_enabled` conditions so disabling a family does not change its registered item or recipe IDs. The special dye recipe retains the explicit legacy ID `qmd:armour_dye`. Generated recipe JSON belongs under `src/generated/resources` after the integrator adds the catalog call and runs data generation; it must not be hand-authored.

The original item textures and item models remain authored assets because they contain item-specific art rather than a repetitive generated family. Textures are copied byte-for-byte from the legacy tree. HEV worn textures retain the base and overlay layers required for dye tinting.

## Verification scope

The equipment GameTests cover equipping every HEV slot, observable tool harvesting, per-block drill energy debit, charged and uncharged HEV attributes/protection, the loaded dye recipe and preservation of dyed energy state, and registry-aware item persistence. Compilation is a separate check from executing those tests.

Headless tests do not prove the appearance of held tools, drill transforms, the HEV item tint, the two worn armor layers, animation, or first-person/third-person armor alignment. Those rendering limits are recorded explicitly and do not reduce the required runtime behavior.

The main-tree rescue integration registers the items, capabilities, conditions, common equipment events, creative entries, and `qmd-equipment.toml`. Client hooks now belong to `EquipmentClient`, called only by `QMDClient`; the common registry imports no client class. `RecipeCatalog` calls `QmdEquipmentRecipes`, and `runData` emitted its recipes. Authored equipment models retain ownership of their layered textures; the generic item-model provider skips this family.

The six equipment GameTests passed in the integrated 119-test base run. The drill fixture uses a fake player to avoid simulating a network login without the NeoForge handshake, and its 3x3 plane fits inside the template. Exact commands and transcripts are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). Rendered equipment remains unverified.
