# JEI migration

The Minecraft 1.21.1 integration lives in `lach_01298.qmd.compat.jei`; `legacy-src/main/java/lach_01298/qmd/jei` remains the reference. The development dependency is JEI **19.70.0.469**, as selected in `build.properties`. Its transitive MezzConfig API also resolves from the filtered BlameJared repository. JEI is an optional client dependency in mod metadata. Neither common mod entry point loads the plugin; JEI discovers `QMDJeiPlugin` through `@JeiPlugin`.

## Implemented categories and interactions

| Category | Restored behavior |
| --- | --- |
| `qmd:ore_leacher` | Original background, both synchronized progress strips, exact item/fluid alternatives and quantities, all nonempty outputs, chance/minimum/mean details, configured processing time/power/total energy/radiation, machine catalyst, both progress click areas, acid-tank recipe/use lookups and item transfer. |
| `qmd:liquid_collector` | Exact fluid/components and full-efficiency rate, power, scan/efficiency guidance, complete scrolling block/state, biome/tag and dimension predicates, priority and recipe ID, machine catalyst and nonconsumed world-block lookup. |
| `qmd:atmosphere_collector` | Equivalent collector information with atmosphere-specific power and obstruction/scan guidance. |
| `qmd:particle_info` | All 64 registered species, counted constituent inputs, complete mass/charge/spin/strong/weak properties, antiparticle and scrollable description. Mass is shown in the definitions' actual MeV/c² units. |

Custom particle ingredients support localized search, original textures, full beam tooltips, species-based recipe/usage identity, and bookmark serialization. Species identity deliberately ignores beam count, energy and focus, matching the legacy integration. The ingredient codec retains those values, including exact signed-long energy limits, and rejects empty/unknown particles. All 66 legacy particle description translations and current JEI labels are present in English, Russian and Chinese. The creative-source preview supports recipe/use lookup of its acknowledged particle state.

Leacher transfer delegates to JEI's standard handler after selecting item INPUT recipe slots. The inventory contract is input slot **0**, player slots **6–41**; machine output slots **1–3** and upgrade slots **4–5** are excluded. Fluid slots remain INPUT for recipe search but never enter item transfer. Acids are supplied through fluid automation/container interactions. Raw ingredient arrays are copied without invoking Neohaul's chance-rolling methods. Validated recipes require all three acids; defensive layout registration also skips empty tanks and padded products.

## Recipe reload lifecycle

`QMDJeiRecipes.from(manager)` returns independent immutable lists of authoritative recipe holders sorted by recipe ID. Each plugin registration fetches the connected server's current client recipe manager; there is no static recipe cache or retained connection manager.

Machine statistics use an immutable server snapshot of the ten processing/source settings. QMD's config is COMMON and therefore is not automatically synchronized by NeoForge. A common clientbound payload precedes recipe synchronization on join and datapack reload and broadcasts changes when the relevant QMD, accelerator, irradiator or chamber config reloads. The client discards the snapshot on disconnect. These transport/data classes have no JEI or client imports, so they can load without JEI. Recipe layouts read the synchronized values when built; process tooltips query the current values.

The pinned JEI source (`mezz.jei.neoforge.startup.StartEventObserver`) handles `RecipesUpdatedEvent` by restarting registrations, including an empty recipe sync. Consequently datapack reloads and KubeJS removal/replacement enter the next registration from the newly synchronized manager. The headless tests verify the common snapshot/reload contracts; they do not execute JEI's actual client event or index rebuild.

## Verification

Use the commands and current results in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). Focused unit tests cover all 64 ingredients/info pages, identity/copy isolation, exact bookmark codecs, constituent counts, deterministic item/fluid alternatives with components, chance means, configured processing statistics, item-only transfer filtering, registered slot quantities/padded products and both progress masks/positions.

`JeiRecipeSnapshotGameTests` adds three required headless tests: complete authoritative family snapshots, full collector descriptions at the accepted 4,096-alternative limits, and an isolated asynchronous resource reload. With KubeJS it additionally checks actual removal/replacement and changed generation values while older snapshots retain their previous values. A fourth test exercises authoritative settings/payload snapshots independently of a divergent integrated-client cache. Unit tests also reject malformed settings packets and confirm that leacher display statistics use received server values. No client category/screen is loaded by these GameTests. Existing machine/menu/automation/persistence tests continue to cover the underlying runtime.

Headless GameTests and proxy layout tests do not verify rendered appearance, actual JEI keyboard/mouse shortcuts, bookmark UI, client index refresh or transfer packet delivery. No desktop automation is required or used. An eventual client check should inspect those interactions with real synchronized recipes, including a KubeJS replacement and an empty family after reload.

Optional-server loading was also checked with all 50 GameTests and no JEI/MezzConfig in the discovered mod list. The one-off `build/reports/migration/without-jei.init.gradle` contains:

```groovy
allprojects {
    configurations.matching { it.name.toLowerCase(java.util.Locale.ROOT).contains('runtime') }.configureEach {
        exclude group: 'mezz.jei'
        exclude group: 'net.mezzdev.config'
    }
}
```

Run it with `runGameTestServer --init-script build/reports/migration/without-jei.init.gradle --no-configuration-cache --console=plain`; the exact command/result and transcript are in the handoff. This excludes only runtime dependencies for that invocation and preserves the user's normal full JEI dependency.

## Expanded runtime categories

Client layouts now use the original machine and JEI textures, native recipe slots and current JEI drawable/scroll widgets. Fluid and particle lookup for accelerator/chamber screens follows the actual Minecraft widget bounds. See [GUI_MIGRATION.md](GUI_MIGRATION.md) for the layout, asset ownership and verification details.

All 19 legacy category families now have active registrations under `compat.jei`. The shared plugin delegates to `QMDAcceleratorJei`, `ChamberJei` and `IrradiatorJei`; the liquefier has its own discovered plugin. These registrations use real server recipe types and operating machines.

| Categories | Runtime and viewer integration |
| --- | --- |
| Accelerator source, cooling, mass spectrometer | Fourteen source, five cooling and 133 spectrometer defaults; actual ion-source/spectrometer inventory transfer, controller/cooler catalysts, thermal and chance information, live fluid/beam lookups. |
| Target, decay, collision, beam dump | All input/output beam channels, item/fluid alternatives, quantities, energy ranges, focus and reaction metadata; real controller shortcuts, target item transfer and synchronized beam/tank lookups. |
| Neutral containment, cell filling, nucleosynthesis, vacuum heating | Containment and plasma apparatus, cryogenic and casing thermal behavior, cell exchange and material channels; containment cell transfer and separate heating/filling shortcuts. |
| Irradiator and irradiator fuel | Process/source recipes, real input/source slots, fuel-specific transfer, process/source shortcuts, radiation and authoritative work/fuel-use values. |
| Liquefier and liquefier coolant | Native HX-based liquefier, both fluid conversions, pressure/temperature/heat/energy information, controller and part catalysts, matching GUI shortcuts. |

See [ACCELERATOR_MIGRATION.md](ACCELERATOR_MIGRATION.md), [CHAMBERS_MIGRATION.md](CHAMBERS_MIGRATION.md), [IRRADIATOR_MIGRATION.md](IRRADIATOR_MIGRATION.md) and [LIQUEFIER_MIGRATION.md](LIQUEFIER_MIGRATION.md) for operating details and test coverage. Native fission, reflector, turbine, heat-exchanger and condenser additions use Neohaul's existing JEI categories and recipe/data-map discovery.

Viewer synchronization now includes irradiator base work/particle usage, both ion-source multipliers, and target/nucleosynthesis work scales in addition to the original four processor values. Join/reload/config-change snapshots carry server values; the client cache resets on disconnect. The expanded packet uses protocol version 2. Other multiblock statistics depend on the assembled machine and are shown by its synchronized controller GUI.

The expanded sources passed the full build with 80 unit tests, all 98 base required GameTests, all 99 with KubeJS, and all 98 with JEI/MezzConfig excluded from runtime. Exact commands, timings and transcripts are in the handoff. Headless tests establish recipe/machine/menu contracts; they do not execute JEI's rendered client layout, client event rebuilding, bookmark UI or actual transfer packet delivery. EMI remains separate work.
