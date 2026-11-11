# Liquefier migration

The liquefier now extends the pinned Nuclearcraft-Neohaul 0.10.10 heat-exchanger implementation rather than introducing a second casing/tube system. QMD provides the controller, three compressor materials, nozzle, gas/liquid port and energy port. Neohaul supplies HX casing, glass, shell baffles, configurable tubes, coolant inlets/outlets, redstone and computer ports. All participate in ZeroCore's shared assembly, disassembly, chunk and saved-multiblock lifecycle.

## Structure and operation

- Build an HX cuboid using the configured Neohaul HX size limits. The interior needs at least three layers vertically.
- Leave the lowest interior layer empty. Fill the next layer with HX shell baffles and liquefier nozzles.
- Install compressors in the roof and join their open HX tube connections to the nozzles. Each nozzle needs its own connected tube circuit and at least one compressor. Every compressor and nozzle must be connected; compressor count must be at least nozzle count.
- Provide separate liquefier fluid ports in gas-input and liquid-output modes, at least one energy port, and HX coolant inlet and outlet ports above the nozzle layer. The Neohaul multitool toggles liquefier fluid port modes.
- Supply FE, gas and suitable coolant, then activate through the controller's redstone input, an HX redstone port or the HX computer interface. Liquid ports automatically push their output outward; shell outlets retain Neohaul's automation.

The original pressure, compressor efficiency, nozzle speed and heat-transfer formulas are retained. Pressure is `10 × (compressors / nozzles)²` bar. The coolant input must not exceed the gas inversion temperature, and its output must not exceed the compressed gas temperature. Tube cooling coefficients and the actual connected circuits bound processing. Both input inventories, both output capacities and available energy bound a tick before either fluid conversion is committed.

`qmd-liquefier.toml` preserves the original energy/tank density defaults (1000 FE, 640 mB gas and 10 mB liquid per exterior block), nozzle speed 0.4, and copper/neodymium/samarium-cobalt efficiencies. Shell tank densities follow the pinned Neohaul HX implementation. Compressor block recipes use Neohaul's steel chassis, servomechanism and electric motor equivalents.

## Recipes and JEI

`qmd:liquefier` and `qmd:liquefier_coolant` are server recipe types and serializers. Seven gas recipes (helium, nitrogen, hydrogen, neon, argon, oxygen and compressed air) and five coolants (liquid helium, liquid nitrogen, liquid neon, liquid argon and water) retain the legacy conversion quantities and properties. Their JSON definitions live in `data/qmd/recipe/liquefier/` and `liquefier_coolant/`.

Each recipe stores `ingredient`, `amount`, `output`, `heat`, `input_temperature` and `output_temperature`. Gas recipes also store `energy` and `pressure_coefficient`; their input temperature means inversion temperature and output temperature means compressed gas temperature. Numeric codecs reject invalid or nonfinite values. Runtime lookup reads the live recipe manager so removed or replaced datapack recipes take effect on existing machines.

`compat.jei.LiquefierJeiPlugin` displays both authoritative recipe types, fluid quantities, heat/energy, inversion and compressed-gas temperatures, pressure coefficients and coolant temperatures. Registered liquefier parts are catalysts. Controller status lines open their corresponding recipe categories. JEI remains optional and client-only.

## GUI, state and compatibility

The 176×100 controller screen retains the six status lines, dimensions/activity title and Shift modifier view. Normal view displays liquid output, coolant input with heat inefficiency, power with energy inefficiency, heat transfer, compressor/nozzle counts and pressure. Shift view displays gas input, coolant output, compressor energy efficiency, temperature difference, compressor heat efficiency and pressure efficiency. Hold Shift to enable clear-all-material.

Menu state is sent only to the current owner of a matching open menu and controller position. Clear actions validate menu ownership, container identity, loaded controller, assembled machine and player distance on the server. The controller retains the legacy slot-free menu. Gas/liquid storage, energy, pressure/efficiencies, recipe IDs and fractional conversion progress persist with multiblock NBT. The legacy duplicate NBT key for pressure efficiency and the exact-one fractional completion boundary are corrected; fractional conversions cannot overdraw fluid or output space.

The integration audit also corrected fractional FE accounting: sub-FE work is prepaid and its credit persists, so a low-energy custom recipe is charged for its total work rather than one FE on every partial tick. Energy is checked and committed before either fluid conversion; recipe replacement under the same ID discards progress associated with the previous recipe object. Clearing material directly through the logic clears both fluid circuits, energy and fractional credits. Calls on unassembled machines cannot process resources, malformed tank indices return empty/zero, and render snapshots return copied fluid stacks.

Neohaul hardcodes its HX logic dispatch. The targeted `HeatExchangerLogicMixin` recognizes only the `liquefier` ID and constructs `LiquefierLogic` from the previous logic, matching Neohaul's own logic factory convention. Its other logic IDs retain upstream dispatch. This integration is tied to the pinned Neohaul API and should be rechecked when updating that dependency.

## Verification

`LiquefierGameTests` exercises a real mixed Neohaul/QMD 5×5×5 cuboid: assembly and world ticking, fluid and energy capabilities, blocked outputs/inversion limits, fractional NBT persistence, menu ownership/clear/distance checks, invalid structure detection, all twelve loaded recipes and codec fields, recipe removal and actual server resource reload.

The final integrated `./gradlew.bat build compileGameTestJava` passed in 23 seconds with all 80 JUnit tests passing and zero failures, errors or skips (`build/reports/migration/build-expanded-final-5.log`). `./gradlew.bat runGameTestServer` passed in 43 seconds with all 98 required tests passing; the game suite completed in 20.46 seconds (`build/reports/migration/gametest-expanded-final-2.log`). `./gradlew.bat runGameTestServer -PwithKubeJS` passed in 48 seconds with all 99 required tests passing; the game suite completed in 23.03 seconds (`build/reports/migration/gametest-expanded-kubejs.log`). Both runs passed all nine liquefier tests, including the fractional-FE regression using real live recipe replacement and the assembled machine, and actual resource reload.

Build and GameTest results are recorded in `docs/DEVELOPMENT_HANDOFF.md` and `docs/TESTING.md` after integration. These tests do not verify rendered screen/model appearance.

The 2026-11-11 model-provider rewrite corrected controller, energy-port and fluid-port generated blockstates to use Neohaul's actual `nuclearcraftneohaul_active` property. Native variant generation validates that property against the registered blocks. `runData` passed; rendered liquefier appearance remains unverified.
