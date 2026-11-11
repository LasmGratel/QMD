# Implementation and parity audit

Updated 2026-11-10 against active `src/`, preserved `legacy-src/`, and the pinned `build/neohaul-upstream` reference (Neohaul 0.10.10, tag `v1.21.1-0.10.10`, commit `b4f6ef877eb33b9b87ff78f82b1d9cf07b5827a8`). Target: Minecraft 1.21.1, NeoForge 21.1.265 and Java 21.

Source presence and inherited native behavior are distinct from in-game verification. Exact build/GameTest commands, failures and final results are in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). GameTests do not verify rendered appearance or actual client interaction.

## Active systems

| System | Implemented scope | Evidence and limits |
| --- | --- | --- |
| Package organization | `block`, `block.entity`, `block.menu`, `client`, `client.screen`, `client.particle`; shared `registry`, `config`, `network`, `capability` | [PACKAGE_LAYOUT.md](PACKAGE_LAYOUT.md); legacy packages are not compiled or forwarded |
| Generated resources | Six providers in the isolated `src/data/java/` source set generate 2,433 models/states/recipes/tags/loot/native data files | [DATA_GENERATION.md](DATA_GENERATION.md); exact semantic equivalence, independent byte-for-byte regeneration, duplicate rejection and jar contents verified |
| Ore leacher | Native processor, automation/upgrades/radiation, full main/configuration GUIs, authoritative recipes, JEI and KubeJS | Existing machine/menu/packet/reload tests; rendered GUI unverified |
| Collectors and initial chemistry | World predicates, scanning, FE/fluid automation, bucket interaction, salt/air bootstrap and reloadable cooling policy | Real native processing chain to hydrochloric acid; no legacy collector GUI existed |
| Particle values and creative source | All 64 species, composition/antiparticles, bounded long energy, storage/capability, real full editor and redstone-gated emission/pulls | Codec/storage, menu ownership, persistence, loaded-chunk and real emission tests |
| Source/cell items | Five sources, nine cells, persistent/network data components, exact fluid exchange, partial values, shell returns and gamma hazards | [FISSION_AND_CELLS_MIGRATION.md](FISSION_AND_CELLS_MIGRATION.md); gamma rendering not visually tested |
| Beamline transport | Collinear loaded segment sharing, endpoint extraction, whole-line focus loss, topology invalidation and sample expiry | Real world capability tests; no forced loading |
| Six accelerator families | Linear, ring, decelerator, diverter, splitter, spectrometer; physical structures, materials/coolers, sources, thermal and beam physics, slots/GUI and automation | [ACCELERATOR_MIGRATION.md](ACCELERATOR_MIGRATION.md); optional old OpenComputers ABI replaced with common server-script state/control access |
| Particle chambers | Target, decay, collision, beam dump; geometry/detectors, reaction math, item/fluid/particle automation, persistent work and GUI | [CHAMBERS_MIGRATION.md](CHAMBERS_MIGRATION.md); 559 particle defaults |
| Vacuum chambers | Neutral containment/cell exchange and nucleosynthesis; coils/lasers, coolant/heater circuits, plasma, failure behavior and full controller screen | Chamber doc; 105 nucleosynthesis, nine neutral and two heating defaults plus component-aware cell exchange |
| Liquefier | Native HX structure/tubes plus compressors/nozzles/ports, coupled fluid/thermal/FE conversion, persistence and full GUI | [LIQUEFIER_MIGRATION.md](LIQUEFIER_MIGRATION.md); targeted pinned-upstream mixin |
| Irradiator and lamps | Actual reservoir-powered machine, sided slots/output modes, GUI, seven process/two fuel defaults; nine redstone lamp variants and infusion | [IRRADIATOR_MIGRATION.md](IRRADIATOR_MIGRATION.md); exact source depletion and output conservation tests |
| Native nuclear extensions | Hafnium shield, carbide reflector, strontium RTG/block, super-alloy blade, four MIX-291 fuels, waste/radiation, mercury cycle | Native controller behavior, persistent QMD types and actual native recipe-handler tests; full reactor plant not comprehensively tested |
| Progression resources | Source/cell/machine/part crafting, semiconductor/boule/wafer routes, superconducting wires/compounds, alloys, fuel manufacture/recovery, local metal melt/form routes | Every active main JSON parses; actual resource loading is checked separately by GameTests |
| JEI | All 19 legacy category families, custom particle ingredients/pages/bookmarks, catalysts, applicable item transfer, tank/beam lookups, authoritative server settings | [JEI_MIGRATION.md](JEI_MIGRATION.md); client discovery/layout/index/shortcuts not executed by headless tests |
| KubeJS | Optional particle lookup/value helpers, active serializers through `event.custom`, normal recipe removal/replacement/reload | [KUBEJS.md](KUBEJS.md); dedicated builders and startup extension registration still open |

## Corrections retained from the initial audit

The ore-leacher screen uses Neohaul's recognized side-configuration superclass so processor slots are guarded during configuration. Output mode changes commit immediately, preventing parent navigation from losing changes or duplicate close packets from voiding later output. Fluid configuration translates relative labels through the actual block facing. Existing GameTests exercise common actions, native packet handling and delayed acknowledgements; client navigation/rendering remains unverified.

The initial salt/acid bootstrap gap is resolved by salt-water collection and actual native chemistry. The expanded source work also fixes powered creative-source capability extraction, immutable fluid component identity, legacy cell payload cleanup, radiation overflow bounds, native reprocessor output-array loss, and fractional liquefier FE overcharging. These findings have focused automated coverage; final execution status belongs in the handoff.

## Remaining migration scope

- Tungsten-carbide tools/drills, HEV armor and energy/armor behavior, weapons and three projectile entities, meter, tablets, additional food/paint and armor dye behavior.
- Complete player manual and sound registry/resources, remaining beam/projectile rendering and comprehensive multiplayer client delivery/rendered GUI checks.
- Broader legacy chemistry and survival progression beyond the restored machine prerequisites; configurable shared-material input/preferred-output policy and remaining equipment/reactor configuration.
- Supported KubeJS startup registries for particles/composition/antiparticles, arbitrary sources and machine components/placement rules; particle-aware convenience builders and full legacy scripting parity. No CraftTweaker/GroovyScript implementation is planned.
- More exhaustive large/cross-chunk multiblock, quench/failure and native reactor plant coverage. Passing authored fixtures is not a claim that every possible structure/configuration has been verified.

The full legacy inventory and completion criteria remain in [MIGRATION_PLAN.md](MIGRATION_PLAN.md). No missing feature is intentionally removed as a migration shortcut. Registration counts and the absence of TODO markers are not measures of feature parity.
