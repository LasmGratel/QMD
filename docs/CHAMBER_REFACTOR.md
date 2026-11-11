# Chamber classes by recipe kind

## Plan and ownership

The refactor was implemented through three isolated Java slices. All slices stopped before integration into `G:/QMD`; compilation, data generation and GameTests ran there after the merge.

| Slice | Owned implementation |
| --- | --- |
| Recipes and JEI | `recipe/chamber`, `compat/jei/category/chamber` |
| Controllers and simulation | `block/chamber`, `block/entity/chamber`, `multiblock/chamber`, existing shared chamber geometry and reaction helpers |
| Menus and screens | `block/menu/chamber`, `client/screen/chamber` |
| Integration | Shared registries, client/JEI registration, datagen factories, meter/cell-exchange callers, tests and documentation |

## Concrete families

`ChamberKind` retains the eight recipe IDs and physical channel counts. It replaces the nested `ChamberRecipe.Kind`; the old shared recipe, category, block, block entity, menu, screen and vacuum-state classes are removed.

| Kind | Recipe and JEI prefix | Controller, block, menu and screen prefix |
| --- | --- | --- |
| Target chamber | `TargetChamber` | `TargetChamber` |
| Decay chamber | `DecayChamber` | `DecayChamber` |
| Collision chamber | `CollisionChamber` | `CollisionChamber` |
| Beam dump | `BeamDump` | `BeamDump` |
| Neutral containment | `NeutralContainment` | `NeutralContainment` |
| Cell filling | `CellFilling` | Auxiliary process in neutral containment |
| Nucleosynthesis chamber | `NucleosynthesisChamber` | `NucleosynthesisChamber` |
| Vacuum chamber heating | `VacuumChamberHeating` | Auxiliary heater-vent process in nucleosynthesis |

Each recipe extends `AbstractChamberRecipe` and supplies its fixed kind, constructor factory, typed serializer and typed recipe type. `QMDRecipes` registers all eight concrete types explicitly. The generic datagen builder accepts these concrete constructor factories. Shared codecs preserve the original fields, optional defaults, validation, physical channels, alternatives, components and exact long energies.

`AbstractChamberBlockEntity` owns common storage, ports, capabilities, synchronization and persistence. Particle, matter-processing and vacuum abstract bases share their corresponding mechanics. Concrete controllers own reaction selection, product formulas, work buffering, fluid acceptance and menu creation. `ChamberPartBlockEntity` represents non-controller parts. Neutral containment and nucleosynthesis have separate simulation states under `multiblock/chamber`; cuboid assembly/validation and reaction arithmetic remain shared helpers.

`AbstractChamberMenu` retains ownership/distance/stale-menu validation, inventory transfer, bucket exchange and server-validated controls. Each concrete menu owns its slot layout and registered type. `AbstractChamberScreen` shares widgets and rendering helpers; concrete screens own their original artwork, instruments, overlays, clear/status positions and recipe areas. Eight typed JEI categories own their diagrams, animations and metadata. Target transfer uses `TargetChamberMenu`/`TargetChamberRecipe`; cell transfer uses `NeutralContainmentMenu`/`CellFillingRecipe`.

## Compatibility

- Blocks, items, recipes, configs, capabilities and blockstate properties retain their IDs and meanings.
- All chamber blocks retain the `qmd:particle_chamber` block entity ID. The registered factory reconstructs the concrete controller or part from its block state, including when loading saved NBT. Existing entity/vacuum keys are preserved.
- The target menu retains `qmd:particle_chamber`. The other five controllers have their own menu IDs matching their recipe kinds.
- No new cell-filling or heater world controller is invented. Their recipes and JEI categories remain separate auxiliary processes.
- Authored GUI assets and generated-resource ownership are unchanged.

## Verification (2026-11-11)

| Exact command or comparison on `G:/QMD` | Result |
| --- | --- |
| `.\gradlew.bat compileJava --console=plain` | Passed on the first integrated attempt, 24 seconds |
| `.\gradlew.bat runData --console=plain` | Passed, 23 seconds; all ten providers completed |
| SHA-256 comparison of generated resources | All 2,476 resources identical; zero added, removed or changed paths |
| `.\gradlew.bat runGameTestServer --console=plain` | Passed, 49 seconds; all 123 required tests passed, 21.61-second suite |

New GameTests round-trip every loaded recipe in all eight types through data and registry-aware network codecs, retaining its concrete class and complete serialized payload. A second test places all six controller/block pairs, restores them through the retained block entity ID with inventory/fluid/control data, and checks their concrete registered menus. Existing tests continue to cover assembled chamber reactions, automation, inventories, thermal behavior, radiation, persistence and resource reload.

Baseline Java, resource hashes, merged-file lists, command logs and comparisons are under `build/reports/migration/chamber-kinds/`. The GameTest log retains the known upstream optional Create loot-table error and a version-check TLS failure; no QMD recipe-loading errors or required-test failures occurred. No release build, optional KubeJS run or rendered client/JEI check was performed. Headless tests do not verify appearance.
