# Ore leacher migration

The first standalone processor port is `qmd:ore_leacher`. The block entity, inventory, tanks, upgrades, processing, persistence, sided capabilities and interactive GUI use Nuclearcraft Neohaul's processor framework. QMD supplies its own recipe type/serializer, the original texture and layout, and the seventeen legacy default recipes.

Implementation and verification are separate. The current command results and remaining client checks are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). The recipe unit tests exercise validation and the empty-tag fallback; they do not establish rendered GUI behavior, complete survival progression or compatibility with every optional ore provider.

## Machine contract

The machine has one item input, three independent 16,000 mB input tanks, three item outputs, and speed/energy upgrade slots. Defaults are 400 ticks and 50 FE/t before recipe and upgrade multipliers. These defaults are configurable as `processors.ore_leacher_time` and `processors.ore_leacher_power`. Neohaul computes energy capacity from the maximum loaded recipe requirements; with the default 1× recipes and no upgrades the base capacity is 20,000 FE.

The machine requires sufficient cycle energy before starting and consumes inputs when a cycle finishes. Energy starvation loses progress at 1.5 times the speed multiplier per tick, matching the legacy processor. An enabled redstone halt preserves partial progress while inputs remain valid. The inherited radiation source is exposed by Neohaul's capability registration for every registered block entity implementing its `ITile` interface.

The full processor screen retains the legacy 176×166 layout, energy display, three fluid displays, both progress strips, upgrade slots, redstone toggle and side configuration. Neohaul supplies the per-face item/fluid sorption dialogs, output settings, tank clearing and server synchronization, with QMD adapters for the defects below. Gameplay, menu and synchronization verification follows the user's GameTest-only testing preference in [TESTING.md](TESTING.md). These headless checks do not establish rendered appearance.

The three screen-path defects identified in the [implementation audit](IMPLEMENTATION_AUDIT.md) are now corrected in QMD's source:

- Side configuration extends Neohaul's recognized `UpgradableProcessorScreen.SideConfigScreen`, so its machine-slot guards apply over the sorption buttons. QMD overrides progress drawing to retain both legacy strips.
- The item-output dialog cycles its local selection, persists the selected mode and sends Neohaul's update packet immediately on each click. Older server acknowledgements cannot rewind that local selection. Returning to the parent does not resend the mode; this also avoids a second `VOID` packet clearing output added after the original click.
- Fluid-input dialogs replace Neohaul's world-direction array with bottom/top/left/right/front/back relative to the machine facing. Inherited buttons, tooltips, resets and packets use the corrected mapping. Block icons keep Neohaul's fixed presentation order, matching item dialogs, so their front/back pictures do not rotate with the world-facing addresses.

`OreLeacherGuiActions` contains the common-safe direction and output-click behavior. Four `OreLeacherGuiActionsGameTests` cover all four horizontal facings, distinct/opposite directions and independent arrays; every output slot, mode and cycle direction with state stored before exactly one sender callback; the real output-setting packet codec and pinned handler's preserve/void behavior; and older acknowledgement NBT arriving between clicks without rewinding the dialog's selection or clearing newly added output. They passed in both final expanded suites (34 base tests / 35 with KubeJS). They do not instantiate client screens, establish rendered slot visibility or verify real client input/navigation and network delivery. Complete GUI appearance and client interaction remain unverified.

Known upstream keyboard limitation: Neohaul's `NCScreen.isEscapeKeyDown()` recognizes Escape by scan code `1` rather than GLFW key code `256`. On platforms with a different Escape scan code, inherited side-config, fluid-input, item-input and upgrade dialogs can close the whole machine instead of returning to their parent. Inventory-key return is unaffected, and QMD's output adapter overrides the close path. This is a source finding; headless tests do not verify physical keyboard or platform behavior.

The recipe handler is registered under the namespaced key `qmd:ore_leacher`. It extends Neohaul's `BasicProcessorRecipeHandler` so `RecipeStats` includes its processing time/power when calculating capacity. The inherited handler already resolves namespaced recipe types and resets its recipe cache during resource reload. QMD does not patch Neohaul's handler map or retain a separate recipe-manager cache.

## Server integration verification

Before the GUI corrections and collector expansion, `.\gradlew.bat runGameTestServer` passed on 2026-11-07 in 24 seconds with all 14 required GameTests successful and the pinned Neohaul release, with KubeJS absent. This historical run does not verify the subsequent additions. Its coverage includes:

- A complete world-ticked gold cycle producing three ingots and consuming one ore, 16 mB of each acid and the expected energy; insufficient cycle energy and blocked outputs; automation simulation and side settings; radiation capability registration; upgrades and actual redstone halt/resume; legacy starvation decay; and complete machine NBT persistence.
- The registered menu factory and position-buffer constructor, all 42 machine/player slots and item filters, update-listener lifecycle, shift-click routing for ore/products/upgrades, conservation when the player inventory is full, distance limits, and invalidation after the machine is removed or replaced. QMD's menu now checks the current block entity identity and removal state in addition to Neohaul's distance check.
- All four placement facings and eight facing/active combinations preserving the same block entity, inventory, energy, progress and valid menu; real processing start/halt transitions updating the active block state.
- Independent processor-update snapshots, network encoding/decoding, applying energy/progress/power/tank/processing state to a replica, and clearing stale displays. These checks exercise synchronization data and application without claiming real client packet delivery.
- Loaded-registry data/network recipe roundtrips and asynchronous server resource reload replacing the handler's cached recipe instances while retaining default recipe data.

The final collector/GUI follow-up passed `.\gradlew.bat runGameTestServer` with all 34 required tests and `.\gradlew.bat runGameTestServer -PwithKubeJS` with all 35; both commands completed in 26 seconds. `.\gradlew.bat build` passed in 13 seconds with all 39 unit tests successful. These results supersede the historical run above. Broader command results and evidence locations are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md).

Tests use the test-only `qmd:empty` structure under `src/gameTest/resources/`, with a readable source in `src/gameTest/structures/empty.snbt`. Resource reload tests run in separate named batches. Headless menu interactions and state checks establish server behavior; rendered screens, model appearance, actual client clicks and packet delivery remain unverified. The base run does not execute KubeJS scripts.

Neohaul's shared GUI payload handlers remain an upstream limitation: they check loaded positions and world interaction permission, but do not require a matching open menu or player distance, and several handlers directly index supplied slot, tank or enum values. Tile-level overrides cannot cover enum decoding or connection lookups that occur before the tile method. QMD has not patched these upstream handlers, and malformed-payload behavior is not covered by the integration tests.

## Default recipes

Every recipe consumes one `c:ores/<ore>` item and 16 mB each from the fluid tags `c:nitric_acid`, `c:hydrochloric_acid` and `c:sulfuric_acid`. The main output count is three. Byproducts have count one unless specified below. All recipes use 1× time, 1× power and zero recipe radiation.

| Ore | Main product | First byproduct | Second byproduct |
| --- | --- | --- | --- |
| Iron | Iron dust, ingot fallback | Chromium dust 60% | Manganese dust 5% |
| Gold | Gold dust, ingot fallback | Silver dust 10% | Empty |
| Copper | Copper dust | Zinc dust 5% | Iridium dust 1% |
| Tin | Tin dust | Zirconium dust 25% | Tungsten dust 25% |
| Lead | Lead dust | Nickel dust 10% | Cobalt dust 5% |
| Uranium | Uranium dust | Thorium dust 10% | Niobium dust 10% |
| Thorium | Thorium dust | Titanium dust 15% | Hafnium dust 10% |
| Lithium | Lithium dust | Aluminum dust 10% | Empty |
| Magnesium | Magnesium dust | Calcium dust 10% | Potassium dust 5% |
| Boron | Boron dust | Salt dust: one guaranteed, second at 50% | Iodine dust 25% |
| Osmium | Osmium dust | Platinum dust 10% | Iridium dust 10% |
| Iridium | Iridium dust | Platinum dust 10% | Osmium dust 10% |
| Platinum | Platinum dust | Iridium dust 10% | Osmium dust 10% |
| Nickel | Nickel dust | Iron dust/ingot 25% | Aluminum dust 10% |
| Titanium | Titanium dust | Iron dust/ingot 25% | Manganese dust 10% |
| Silver | Silver dust | Lead dust 25% | Empty |
| Aluminum | Aluminum dust | Iron dust/ingot 25% | Empty |

Files live in `src/main/resources/data/qmd/recipe/ore_leacher/`. NeoForge tag conditions enable a recipe only when its ore and required output tags exist. This preserves recipes for ores supplied by other mods without producing invalid placeholder items when those mods are absent. Iron and gold accept either a dust or ingot output tag and prefer dust when available.

NeoForge represents an absent item tag with a named Barrier stack for ingredient display. A plain `[dust tag, ingot tag]` alternative would therefore make Neohaul choose Barrier as the first product when dust is absent. The five iron/gold fallback products use `neoforge:difference` with `minecraft:barrier` subtracted from those ordered alternatives. The first remaining stack supplies the product.

Chance products use Neohaul's `SizedChanceItemIngredient`: output quantity is `minStackSize + Binomial(count - minStackSize, chancePercent / 100)`. The boron salt output preserves a guaranteed minimum of one and a 50% chance for a second. Omitted trailing products are padded to three empty slots by QMD's recipe constructor.

## Acid production

Six supporting recipes use existing Neohaul processors. Fluid amounts in this table are mB; the salt input is one item.

| Processor | Inputs | Outputs | Time multiplier |
| --- | --- | --- | --- |
| Fluid enricher | Salt dust + 1,000 water | 666 sodium chloride solution | 1× |
| Electrolyzer | 1,332 sodium chloride solution | 1,000 hydrogen + 1,000 chlorine + 1,332 sodium hydroxide solution | 0.5× |
| Chemical reactor | 1,000 hydrogen + 1,000 chlorine | 2,000 hydrochloric acid | 1× |
| Chemical reactor | 1,000 nitrogen + 1,000 oxygen | 2,000 nitric oxide | 1× |
| Chemical reactor | 2,000 nitric oxide + 1,000 oxygen | 2,000 nitrogen dioxide | 1× |
| Chemical reactor | 3,000 nitrogen dioxide + 1,000 water | 2,000 nitric acid + 1,000 nitric oxide | 1× |

All six use a 1× power multiplier and zero recipe radiation. QMD registers hydrochloric acid, nitric acid, chlorine, nitric oxide, nitrogen dioxide and sodium chloride solution with source/flowing fluids, fluid types, buckets and common fluid tags. Hydrogen, nitrogen, oxygen, sulfuric acid and sodium hydroxide solution come from Neohaul. The chemistry recipes retain the legacy conversion ratios.

The initial salt bootstrap now exists: the ocean liquid collector produces QMD salt water, 10,000 mB salt water can crystallize directly into one salt dust, and the distiller converts 10,000 mB salt water into 666 mB sodium chloride solution plus 9,000 mB water. Crystallizing 666 mB solution also produces one salt dust. The expanded integration suite ticks actual crystallizers, an enricher, an electrolyzer and a chemical reactor from collected salt water through produced salt to hydrochloric acid. QMD additionally registers salt water, compressed air, liquid air, argon and neon for this slice. These additions resolve the earlier salt bootstrap gap; complete survival progression, a player-built distiller and client behavior remain unverified. See [COLLECTORS_MIGRATION.md](COLLECTORS_MIGRATION.md).

## Recipe-browser integration

`compat.jei.QMDJeiPlugin` now registers a dedicated JEI category, current server recipe holders, machine catalyst, both progress-area shortcuts and acid-tank lookups. The category retains the original background and both animated progress strips, exact item/fluid alternatives and quantities, chance/minimum/mean output tooltips and configured time/power/energy/radiation details. Raw ingredient alternatives are copied without rolling Neohaul's chance outputs. Padded empty tanks and products are omitted from layout registration.

Recipe transfer uses only item input slot 0 and player slots 6–41. It filters fluid recipe slots before invoking JEI's standard transfer helper; outputs and upgrades are excluded. Acid remains a searchable fluid input and is supplied through the machine's fluid interface. Fresh recipe snapshots are gathered on every JEI registration; JEI 19.70 restarts after synchronized recipe updates, covering datapacks and KubeJS replacements.

JEI is optional client metadata and the plugin is discovered through `@JeiPlugin`; common mod setup does not load it. The user's full JEI development dependency remains in place. See [JEI_MIGRATION.md](JEI_MIGRATION.md) for supported categories, tests and client verification limits. EMI has no QMD adapter; it is separate future integration work. Headless tests do not establish rendered arrows/tooltips, actual shortcut keys or client transfer packets.

## Recipe format and KubeJS

The Minecraft recipe type and serializer are both `qmd:ore_leacher`. Required fields are `itemIngredients` with exactly one entry, `fluidIngredients` with exactly three entries, and `itemProducts` with one to three entries. `fluidProducts` is optional and must be empty. Modifier defaults are `timeModifier: 1`, `powerModifier: 1`, `radiation: 0`.

Inputs require positive counts/amounts and deterministic consumption. Item products require a positive count, chance from 1 through 100, and an optional minimum count no greater than the maximum. Explicit empty products are allowed after the primary product using `{ ingredient: [], count: 0 }`. Omit `minStackSize` for zero: Neohaul's field codec accepts positive explicit minima and supplies zero when omitted. Time must be finite and positive; power/radiation must be finite and nonnegative. Both data and network decoding enforce machine dimensions and these bounds.

KubeJS can add, replace and remove these data-driven recipes through its standard server recipe event. Save this example in `kubejs/server_scripts/qmd_ore_leacher.js` and reload server resources:

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'qmd:ore_leacher/gold' })
  event.custom({
    type: 'qmd:ore_leacher',
    itemIngredients: [
      { ingredient: { tag: 'c:ores/gold' }, count: 1 }
    ],
    fluidIngredients: [
      { ingredient: { tag: 'c:nitric_acid' }, amount: 16 },
      { ingredient: { tag: 'c:hydrochloric_acid' }, amount: 16 },
      { ingredient: { tag: 'c:sulfuric_acid' }, amount: 16 }
    ],
    itemProducts: [
      { ingredient: { item: 'minecraft:gold_ingot' }, count: 3 }
    ],
    timeModifier: 1,
    powerModifier: 1,
    radiation: 0
  }).id('qmd:ore_leacher/gold')
})
```

This example deliberately replaces the gold recipe with one guaranteed product so a pack can verify add/remove/reload behavior. Add optional products with `chancePercent` and `minStackSize` as required. A specialized `event.recipes.qmd.ore_leacher` builder is not registered; `event.custom` uses the actual serializer.

The final `.\gradlew.bat runGameTestServer -PwithKubeJS` run passed in 26 seconds with all 35 required tests successful, zero script errors and zero failed recipes. This repeatable configuration syncs tracked scripts from `src/gameTest/kubejs/` and conditionally adds a required KubeJS integration GameTest. Its assertions cover removal/replacement and changed recipe values after resource reload for both ore-leacher and collector recipes, along with startup/server bindings, exact long energy and invalid-input rejection. See [KUBEJS.md](KUBEJS.md) and the handoff for evidence locations. These server-side checks do not establish the remaining scripting migration or rendered GUI behavior.

## References

- Legacy recipe behavior: `legacy-src/main/java/lach_01298/qmd/recipes/OreLeacherRecipes.java` and the supporting legacy chemistry handlers.
- QMD implementation: `recipe/OreLeacherRecipe.java`, `recipe/QMDMachineRecipes.java`, `machine/OreLeacherMachineInfo.java`, `machine/OreLeacherBlockEntity.java`, `machine/OreLeacherMenu.java`, `machine/OreLeacherScreen.java` and its adapters in `machine/client/`.
- Neohaul reference: `E:/Nuclearcraft-Neohaul/src/main/java/com/nred/nuclearcraft/` processor entities, recipe handlers, menus, screens and `RecipeStats`.
- [KubeJS recipe documentation](https://kubejs.com/wiki/tutorials/recipes).
- [NeoForge 1.21.1 recipe documentation](https://docs.neoforged.net/docs/1.21.1/resources/server/recipes/).
