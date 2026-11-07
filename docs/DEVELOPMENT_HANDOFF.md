# Development handoff

## Target and starting state

Updated 2026-11-07. Target Minecraft 1.21.1, NeoForge 21.1.265 and Java 21. Mod identity remains `qmd` / `Quantum Minecraft Dynamics`.

Testing preference: the user explicitly requested NeoForge GameTest instead of the computer-use skill. Continue gameplay and menu verification through automated tests; do not request a desktop unlock or use desktop automation. Headless server tests do not establish rendered appearance, so record that limit without making it a prerequisite for further migration.

At the beginning of this session, `src/` contained the example mod, while the previous QMD implementation was preserved in `legacy-src/`. This handoff and `build.properties` did not exist. The working tree already contained extensive uncommitted migration/template changes and staged legacy-source moves; these were preserved throughout implementation. The foundation, ore-leacher/collector slices, tests and documentation are recorded together as a migration checkpoint; consult `git log` for its commit identity.

The baseline template passed `./gradlew.bat build` before implementation. That result did not establish any migrated QMD gameplay.

## Implemented foundation

- Replaced template registrations, sample food, logging and example config with the QMD entry point and material creative tab. Removed the unused empty mixin configuration.
- `build.properties` pins Nuclearcraft Neohaul **0.10.10**, Modrinth project `7hvoLzay`, file `TBpRjpDn`, plus ZeroCore Curse file `7344742`, TerraBlender `4.1.0.8`, and optional KubeJS `2101.7.2-build.368`. Runtime metadata requires the exact tested Neohaul release. Unrelated template integration dependencies were removed; they can return alongside their actual implementations.
- Registered 83 ordinary material items with flattened IDs, original textures/models, English/Russian/Chinese names, common item tags and 18 local-material furnace conversions. See [MATERIAL_MIGRATION.md](MATERIAL_MIGRATION.md) for every legacy ID/metadata mapping and the remaining configurable shared-material recipe policy.
- Ported all 64 registered particle definitions, physical properties, antiparticles and constituent relationships, with the original particle textures. Initialization is idempotent. Namespaced `qmd:` and legacy bare names resolve to the same definition. Registered definitions are frozen to prevent scripts from mutating the global catalogue.
- Ported particle stacks and the base storage/interfaces/snapshots to modern `CompoundTag`, `Direction` and `BlockEntity`. Preserved legacy serialization keys. Fixed first-insertion bounds, inclusive simulation/receive agreement, arithmetic overflow and partial extraction. Energy averages use exact integer arithmetic and retain legacy truncation/focus behavior.
- Ported the beam attenuation config with legacy default `0.02` and range `[0,1000]`. The rest of the legacy config remains to migrate with its systems.
- Added optional KubeJS plugin discovery and validated `QMDParticles` catalog/lookup/stack helpers, including exact long energies supplied as decimal strings. See [KUBEJS.md](KUBEJS.md). Custom particle/content registration remains to migrate. No QMD CraftTweaker or GroovyScript dependency/integration was introduced.

## First machine and GUI slice

- Registered `qmd:ore_leacher` with a real block entity, menu and full processor screen. Preserved the original GUI, both progress strips, three acid tanks, three item outputs, upgrade slots, redstone control, and per-face item/fluid configuration. Neohaul supplies processing, automation, persistence and synchronization. Config defaults remain 400 ticks and 50 FE/t. Energy starvation loses progress; redstone halt retains it.
- The menu checks removal and current block-entity identity as well as distance, preventing stale menus from remaining valid after removal or replacement. Server GameTests cover the registered menu factory, all 42 slots, item filters, listener lifecycle, shift-click routing and inventory conservation, alongside placement/facing/activity transitions and processor-update packet snapshots, codecs and application.
- Added the legacy machine crafting recipe, self-drop loot, pickaxe tag, translations and original front textures. All eight horizontal facing/active model variants use Neohaul's actual `nuclearcraftneohaul_active` property; an initial fresh-client launch caught and prompted correction of the shorter, invalid `active` property.
- Implemented namespaced recipe type/serializer `qmd:ore_leacher`, validated data/network codecs, 17 conditionally enabled legacy ore recipes, and six supporting chemistry recipes. Iron/gold dust-to-ingot fallbacks filter NeoForge's empty-tag Barrier representation. See [ORE_LEACHER_MIGRATION.md](ORE_LEACHER_MIGRATION.md).
- Added six complete fluid registrations: hydrochloric acid, nitric acid, sodium chloride solution, chlorine, nitric oxide and nitrogen dioxide, including sources, flowing forms, blocks, buckets, types, tags, fluid rendering and bucket tint registration.
- KubeJS supports the leacher through `ServerEvents.recipes` / `event.custom` and normal removal filters. Dedicated convenience builders and recipe-viewer plugins remain to migrate.
- Added a development-only GameTest source set and `qmd:empty` structure. `runGameTestServer` uses `run/gametest` to isolate test worlds/configs/logs from the client. GameTest classes/resources are excluded from the distributed main jar.
- Test sources and smoke recipes are selected only for the GameTest run through an alternate mod folder group. Normal client/server/data runs and unit tests select the main source set. KubeJS sync tasks own only the isolated startup/server script directories, removing stale fixtures without clearing the KubeJS root or configuration.
- [TESTING.md](TESTING.md) describes the headless verification workflow. CI now runs the build and GameTests both with and without KubeJS and uploads test reports/logs. Tracked test scripts are copied into the isolated server only when KubeJS is enabled.

## Collector, chemistry and GUI follow-up

- Ported `qmd:liquid_collector` and `qmd:atmosphere_collector` with 32,000 mB output tanks, all-sided energy input/fluid extraction and adjacent-tank output, the legacy 5×5×5 efficiency volumes, 100-tick refresh, world recipes, bucket transfer and empty-hand status. They were menu-free in the legacy mod; these interactions preserve their original behavior. Collection remains independent of redstone power, as in legacy.
- Registered `qmd:liquid_collector` and `qmd:atmosphere_collector` recipe types/serializers. World predicates use dimension resource IDs, biome IDs/tags and validated block-state properties. Output components survive data/network roundtrips; overlapping recipes use explicit priority and stable recipe-ID ordering. KubeJS supports the same serializers through `event.custom` and standard removal filters.
- Restored all four default collector recipe categories, retaining their rates and covering modern ocean/shore/swamp variants. Collection does not consume world fluid blocks. The liquid collector now consistently debits its own 50 FE default, fixing legacy's mismatched 50 FE threshold/100 FE debit. Zero-rate, incompatible-tank and full-tank collection no longer wastes power. Persistence revalidates world/recipe state on the first tick.
- Added five complete fluid families: salt water, compressed air, liquid air, argon and neon. There are now eleven QMD fluid families with sources, flowing forms, blocks, buckets, tags, localized names and client setup. Original collector textures are retained byte-for-byte.
- Added salt-water crystallization/distillation, sodium-chloride-solution crystallization, liquid-air separation and both cooling-policy branches for air, nitrogen and helium. The salt bootstrap no longer needs an externally supplied salt item. Neohaul disables its nitrogen/helium supercooler recipes when QMD is present, so QMD now supplies the appropriate replacements under both values of the reloadable `recipes.override_nc_recipes` setting (legacy default `true`). See [COLLECTORS_MIGRATION.md](COLLECTORS_MIGRATION.md) for exact quantities and limits.
- Corrected the three source-confirmed ore-leacher GUI defects: side configuration uses Neohaul's slot-guard-compatible superclass; item output modes commit immediately once per click; and fluid controls map relative faces to the machine's facing while keeping fixed icon presentation directions. Output cycling retains the dialog selection across delayed server acknowledgements. Progress strips and inherited controls are preserved. Headless helpers/packet tests cover the common logic; actual client clicks and rendered navigation remain unverified. The inherited platform-dependent Escape scan-code limitation is recorded in the ore-leacher document.
- Reviewed collector lifecycle/parity: block/biome sampling and output push use loaded-chunk checks, so they do not force neighboring chunks to load. Incomplete scans halt generation until the next scan while retaining energy/output. Atmosphere checks restore the legacy differences between partial wood/iron shapes, glass and circuit-like blocks. Configurable fluid-side sorption persists through NBT, and crouching suppresses bucket transfer while retaining empty-hand status.
- Added collector world/capability/persistence tests, collector recipe-codec/world-predicate tests, real salt-to-acid processor-chain tests and a separate cooling-policy resource-reload batch. Extended the tracked KubeJS smoke to check collector recipe removal/replacement and decoded values across manager reloads. Test content remains confined to the GameTest source set.

## Commands and verification

Run from `E:/QMD` in PowerShell. The wrapper uses the installed Gradle-compatible JVM and resolves a Java 21 compilation/game toolchain; the launcher JVM does not need to be Java 21 itself.

| Command | Purpose / observed result |
|---|---|
| `./gradlew.bat --version` | Gradle 9.8.2, launcher JVM Azul Java 26.0.2 |
| `./gradlew.bat build` (baseline) | Passed before template replacement |
| `./gradlew.bat build` (first foundation build) | Passed; 19 particle/storage JUnit tests, zero failures/errors/skips |
| `./gradlew.bat runClient` | Fresh Java 21 client loaded QMD with Neohaul 0.10.10, ZeroCore and TerraBlender, with KubeJS absent; further observations below |
| `./gradlew.bat build` (machine slice, before bucket-tint follow-up) | Passed; 39 tests: 22 particle/storage, 11 KubeJS facade, 6 recipe validation/fallback; zero failures/errors/skips |
| `./gradlew.bat runClient -PwithKubeJS` (foundation) | Passed and exited normally; startup helpers, server script bindings and recipe event executed with zero script errors |
| `./gradlew.bat runGameTestServer` (final expanded menu/sync suite) | Passed in 24 seconds after test isolation; all 14 required GameTests passed with KubeJS absent |
| `./gradlew.bat runGameTestServer -PwithKubeJS` (tracked fixtures) | Passed in 30 seconds; all 15 required GameTests passed, with zero startup/server script errors and zero failed recipes across initial load and both reloads |
| `./gradlew.bat build` (after menu validity fix) | Passed in 16 seconds; all 39 unit tests passed with zero failures/errors/skips. Jar includes the machine/menu/screen; no GameTest classes, bridge, scripts, smoke recipes, structures or legacy sources shipped |
| `./gradlew.bat build prepareClientRun prepareServerRun prepareDataRun` (after test isolation) | Passed in 15 seconds; all 39 unit tests passed and ordinary run configurations were prepared without launching a client/server/data generator |
| `./gradlew.bat runClient` (final machine sources/resources) | Fresh client completed asset/model loading with no QMD blockstate/model errors; desktop lock prevented GUI interaction. Stopped through the launching session with Ctrl+C (task exit 1 due to cancellation), not a completed client test |
| `./gradlew.bat runGameTestServer` (final collector/GUI follow-up) | Passed in 26 seconds; all 34 required tests passed, including chunk-boundary, fluid-side persistence and crouching-bucket regressions |
| `./gradlew.bat runGameTestServer -PwithKubeJS` (final collector/GUI follow-up) | Passed in 26 seconds; all 35 required tests passed, zero startup/server script errors and zero failed recipes across the initial load and reloads |
| `./gradlew.bat build` (final collector/GUI follow-up) | Passed in 13 seconds; all 39 unit tests passed, zero failures/errors/skips. Main jar has collector/GUI/recipe entries and excludes GameTest classes, bridge/scripts, smoke recipes, structure and legacy sources |

The first foundation build produced `build/libs/qmd-1.0.0.jar`. Later verification entries below supersede earlier results when sources change. A client run and unit tests are separate from a full release build, dedicated-server verification, or feature parity.

The first base client entered a local creative test world and all 83 material entries were visually inspected, including the three casing item models. Evidence logs are `build/reports/migration/client-base.log` and `client-kubejs-foundation.log` (ignored build artifacts).

The retained 14 ore-leacher GameTests exercise a real 400-tick gold-ore cycle (three ingots, 16 mB of each acid and full process energy), insufficient cycle energy, blocked outputs, automation simulation and per-face sorption, radiation capability registration, upgrades, actual redstone halt/resume, energy-starvation decay, machine NBT persistence, recipe data/network roundtrips and recipe-handler cache refresh after an asynchronous server resource reload. Four menu tests cover slot/filter/shift-click behavior, full-inventory conservation, update listener lifecycle, validity after removal/replacement, all four placement facings and all eight facing/activity combinations preserving the same block entity and its contents. Two synchronization tests cover independent snapshots, consecutive packet codec roundtrips, fractional progress, long energy, ordered tanks and clearing stale replica data. These check packet data/application, not actual client packet delivery.

The final base suite has 34 required tests. Twenty additions cover eight collector world/capability/persistence interactions, four collector codec/world-predicate checks, three salt/air chemistry checks, one cooling-policy reload test and four GUI-action/packet checks. The chemistry test ticks actual crystallizers, enricher, electrolyzer and chemical reactor using produced salt to reach hydrochloric acid; it does not inject process progress or replace recipes. The cooling test changes configuration, asynchronously reloads to the alternate recipe policy and reloads back. GameTest time advances faster than real-time gameplay. Final evidence logs are `build/reports/migration/gametest-base.log` and `gametest-kubejs.log`.

The optional KubeJS configuration uses tracked fixtures in `src/gameTest/kubejs/` and tracked test datapack recipes. A conditional test generator adds the thirty-fifth required test; a test-only Java bridge records startup, recipe-event and actual-manager assertions across script generations. It checks all 64 particle definitions, exact long energy and invalid-input rejection, actual ore-leacher/collector recipe removal/replacement, padded ore-leacher outputs, collector world/fluid/priority fields, and changed recipe values/new instances after asynchronous resource reload. Collector smoke recipes require an absent test biome tag so they cannot affect world fixtures. Script errors fail the required test. Other legacy scripting features remain pending.

The Gradle GameTest task clears stale logs and requires a positive required-test success summary. A temporary throwing startup script proved that a KubeJS bootstrap abort which exits the Java process with code zero still fails Gradle; the probe was removed afterward. Negative-test evidence is `build/reports/migration/gametest-negative-bootstrap.log`. An initial expanded optional run timed out after 600 unthrottled ticks while its background reload was still running; that failure is preserved in `gametest-kubejs-timeout.log`. Async integration tests need a wall-clock deadline as well as a generous tick ceiling.

Historical client checks did not establish interactive GUI or rendered facing/state parity. Subsequent testing follows the user's GameTest-only direction above. The initial machine client predated resource/tint fixes and was stopped through its launching Gradle session. Do not treat startup or server GameTests as rendered verification.

All 291 main JSON resources parse successfully. The built main jar contains 558 entries, with expected collector/GUI/recipe files and no GameTest or legacy content. `git diff --check` found no whitespace errors (only the existing wrapper line-ending warning). The historical client asset-loading log is preserved as `build/reports/migration/client-machine-assets.log`; it predates the collectors and establishes successful loading of that earlier resource set, not visual parity.

Resolved run-model inspection also passed: `./gradlew.bat help --no-configuration-cache --init-script E:/QMD/build/reports/migration/inspect-loaded-mods.init.gradle --console=plain` confirmed that client/server/data runs and unit tests select only `main`, while the GameTest server selects `main` plus `gameTest`. This one-off inspection script and its output are ignored build artifacts; evidence is `build/reports/migration/loaded-mods-inspection.log`.

Known upstream log issues: Neohaul's `create_turbine_rotor_bearing` loot table references an absent optional Create item, and ZeroCore's version checker may fail its SSL handshake. Both occurred during successful test runs and are distinct from QMD recipe or machine failures.

## Remaining migration work

The 2026-11-07 [implementation audit](IMPLEMENTATION_AUDIT.md) checks active registrations and legacy scope, including material progression blockers and missing particle runtime integration. Its three ore-leacher configuration defects and salt bootstrap gap now have source implementations and regression tests. Rendered GUI verification, broader material progression and recipe-viewer integration remain open.

The full inventory and implementation sequence are in [MIGRATION_PLAN.md](MIGRATION_PLAN.md). Beyond the ore leacher and two collectors, multiblocks, irradiator/other utility blocks and their GUI, particle capabilities, source/cell data components, particle network synchronization, radiation extensions, equipment/entities and the remaining fluids/recipes still require migration.

Continue particle capability/data-component contracts, specialized storage and source/cell/beamline consumers before particle-processing multiblocks. Restore missing material production routes and ore-leacher/collector recipe-viewer integration alongside the next complete machine slice. Extend GameTests with each new interaction. Preserve full interactive GUI and behavior for every migrated system.

Rendering validation is limited to content actually inspected in this checkout. No pump has been ported or visually verified during this session; the guide's previous pump note is not evidence for this starting state. Neohaul's inherited GUI payload handlers also lack comprehensive malformed-index/open-menu validation; this slice does not claim to harden those upstream handlers.

## References read

- [NeoForge 1.21.1 getting started](https://docs.neoforged.net/docs/1.21.1/gettingstarted/), registry/configuration documentation and ModDevGradle JUnit instructions.
- [NeoForge 1.21.1 Game Tests](https://docs.neoforged.net/docs/1.21.1/misc/gametest/): test holders, template namespaces, completion assertions, batch isolation and headless server runs.
- [KubeJS wiki](https://kubejs.com/wiki), startup/server script lifecycle and recipe documentation.
- `E:/Nuclearcraft-Neohaul` is the local source reference. Legacy `nc.*` imports require evaluating equivalent `com.nred.nuclearcraft.*` APIs: registration, block entities, menus/screens, payloads and recipe handlers have changed structure.
