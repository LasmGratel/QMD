# Shared-material furnace policy

QMD's 18 elemental dust-to-ingot furnace recipes retain their existing IDs, zero experience and 200-tick cooking time. Mercury remains excluded. The four copernicium and MIX smelting conversions are outside this policy and remain unchanged.

The legacy implementation used Nuclearcraft's `ore_dict_raw_material_recipes` switch. Its false branch accepted only QMD dust; its true branch registered every dust from the shared ore dictionary. Both branches selected their result through Nuclearcraft's prioritized crafting-stack helper. The historical Nuclearcraft default is not present in QMD's source and Neohaul 0.10.10 does not expose the old setting, so QMD does not infer it.

## Configuration

`qmd-material-recipes.toml` exposes the replacement policy:

| Setting | Default | Behavior |
| --- | --- | --- |
| `furnace_materials.shared_material_inputs` | `false` | `false` emits the local `qmd:dust_<material>` ingredient. `true` emits the common `#c:dusts/<material>` ingredient. |
| `furnace_materials.preferred_output_mods` | `["qmd"]` | Ordered mod namespaces used to select an output from `c:ingots/<material>`. If none is available, the matching `qmd:ingot_<material>` is used. |

The default therefore preserves the currently shipped QMD-input/QMD-output behavior. Pack authors who want the shared-material policy enable `shared_material_inputs` and put their preferred material provider before `qmd` in `preferred_output_mods`, for example `["examplemetals", "qmd"]`.

This policy is evaluated by data generation because vanilla furnace results require a concrete item ID. After changing either value, rerun `./gradlew.bat runData --console=plain` and distribute the regenerated data. It is not a live `/reload` switch. Selection is deterministic: configured namespaces are checked in order, candidates within one namespace are sorted by resource location, and QMD is the final fallback.

## Ownership and verification

`MaterialRecipeConfig` owns validation and the pure local/tag input plus preferred-output selection rules. `MinecraftSmeltingRecipes` owns the 18 preserved recipe IDs and queries the matching common ingot tag for candidates. Generated JSON remains owned by `src/generated/resources/` and must not be edited by hand.

Focused tests should exercise both policy values, preferred namespace ordering and QMD fallback. This rescue slice intentionally does not run data generation or edit generated resources; the integrator must register the config and regenerate after integration.
