# Accelerator classes by machine and recipe kind

## Plan and structure

The accelerator split follows the chamber hierarchy: separate the recipe families, give each machine a concrete block/entity/menu/screen, extract machine-specific processing, then integrate registrations, payloads, data generation and callers. This change was implemented sequentially and verified on `G:/QMD`.

| Layer | Implementation |
| --- | --- |
| Recipes | `recipe.accelerator.AbstractAcceleratorRecipe`, `AcceleratorSourceRecipe`, `AcceleratorCoolingRecipe`, `MassSpectrometerRecipe` and standalone `AcceleratorRecipeKind` |
| JEI | `compat.jei.category.accelerator.AbstractAcceleratorCategory` and three concrete categories; typed registrations/transfers in `QMDAcceleratorJei` |
| Blocks | `block.accelerator.AbstractAcceleratorBlock`, six controller blocks, an ion-source block and a common component block |
| Block entities | `block.entity.accelerator.AbstractAcceleratorBlockEntity`, six controller entities, an ion-source entity and a common component entity |
| Menus | `block.menu.accelerator.AbstractAcceleratorMenu`, six controller menus, an ion-source menu and a component/port menu |
| Screens | `client.screen.accelerator.AbstractAcceleratorScreen`, a shared beam-instrument base and eight concrete views |
| Processing | `AbstractAcceleratorLogic`, shared ring/routing bases and six concrete logic classes under `multiblock.accelerator` |

The six controller prefixes are `LinearAccelerator`, `RingAccelerator`, `Decelerator`, `BeamDiverter`, `BeamSplitter` and `MassSpectrometer`. `AcceleratorKind` selects their concrete processing logic. The existing `Accelerator` coordinator retains ZeroCore attachment, assimilation, shared envelope/topology validation, thermal/energy storage and persistence. Concrete logic owns beam limits, energy/focus transformations, neutral-beam acceptance, splitting, straight/bent outputs, synchrotron radiation and spectrometer conversion. Cooling and source acquisition remain shared services.

Each recipe fixes its family, exposes typed serializer/type registrations and uses a concrete constructor factory in datagen. `QMDRecipes` no longer uses accelerator mode maps. `QMDMenus` registers each concrete view, and `AcceleratorStatePayload` accepts the common menu base while retaining its container-ID/position binding. JEI source and spectrometer transfers use their concrete menus, retain item alternatives and keep player inventory starts at 2 and 6 respectively.

## Preserved behavior

- All existing block/item, recipe/serializer, config and payload IDs remain unchanged. The block entity ID is still `qmd:accelerator_part`; its factory reconstructs the concrete entity from the block state. Existing inventory, fluid, output-setting and multiblock NBT keys remain.
- The linear controller retains menu ID `qmd:accelerator`. Other controllers, ion sources and component/port views have separate menu types. Right-clicking non-controller parts retains their existing common view and controller-bound actions.
- Basic and laser ion sources retain their variant settings, reservoir swap/consumption rules, physical source inventory and spectrometer inventory/tank routing.
- Menus retain slot indices, the hidden spectrometer slot, shift-click behavior, owner/distance/stale-inventory checks, clearing, redstone/percentage controls, routing and synchronized capacities.
- Screens/categories use the original source, controller, cooling and spectrometer artwork, atlas coordinates and native widgets. Existing worktree JEI adjustments are retained: source item input at `(3,3)`, spectrometer fluid input at x=1, particle output at `(55,12)` relative to the category panel.
- Recipe fields/defaults, ingredients/components, chance outputs, exact long particle energies and conditions are unchanged. No generated JSON was edited by hand and no assets changed ownership.

## Verification (2026-11-11)

| Exact command/comparison on `G:/QMD` | Result |
| --- | --- |
| `.\gradlew.bat compileJava --console=plain` | Final source passed, 3 seconds |
| `.\gradlew.bat runData --console=plain` | Passed, 22 seconds; all ten providers completed |
| SHA-256 generated-resource comparison | All 2,476 files identical; zero added, removed or changed paths |
| `.\gradlew.bat runGameTestServer --console=plain` | Final run passed, 47 seconds; all 125 required tests passed, 24.48-second suite |

Two new GameTests round-trip every loaded recipe in all three concrete families through data/network codecs and load all six controller entities, a source and a component through the retained block entity ID, checking storage, output settings, concrete menus and physical slots. Existing assembled accelerator tests retain coverage of all six machine behaviors, source/collector automation, thermal/energy behavior, persistence, live recipe removal and resource reload. Chamber and other existing tests also pass through the updated shared cooling and registry APIs.

An initial runtime compile identified a missing `List` import in the extracted spectrometer screen. The first GameTest command stopped during test-source compilation on two old type references; no in-game test failed. After those fixes, the suite passed at 125 tests. The final run followed preservation of the worktree's JEI offsets and a fresh successful runtime compile. Logs, source/doc snapshots and resource comparisons are under `build/reports/migration/accelerator-kinds/`.

No release build, optional KubeJS run or rendered client/JEI check was performed. Headless tests do not establish appearance or actual client packet delivery.
