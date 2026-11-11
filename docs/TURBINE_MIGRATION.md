# Native turbine and mercury integration

`qmd:turbine_blade_super_alloy` is a native Neohaul turbine rotor blade with a QMD block entity type. It retains legacy efficiency 1.1 and expansion 1.3, configurable through `qmd-turbine.toml`. Neohaul owns rotor validation, controller attachment, bearing failure, activation, generation, inventory/fluid automation, and its complete turbine GUI. The block uses Neohaul's rotor-direction property and model geometry with the original QMD texture. Blade direction persists with native NBT and its render-state accessor returns the QMD block for every direction, including the native invisible state.

The mercury cycle uses native recipes. Fission heating converts 1 mB mercury into 2 mB high-pressure mercury at 512 heat, or 1 mB hot mercury into 2 mB high-pressure mercury at 256 heat. The turbine converts 1 mB high-pressure mercury into 3 mB exhaust at 192 FE per input mB with ideal expansion three. Heat exchange and condensation each convert 6 mB exhaust into 1 mB hot mercury at 128 heat and 700 K on both sides. Final condensation converts 1 mB hot mercury into 1 mB mercury at 256 heat, cooling from 700 K to 300 K. Recipes use the registered common fluid tags and native serializers, so native machines, JEI, datapack replacement and KubeJS `event.custom` operate on the same recipe manager.

Pinned Neohaul 0.10.10 computes a turbine tick's power limit before initializing the selected recipe's power statistics. Its first processing tick can therefore consume fluid and produce exhaust with a zero power limit; sustained flow generates power on subsequent ticks. QMD retains this native startup behavior. The verification checks exact fluid conversion and actual generated FE; it does not establish conservation of the initial batch's theoretical energy.

All three `TurbineCompatGameTests` passed in the final headless runs with and without KubeJS. The real 5×5×5 native turbine contains three shaft stages, twelve QMD blades, two bearings, two magnesium dynamos, inlet, outlet and controller. Verification established actual native assembly, subclass recognition, calculated rotor expansion, both conductive dynamo connections, actual world ticking from 2000 mB input to exactly 6000 mB exhaust, and generated stored FE. The supply spans multiple world ticks so the native rotor can spin up after initializing its recipe power. Separate tests passed for all blade NBT/render directions and actual native recipe-handler matching of all six mercury recipes.

The fission-material audit also restores MIX-291 reprocessing to native legacy probabilities: up to two ruthenium-106 dust at 30% per unit and up to two europium-155 dust at 20% per unit. TRISO additionally returns three graphite dust and one silicon-carbide alloy; zirconium-alloy fuel returns one zirconium dust. Native item radiation retains the legacy caesium-137 level for fission waste and half that level for every spallation waste variant. Reprocessing output channels and waste radiation are covered in `FissionCompatGameTests`.

Verification from `G:/QMD` on 2026-11-10:

| Command | Result |
| --- | --- |
| `.\gradlew.bat build compileGameTestJava --console=plain` | Passed in 23 seconds; all 80 unit tests passed with zero failures/errors/skips |
| `.\gradlew.bat runGameTestServer --console=plain` | Passed in 43 seconds; all 98 required GameTests passed, suite time 20.46 seconds |
| `.\gradlew.bat runGameTestServer -PwithKubeJS --console=plain` | Passed in 48 seconds; all 99 required GameTests passed, suite time 23.03 seconds |

Evidence logs are `build/reports/migration/build-expanded-final-5.log`, `gametest-expanded-final-2.log`, and `gametest-expanded-kubejs.log` in that same directory. Headless NBT/render-state checks do not establish rendered model appearance, native GUI clicks, or actual client packet delivery. The small verified turbine fixture does not establish every reactor/turbine plant arrangement or conservation of startup energy.
