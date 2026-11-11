# Particle and vacuum chamber migration

The active implementation uses `block.ChamberBlock`, `block.entity.ChamberBlockEntity`, `block.menu.ChamberMenu`, `client.screen.ChamberScreen`, `multiblock.ChamberMultiblock` and focused reaction, vacuum geometry and thermal helpers. Neohaul/ZeroCore provides physical connectivity, shell validation, assembly and world ticking. The legacy sources are reference only.

## Implemented sources

The four particle controllers have physical beam paths, sided receive/output beam capabilities, detector power and efficiency, real FE consumption, redstone suspension, item/fluid automation, output routing, exact beam snapshots, persistence and a controller inventory screen. Target and dump reactions accumulate actual particle work before consuming ingredients. Decay and collision apply their individual rounding, signed reaction energy, beam-energy balance and focus attenuation. Collision input order is shapeless; the product channels remain ordered. Removal of a shell part stops operation and clears output beams.

Neutral containment validates its two coil rings, two opposed lasers and perpendicular beam inputs. The nucleosynthesis controller validates the full 11 × 7 × 5 apparatus, beam pillars, nozzles, plasma-glass tunnel and four end process-fluid ports. Both require energy and paired cryogenic vents; nucleosynthesis also requires paired heater vents. Configurable heater placement expressions support exact counts, axial pairs, rooted dependencies and `&&` / `||`.

All apparatus parts retain the legacy six-face connectivity requirement. In nucleosynthesis, the upper plasma-glass rail needs a heater connection to the beam bridge or shell; a redstone heater between that rail and the beam bridge is a valid, functional connection. The lower and side rails touch the shell directly. Particle/vacuum shell glass uses cutout rendering, plasma glass uses translucent rendering, and all three disable opaque-face occlusion and suppress shared faces between matching glass blocks.

Vacuum operation starts at 293 K and must cool using live accelerator-cooling recipes. Cryogenic heat and fractional coolant work persist. Neutral containment accumulates both unordered input beams and stores exotic fluid; its two inventory slots fill, refill and empty the registered particle cells without losing their sub-mB contents. Cell exchange reads actual live recipe input/output item counts, fluid quantities, products and chance fields; arbitrary custom conversions use atomic output-capacity checks. Partial reservoir filling uses integer conversion quanta, preserving exact particle/fluid ratios. Nucleosynthesis uses both unordered reaction fluids, accumulates beam work and casing heat, and places the native Neohaul plasma fluid in the actual tunnel. Heater coolant conversion removes casing heat. Loss of containment invokes configured explosions and gamma/radiation effects; safely emptying both reaction inputs extinguishes plasma. Redstone ports expose apparatus/casing temperature signals.

The controller screen includes player inventory, machine slots where relevant, FE, machine state, beam contents, work, operating and casing temperature, all applicable fluid tanks, container interactions, enable/output-routing controls and validation-error tooltips. JEI supplies all eight chamber recipe families, catalysts, beam/fluid lookups, dynamic screen click regions, item-only transfer for targets/cells, energy bounds, ratios, reaction heat and chance-output details. A modern block-entity renderer restores the original rotating fluid cuboid and red laser prism, with compact snapshots sent to tracking observers even without an open menu. Source additions have not established rendered appearance or real client packet delivery.

## Data and progression

| Recipe family | Main JSON entries |
| --- | ---: |
| Target chamber | 475 |
| Decay chamber | 40 |
| Collision chamber | 39 |
| Beam dump | 5 |
| Neutral containment | 9 |
| Nucleosynthesis chamber | 105 |
| Cell fill/refill/empty | 27 |
| Vacuum chamber heating | 2 |

The recipe tables preserve the legacy channels, including explicit empty products. Native Neohaul isotope fluids are linked through common fluid tags, and QMD's copernicium alloy forms join the target isotope tag. The migration includes all 17 particle and 24 vacuum part assets, original textures, blockstates, models, loot, mining tags, crafting, all 22 waste items and the bubble/wire detector fluid-infuser routes. English, Russian and Chinese preserve available legacy block/category names; new controls use English fallback text in those language files.

`qmd-chambers.toml` controls particle chamber power, energy/tank capacities, detector power/efficiency and legacy reaction scaling factors. `qmd-vacuum.toml` controls dimensions, component power/heat/temperature, heater cooling/rules and containment hazards. Server config is authoritative for operation. Datapack ingredient amounts describe baseline work; non-default server reaction-scale factors alter required work at runtime and synchronize to JEI for scaled ingredient counts and exact work tooltips.

## Datapacks and KubeJS

All families are registered recipe types and serializers. KubeJS can add/remove them with standard recipe events; no CraftTweaker or GroovyScript classes are introduced. Example:

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'qmd:beam_dump/hydrogen' })
  event.custom({
    type: 'qmd:beam_dump',
    particleIngredients: [{ particles: ['qmd:proton'], amount: 2000,
      min_energy: 0, max_energy: 9007199254740991, min_focus: 0 }],
    fluidProducts: [{ ingredient: { tag: 'c:hydrogen' }, amount: 1 }]
  }).id('example:proton_dump')
  event.custom({
    type: 'qmd:vacuum_chamber_heating',
    fluidIngredients: [{ ingredient: { tag: 'c:mercury' }, amount: 1 }],
    fluidProducts: [{ ingredient: { tag: 'c:high_pressure_mercury' }, amount: 2 }],
    heat: 256
  }).id('example:mercury_heating')
})
```

JavaScript cannot represent every 64-bit integer exactly. Use safely representable energy bounds in script literals or author an ordinary JSON recipe for exact large values. Fields are `itemIngredients`, `fluidIngredients`, `particleIngredients`, `itemProducts`, `fluidProducts`, `particleProducts`, `crossSection`, signed `energyReleased`, `radiation`, `heat` and `temperature`. Item/fluid ingredients use Neohaul's sized-chance format. Particle inputs use `particles`, `amount`, `min_energy`, `max_energy`, `min_focus`; products use `particle`, `amount`, `meanEnergy`, `focus`. The channel counts below are exact; retain explicit empty entries for unused channels.

| Kind | Item in | Fluid in | Particle in | Item out | Fluid out | Particle out |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `target_chamber` | 1 | 1 | 1 | 1 | 1 | 3 |
| `decay_chamber` | 0 | 0 | 1 | 0 | 0 | 3 |
| `collision_chamber` | 0 | 0 | 2 | 0 | 0 | 4 |
| `beam_dump` | 0 | 0 | 1 | 0 | 1 | 0 |
| `neutral_containment` | 0 | 0 | 2 | 0 | 1 | 0 |
| `cell_filling` | 1 | 1 | 0 | 1 | 1 | 0 |
| `nucleosynthesis_chamber` | 0 | 2 | 1 | 0 | 2 | 0 |
| `vacuum_chamber_heating` | 0 | 1 | 0 | 0 | 1 | 0 |

An empty item is `{ ingredient: [], count: 0 }`, an empty fluid is `{ ingredient: [], amount: 0 }`, an empty particle input is `{ particles: [], amount: 0, min_energy: 0, max_energy: 0, min_focus: 0 }`, and an empty product is `{ particle: '', amount: 0, meanEnergy: 0, focus: 0 }`. Zero-channel arrays may be omitted; arrays with physical channels must be present.

## Verification boundary

Combined verification completed from `G:/QMD` on 2026-11-10:

| Exact command | Observed result |
| --- | --- |
| `.\gradlew.bat build compileGameTestJava --console=plain` | Passed in 23 seconds; all 80 JUnit tests passed with zero failures/errors/skips; release jar built and GameTests compiled. |
| `.\gradlew.bat runGameTestServer --console=plain` | Passed in 43 seconds; all 98 required GameTests passed (20.46-second suite). |
| `.\gradlew.bat runGameTestServer -PwithKubeJS --console=plain` | Passed in 48 seconds; all 99 required GameTests passed (23.03-second suite). |

Full transcripts are `build/reports/migration/build-expanded-final-5.log`, `gametest-expanded-final-2.log` and `gametest-expanded-kubejs.log`. These are whole-project results, including the chamber tests described below; the test totals are not chamber-only counts.

The first combined final run exposed two fixture defects: collision rotation assumed a fixed starting channel for the north outlet, and the nucleosynthesis upper glass rail lacked a connecting heater. The passing tests now check all four physical outlets through a complete rotation, connect the rail through a valid redstone heater, require all twenty glass blocks to belong to the assembled controller, and include the native validation message on assembly failure. These corrections do not relax machine geometry or connectivity.

Passing GameTests cover the physical beam dump, decay, target and collision chambers; energy and item/fluid/particle automation; output rotation; shell disassembly; persistence and live menu identity; real neutral-containment cooldown, unordered reactions and partial-cell exchange; real nucleosynthesis cooldown, process-fluid routing, plasma/casing heat and safe fuel exhaustion; player inventory conservation; authoritative resource reload of all 105 nucleosynthesis entries; arbitrary custom cell recipe counts/products and blocked transaction conservation; and compact visual updates preserving full inventory/energy. `ParticleIngredientTest` covers energy/focus/count boundaries and immutable alternatives. The fixtures do not inject reaction progress or temperature.

Headless tests do not verify actual client screen/renderer delivery and appearance or actual client JEI rebuilding after server scale changes. Configured destructive hazard branches also remain unverified. These limits are distinct from the default machine behavior exercised above and do not establish full release parity.
