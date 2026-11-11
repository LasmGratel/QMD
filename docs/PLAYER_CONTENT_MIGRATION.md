# Player content migration

## Scope

This slice restores the beam meter, potassium iodine tablets, three luminous-paint colors, and the complete player manual for Minecraft 1.21.1 / NeoForge 21.1. It also verifies the already-migrated flesh food contract without registering a duplicate.

Shared entrypoints, the shared language file, Gradle, generated output, and other feature packages were intentionally left unchanged. Exact merge lines are in `docs/agent-notes/player.md`; the Patchouli coordinate is also isolated in `docs/agent-notes/player-deps.md`.

## Beam meter

`BeamMeterItem` now opens a real no-slot menu and `BeamMeterScreen`; it does not replace the legacy interaction with chat text. The server captures the reading and sends an immutable snapshot containing:

- particle species and amount;
- mean energy;
- focus at the end of the complete beamline;
- focus loss per block;
- remaining travel distance;
- complete beamline length;
- accelerator input/output mode; or
- particle-chamber output channel.

Normal use scans the clicked block's particle capability without extracting from it. Beamline measurements copy the raw sample and apply `ParticlePhysics.focusLoss` for the complete line exactly once. An empty capability still opens an explicit `No Particles` display. Sneak-use reads output chamber channels and the mode of accelerator beam or synchrotron ports.

The menu captures its owner, hand, and original selected main-hand slot. It stays valid only while that same living player holds the meter in that exact location. It exposes no inventory slots. Both opening data and subsequent state payloads carry complete readings, and payload application requires the matching current menu and container ID.

## Tablets

`PotassiumIodineTabletItem` is always edible with zero nutrition and saturation. It consumes normally and updates Neohaul's entity radiation-immunity state with the legacy diminishing-return equation:

```text
newTime = current > lifetime ? current + lifetime * lifetime / current : current + lifetime
```

The `qmd-player.toml` setting `ki_time` defaults to 400 ticks and accepts values from 1 through `Integer.MAX_VALUE`. Weakness is always applied. The strict legacy overdose thresholds add Hunger, Slowness, stronger effects, Poison, Nausea, and magic damage at the original dose levels.

## Luminous paint

The three flattened items place distinct green, blue, or orange paint blocks. Each block:

- attaches to any of the six faces;
- occupies a 1/16-block-thick selection shape;
- has no collision or occlusion;
- emits light level 12;
- requires a sturdy supporting face and drops its associated color when support disappears; and
- contributes `RadSources.RADIUM / 16D` as both an item and placed block.

Radiation registration follows Neohaul's persistent `RadSources.RUNNABLES` reload pattern. The recipe table preserves all radium/promethium alternatives and outputs 16 paint items per craft.

## Flesh

No new flesh item is registered. The canonical item remains `QMDIrradiator.FLESH`, with nutrition 4, saturation modifier 0.1 (`FoodProperties.saturation() == 0.8`), and the existing food radiation-resistance registration. Its existing migrated texture remains the sole texture owner.

## Player manual

Patchouli `1.21.1-93-neoforge` is published for this target and is therefore used instead of a reduced custom guide. The migrated guide contains the complete legacy inventory:

| Content | Count |
| --- | ---: |
| Categories | 11 |
| Entries | 47 |
| Templates | 4 |
| Pages | 459 |
| Images | 72 |

`book.json` uses Patchouli's 1.21.1 data-pack location and `use_resource_pack: true`; localized categories, entries, templates, and images remain in the resource pack. Page and template component types are namespaced, stale metadata IDs and Nuclearcraft namespaces are updated, internal links are normalized, and machine prose calls the migrated machine the Ring Accelerator while retaining the physical term synchrotron radiation and the registered Accelerator Synchrotron Port name.

The OpenComputers category and both entries remain as 25 pages of migration documentation. They state that OC peripherals and `component.qmd_accelerator` are not reintroduced. They map the historical commands to `computerSnapshot()` and `computerSetPercentage(int)`, document `-1` as redstone control and `0..100` as direct percentage control, and enumerate the common server-side snapshot fields.

Eight retained manual pages reference weapon/equipment IDs owned by their separate migration slices. The chapters were preserved rather than silently removed.

## Recipes and assets

`QmdPlayerContentRecipes` defines the meter crafting recipe, four-tablet Neohaul assembler recipe, six paint recipes, and a guide-book recipe carrying the `patchouli:book = qmd:guide` data component. Integration must add its one catalog call and then run normal data generation.

The shared asset providers and legacy texture copies cannot be edited in this owned slice. Exact model, blockstate, loot, texture, language, and `docs/DATA_GENERATION.md` merge instructions are recorded in `docs/agent-notes/player.md`.

## Automated coverage

The new GameTests cover:

- meter menu ownership, hand/slot validity, zero-slot/inventory preservation, and dead-owner rejection;
- exact consecutive opening/payload codec round trips and current-menu/container scoping;
- immutable full-beamline attenuation snapshots;
- tablet consumption, first-dose immunity and Weakness, plus the strict first overdose threshold;
- real `BlockItem` placement for every paint color, facing, consumption, light, Neohaul radiation, and support loss; and
- canonical flesh nutrition and saturation.

The task explicitly prohibited `runGameTestServer`, so these tests exist but have not run in this worktree. Headless GameTests can establish server behavior but cannot establish rendered meter or Patchouli page appearance.

## Verification

All manual JSON parses successfully. Internal entry links and anchors resolve, every referenced migrated manual image exists, the listed stale IDs are absent, and the retained content counts match the legacy book. `git diff --check` passes.

The permitted compilation was run once with this task command:

```powershell
.\gradlew.bat compileJava compileGameTestJava --console=plain --no-configuration-cache -I build/tmp/player-verify.init.gradle
```

The temporary init script supplied the published Patchouli dependency, overlaid the missing full-tree sources from `G:\QMD` read-only, and included this slice's data recipe source in `compileJava`. Both tasks completed successfully. The only diagnostic was an existing removal warning for `DeferredRegister.createDataComponents` in the integration tree's `QMDDataComponents`; no player-content compilation errors were reported.

The isolated slice did not run a client, data generator, or GameTest server. The subsequent main-tree rescue now registers `QMDPlayerContent`, `PlayerContentConfig`, the meter screen and payload, creative entries, recipes and radiation callbacks. Flesh remains the single existing `QMDIrradiator.FLESH` item. Patchouli `maven.modrinth:nU0bVIaL:BIogJv2D` resolved and is declared as a required dependency.

Main-tree `runData` generated the player recipes, luminous-paint states/models, flat item models, and matching item-drop loot tables. Legacy meter, tablet and paint textures and the language fragment are integrated. All seven meter/player-content GameTests passed in the final 119-test base run; exact commands and transcripts are in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). No client was launched. Rendered meter and Patchouli book appearance, client clicks, and actual network delivery remain unverified.
