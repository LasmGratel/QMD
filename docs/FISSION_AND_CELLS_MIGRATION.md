# Native fission, turbine and particle-cell integration

This implementation targets Nuclearcraft-Neohaul 0.10.10. Its native machinery supplies the full operating logic; QMD registers its own valid block-entity types for hafnium shielding, strontium RTGs and super-alloy turbine blades. These subclasses override the native constructors' hardcoded type identity so world persistence can restore QMD blocks correctly. The tungsten-carbide reflector uses Neohaul's reflector data map.

## Nuclear materials and progression

The nine QMD isotopes, copernicium-291 material forms, MIX-291 fuel forms and depleted forms have individual registry IDs, original textures, common tags and native radiation mappings. Radiation mappings are reapplied through Neohaul's refresh callback. The four fuel forms use actual native solid/pebble fission recipes, preserving lifetime, heat, efficiency, criticality, flux, decay and self-priming parameters.

Fabrication, oxide/nitride/zirconium-alloy conversion, TRISO assembly, reduction and depleted-fuel reprocessing use native processor serializers. Reprocessing retains isotope yields, waste probabilities and form-specific graphite/silicon-carbide/zirconium returns. The legacy MIX-291 fission-product fraction 0.5 and ruthenium fraction 0.6 produce 30% ruthenium and 20% europium channels. Twenty spallation and two fission waste streams have recovery recipes. Ordinary local metals have native melting and ingot-forming routes; accelerator prerequisite recipes restore semiconductor and alloy production.

Hafnium shielding inherits native moderator lines, shielding, heat clusters and manager behavior. Strontium RTGs inherit native assembly and extractable energy generation. Super-alloy blades inherit native rotor structure, expansion, efficiency, rendering state and failure handling. Their default efficiency and expansion are 1.1 and 1.3. QMD does not introduce separate fission or turbine controller GUIs; these parts use the complete native controllers and screens.

Six native mercury recipes restore the cycle: mercury/hot mercury to high-pressure mercury through fission heating, turbine expansion to exhaust mercury, exhaust recovery through heat exchange or condensation, and final hot-mercury condensation. Native JEI categories discover these recipes and the reflector data map; duplicating native categories would split the same processing behavior across viewers.

## Reservoir items and fluid exchange

`registry.QMDParticleItems` registers five source items, nine 100,000-particle cells and the empty cell shell. Persistent/networked `qmd:particle_amount` stores the remaining amount; fluid identity and its component patch use an immutable `CellFluid` component. Legacy `particle_storage` custom data imports existing partial values. Operations retain unrelated item custom data and components, and remove reserved legacy reservoir data when the cell becomes empty.

Sources can stack to sixteen; cells stack to one. Processing consumes one source unit at a time. A new reservoir is implicitly full, and creative/default instances carry an explicit full amount. Depleted sources disappear; depleted cells return their shell. Native item fluid capabilities exchange 1,000 particles per mB with a 100 mB capacity. Simulation never mutates the stack. Species, fluid components, partial fill/drain and sub-mB particle remainders remain exact.

Dropped cells preserve the legacy lifetime, lava/burning/destruction hazards and configured radiation/explosion strength. Detonation clears the item before spawning a gamma flash, preventing repeated destruction callbacks. The gamma entity saves its size and remaining lifetime, applies native radiation and fatal gamma damage, and expires after 500 ticks. Its dose is bounded before Neohaul's shielding calculation to keep intermediate arithmetic finite even when configured radiation multiplication overflows. Client rendering uses the retained expanding ray effect. Exotic fluid blocks also implement server-side annihilation. `qmd-particle-cells.toml` controls the hazard values; a zero dropped lifetime disables dropped-cell age, fire and destruction hazards.

## Utilities and verification

Nine discharge-lamp variants retain inverted redstone operation: lit while unpowered, off while powered. Their original textures, crafting and eight native infusion routes are present.

`FissionCompatGameTests`, `TurbineCompatGameTests`, `ParticleReservoirGameTests` and `DischargeLampGameTests` passed in the final headless runs with and without KubeJS. They cover native type/persistence and recipe integration, all waste radiation mappings, exact fluid exchange and legacy shell cleanup, cell network/persistent codecs, gamma lifetime and finite shielding arithmetic, lamp redstone, and native recipe channel retention. Channel validation applies to native processor serializers: QMD's ore-leacher serializer intentionally pads its declared outputs to match the machine slots. Explicit zero minima are omitted where upstream's positive-integer codec requires its default instead.

The loaded particle-recipe material test checks every positive item/fluid input and output channel for at least one real raw alternative and verifies fixed chamber channel counts. It caught the calcium-48 mass-spectrometer output alias, now supplied by `c:isotopes/calcium/48` forwarding to `c:ingots/calcium_48`. Conditional recipes skipped because optional materials are absent are outside that loaded catalogue check.

Verification from `G:/QMD` on 2026-11-10:

| Command | Result |
| --- | --- |
| `.\gradlew.bat build compileGameTestJava --console=plain` | Passed in 23 seconds; all 80 unit tests passed with zero failures/errors/skips |
| `.\gradlew.bat runGameTestServer --console=plain` | Passed in 43 seconds; all 98 required GameTests passed, suite time 20.46 seconds |
| `.\gradlew.bat runGameTestServer -PwithKubeJS --console=plain` | Passed in 48 seconds; all 99 required GameTests passed, suite time 23.03 seconds |

Evidence logs are `build/reports/migration/build-expanded-final-5.log`, `gametest-expanded-final-2.log`, and `gametest-expanded-kubejs.log` in that same directory. The small native turbine fixture verifies 2000 mB vapor to 6000 mB exhaust and actual stored FE. As documented in [TURBINE_MIGRATION.md](TURBINE_MIGRATION.md), pinned Neohaul can consume the first tick's fluid before initializing its power statistics; these results do not establish conservation of that initial theoretical energy.

[DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md) records the complete project verification history. Headless persistence, codec and render-state assertions do not establish complete reactor/turbine plant behavior, actual client packet delivery, rendered appearance, or GUI interaction. Equipment, weapons and the remaining player-oriented entities remain separate migration work.
