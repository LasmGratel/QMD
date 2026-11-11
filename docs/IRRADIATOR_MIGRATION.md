# Standalone irradiator migration

The active standalone `qmd:irradiator` uses a source reservoir to irradiate inventory items. This is separate from Neohaul's fission-reactor irradiator. It requires no FE.

The machine has an input at `(44,54)`, a source slot at `(80,21)`, and an output at `(116,54)`, plus the complete player inventory. The original front textures and GUI texture are retained. Cobalt-60 sources provide one work unit per tick; iridium-192 sources provide ten. Default processing time is 200 work units, with ten particle units spent per work unit. Source slots hold one independently depleted reservoir, preventing stacked source duplication. The final partial source contributes only the work its remaining particles can pay for.

The screen has a redstone toggle and a side-configuration screen with all six faces and all three slots. Each connection supports disabled, insert, extract, or both. Default input/source connections insert and output connections extract. Outputs automatically push through configured extraction faces into adjacent loaded inventories. Output modes retain all items, discard excess, or discard all. Menu changes use vanilla menu-button packets and require the matching open owner, a live machine, and distance within eight blocks. Input/source shift-click routing retains item data components. Machine replacement drops all inventory slots.

The COMMON configuration file `qmd-irradiator.toml` controls base time, particle fuel usage, and radiation resistance. The block exposes Neohaul's actual radiation-source and radiation-resistance capabilities, with legacy base resistance 10,000. The flesh product preserves nutrition four, saturation modifier 0.1, and Neohaul's food radiation-resistance value three.

Seven process recipes and two fuel recipes are registered as `qmd:irradiator` and `qmd:irradiator_fuel` serializers/types. They cover rotten flesh, both mushrooms, grass, mossy cobblestone, mossy stone bricks, and mossy cobblestone wall. Registry-aware codecs retain every recipe field. Runtime queries the current recipe manager for every tick, including after removal or resource reload. Progress, recipe identity, source components, connections, output mode, redstone control, and added shielding survive NBT persistence. Redstone pauses retain work; other inactive conditions lose 150 work units per tick, matching the legacy processor.

Optional JEI hooks in `compat.jei.IrradiatorJei` provide process/source categories, catalyst, input/source transfer handlers, and progress/source recipe shortcuts. Time labels use recipe multipliers explicitly; no client-local server timing is presented as authoritative.

## KubeJS recipe customization

Place server scripts under `kubejs/server_scripts/`. Both serializers support ordinary `event.custom(...)` recipes, removal, replacement, and `/reload`; they do not require Java class loading or legacy CraftTweaker/GroovyScript integration.

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'qmd:irradiator/rotten_flesh' })
  event.custom({
    type: 'qmd:irradiator',
    ingredient: { item: 'minecraft:rotten_flesh' },
    output: { id: 'qmd:flesh', count: 2 },
    multiplier: 1.5,
    radiation: 0.0
  }).id('kubejs:irradiator/rotten_flesh')

  event.remove({ id: 'qmd:irradiator_fuel/cobalt_60' })
  event.custom({
    type: 'qmd:irradiator_fuel',
    ingredient: { item: 'qmd:source_cobalt_60' },
    multiplier: 2.0
  }).id('kubejs:irradiator_fuel/cobalt_60')
})
```

An ingredient accepts a normal item ingredient or item tag, for example `{ tag: 'c:mushrooms' }`. Process `output` is a nonempty 1.21.1 item stack, including optional `components`. Process `multiplier` scales the configured base work required; fuel `multiplier` is work units per tick. Multipliers default to one and must be positive finite numbers. Optional process `radiation` defaults to zero and must be nonnegative and finite. Fuel recipes omit `output`; their ingredient must match a QMD particle reservoir for actual source depletion. Input ingredients consume one item per operation. Source usage remains the configured particles per work unit, so a faster source consumes proportionally more particles each tick. Recipe replacements become visible to the machine and JEI after the server reloads and synchronizes recipes.

All six `IrradiatorGameTests` passed in the final headless runs with and without KubeJS. They verify actual world ticking, particle consumption, blocked output, redstone pause, partial source exhaustion, sided automation, live sorption changes, persistence, menu slots/shift-click/ownership, real inventory output pushing, codec round trips, recipe removal, and asynchronous resource reload. The reload test uses a thirty-second wall-clock deadline and confirms that the same placed machine consumes source particles through newly loaded recipes afterward.

Verification from `G:/QMD` on 2026-11-10:

| Command | Result |
| --- | --- |
| `.\gradlew.bat build compileGameTestJava --console=plain` | Passed in 23 seconds; all 80 unit tests passed with zero failures/errors/skips |
| `.\gradlew.bat runGameTestServer --console=plain` | Passed in 43 seconds; all 98 required GameTests passed, suite time 20.46 seconds |
| `.\gradlew.bat runGameTestServer -PwithKubeJS --console=plain` | Passed in 48 seconds; all 99 required GameTests passed, suite time 23.03 seconds |

Evidence logs are `build/reports/migration/build-expanded-final-5.log`, `gametest-expanded-final-2.log`, and `gametest-expanded-kubejs.log` in that same directory. This verifies server behavior and menu/data contracts. Headless tests do not establish rendered appearance, actual client clicks, JEI shortcut behavior, or network packet delivery. The KubeJS examples above describe supported recipe JSON; the passing optional integration run does not imply that each example was executed verbatim.
