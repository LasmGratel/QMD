# QMD 1.21.1 migration plan

Target Minecraft 1.21.1, NeoForge 21.1.x and Java 21. Keep `qmd` / `Quantum Minecraft Dynamics`. The pinned `build/neohaul-upstream` checkout is the source reference; imports beginning `nc.` must be evaluated against `com.nred.nuclearcraft`. Package changes alone do not migrate API behavior.

The old mod remains intact under `legacy-src/` and is excluded from compilation. All migration work belongs under `src/`. This inventory describes legacy scope; it does not claim those systems have already been migrated or verified.

## Current progress

The active implementation uses the modern layout in [PACKAGE_LAYOUT.md](PACKAGE_LAYOUT.md). The material/particle foundation, ore leacher, collectors and creative source now extend to all six accelerator families, beamline transport, four particle chamber families, neutral containment/cell exchange, nucleosynthesis and vacuum heating, the native HX-based liquefier, standalone irradiator, discharge lamps, native fission/RTG/turbine components, source/cell items and the gamma-flash entity.

All 19 legacy JEI category families are registered in `compat.jei` alongside their runtime consumers. Default particle reactions and accelerator sources/cooling/spectrometry are migrated, with supporting semiconductor/alloy/waste/fuel and native mercury-cycle production. Optional KubeJS can customize the active serializers through ordinary server recipe events; custom startup registration/builders still need lifecycle support. CraftTweaker/GroovyScript are reference sources only.

Exact command results and unverified behavior are in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). Source implementation does not establish full legacy parity. Remaining scope includes equipment/tools/armor, weapons/projectiles, meter/medicine/paint, the player manual/audio, broader chemistry/material policy, supported KubeJS startup extension registries and client rendering verification. Headless GameTests cover machine/menu/automation/persistence/reload behavior and do not verify rendered appearance.

Feature details: [ACCELERATOR_MIGRATION.md](ACCELERATOR_MIGRATION.md), [CHAMBERS_MIGRATION.md](CHAMBERS_MIGRATION.md), [LIQUEFIER_MIGRATION.md](LIQUEFIER_MIGRATION.md), [IRRADIATOR_MIGRATION.md](IRRADIATOR_MIGRATION.md), [FISSION_AND_CELLS_MIGRATION.md](FISSION_AND_CELLS_MIGRATION.md), [JEI_MIGRATION.md](JEI_MIGRATION.md).

## Legacy scope

Inspection found 499 QMD Java sources and 39 `nclegacy` support sources. There are 799 resource files, including 583 textures and 43 models. The main legacy block registry declares 76 block fields, many with metadata variants. There are 64 registered particle types and 22 GUI source files.

| System | Legacy source locations | Required equivalent |
|---|---|---|
| Mod lifecycle, config, registries and materials | `QMD.java`, `config/`, `enums/`, `item/QMDItems.java`, `block/QMDBlocks.java`, `tab/` | Deferred registries, all flattened material/block variants, real configuration and tabs |
| Particle model and storage | `particle/`, `capabilities/`, `pipe/` | Full catalogue, composition/antiparticle relationships, stack serialization, capability transfer and beamline state |
| Accelerators | `accelerator/`, `multiblock/` | Linear/ring accelerators, RF cavities, magnets, cooling placement rules, ion sources, diverter/splitter, decelerator and mass spectrometer |
| Particle chambers | `particleChamber/` | Target, decay and collision chambers, beam dump, detectors, beam ports and processing logic |
| Vacuum chambers | `vacuumChamber/` | Exotic/neutral containment, nucleosynthesis, magnetic coils, lasers, plasma and heating |
| Liquefier | `liquefier/` | Complete multiblock, coolant handling, compressor/nozzle operations and rendering |
| Standalone machines and collectors | `machine/`, `tile/`, `block/` | Ore leacher, irradiator/fuel, atmosphere/liquid collectors, creative particle source, lamps and other utility blocks |
| Fluids and chemistry | `fluid/`, `recipes/QMDRecipes.java` | All 59 explicit fluid registrations plus dynamically generated variants, fluid types/blocks/buckets, chemical and processing recipes |
| Nuclearcraft extensions | `fission/`, `QMDRadSources.java`, `QMDRecipes.java` | Reflectors, shields, strontium RTG, copernicium fuel forms, radiation sources and all NC recipe additions |
| Equipment and entities | `item/`, `entity/`, `ArmourBonusHandler.java` | Tungsten carbide tools, drills, HEV suit, energy/particle sources and cells, all three weapons/projectiles, meter, tablets, food and luminous paint |
| Menus, screens and networking | `container/`, `gui/`, `multiblock/container/`, `multiblock/gui/`, `multiblock/network/`, `network/` | Full interactive menus and screens, slot/tank synchronization, controls and server-authoritative payload handling |
| Recipes and viewer support | `recipe/`, `recipes/`, `jei/` | Item/fluid/particle/world ingredients, matching/chance behavior, all recipe families and custom particle JEI ingredients/screens |
| Scripting | `crafttweaker/`, `groovyscript/` | KubeJS equivalents for particles, components/antiparticles, item sources, recipes, removals and reload/cache behavior |
| Rendering, audio and manual | `model/`, `render/`, `sound/`, `assets/qmd/patchouli_books/` | Beam/entity/block rendering, retained sounds, translated tooltips and complete player guide |

## Recipe families

`recipes/QMDRecipes.java` registers 15 QMD handlers: accelerator source, accelerator cooling, mass spectrometer, liquefier, target chamber, decay chamber, beam dump, collision chamber, ore leacher, irradiator, irradiator fuel, neutral containment, cell filling, nucleosynthesis chamber and vacuum chamber heating. Liquefier coolant uses an NC basic handler. Atmosphere and liquid collector mappings are registered separately. The same file adds many recipes to NC machine handlers and fission/heat-exchanger/turbine systems. `QMDCraftingRecipeHandler.java` supplies crafting/tool/armor/fuel conversions; `RecipesArmourDyes.java` supplies custom armor dye behavior.

Every family must preserve its complete inputs, outputs, quantities, energy/focus constraints, chance outputs and machine-specific extras. New datapack serializers should be designed with KubeJS schemas together so scripts and reloads use the same recipe model.

The furnace migration also needs the old Nuclearcraft material policy. `QMDRecipes.java` branches on `NCConfig.ore_dict_raw_material_recipes`: false uses only QMD dust, while true registers every shared-ore dust. Both paths use `OreDictHelper.getPrioritisedCraftingStack` for the result. The current 18 furnace recipes implement only QMD-input/QMD-output conversions. Restore the equivalent configurable tag-input and preferred-mod-output behavior during the recipe/configuration phase. The original NC default value is external to this repository and must not be inferred from the QMD code; the current Neohaul reference does not expose those old names. There is no separate `use_all_crafting` switch in the inspected QMD config/crafting handler.

## Reviewable sequence

1. Establish the minimal NeoForge entry point, exact Neohaul dependency, reproducible build and required mod metadata. Remove the example registrations and template logging/configuration.
2. Port the ordinary material catalogue and particle domain as foundational slices. The 83-material mapping is tracked in `MATERIAL_MIGRATION.md`; a successful build is separate from in-game verification.
3. Add serialized particle storage/capability APIs and registry/data components before implementing cells, sources or beamline transfer. Preserve empty-state, energy, focus and NBT behavior, and validate malformed data safely.
4. Port QMD recipe types/serializers, default data and optional KubeJS integration. Replace CraftTweaker and GroovyScript rather than introducing those dependencies.
5. Restore one complete standalone-machine feature at a time, including its menu/screen, input/output automation, redstone, synchronization, recipes and resources.
6. Restore multiblock infrastructure and each accelerator/chamber/liquefier family with its full GUI. Match the legacy validation, thermal and beam physics behavior.
7. Restore Nuclearcraft fuel/radiation extensions, equipment, entities, rendering, recipe viewer integration and the player manual.
8. Run the build and headless server GameTests with and without KubeJS, covering interactions, synchronization and resource/data reloads as described in `TESTING.md`. Record exact results and keep rendered appearance explicitly unverified until separately observed.

## Completion criteria

No functional item or machine should be registered as an inert placeholder. All legacy GUI must return as interactive GUI. Missing systems remain explicit migration work; feature parity is not established by compiling a foundational slice. Runtime evidence belongs in `DEVELOPMENT_HANDOFF.md`, with the exact command and result.

