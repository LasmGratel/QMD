# KubeJS migration

QMD targets KubeJS `2101.7.2-build.368` for Minecraft 1.21.1 on NeoForge. KubeJS is optional. The normal QMD entry point does not load the plugin; KubeJS discovers it through `kubejs.plugins.txt` when installed. Enable the development runtime with `./gradlew.bat runClient -PwithKubeJS` or `./gradlew.bat runServer -PwithKubeJS`.

CraftTweaker and GroovyScript integrations remain reference code in `legacy-src/`. The migration target for their functionality is KubeJS. QMD does not compile those integrations or add their dependencies.

## Available particle helpers

The `QMDParticles` global binding lists built-in particles, looks up their physical definitions, and constructs particle stacks carrying amount, mean energy and focus. Both legacy bare names such as `proton` and namespaced names such as `qmd:proton` work. A fresh stack is created for every call.

| Helper | Result |
| --- | --- |
| `QMDParticles.names()` | Snapshot list of registered particle names |
| `QMDParticles.catalog()` | Snapshot list of particle definitions |
| `QMDParticles.get(name)` | Definition with `getName()`, `getMass()`, `getCharge()`, `getSpin()`, `getAntiParticle()` and `getComponentParticles()` |
| `QMDParticles.stack(name)` | Stack with 1 pu, 0 keV and 0 focus |
| `QMDParticles.stack(name, amount)` | Stack with the given pu, 0 keV and 0 focus |
| `QMDParticles.stack(name, amount, meanEnergy)` | Stack with the given pu and keV, and 0 focus |
| `QMDParticles.stack(name, amount, meanEnergy, focus)` | Stack with all four properties |

Particle masses retain the legacy MeV/c² units, charge is in elementary charge units and spin is in units of ħ. Stack energy is in keV and amount is in particle units (pu). Focus retains the legacy inverse beam area meaning. Stack getters are `getParticle()`, `getAmount()`, `getMeanEnergy()` and `getFocus()`.

Use this diagnostic example in `kubejs/server_scripts/qmd_particles.js`. It runs whenever server resources load, including `/reload`:

```js
console.log(`QMD particles: ${QMDParticles.names()}`)

const proton = QMDParticles.get('qmd:proton')
console.log(`Proton mass: ${proton.getMass()}, charge: ${proton.getCharge()}`)

const beam = QMDParticles.stack('proton', 120, 1000000, 0.75)
console.log(`${beam.getParticle().getName()}: ${beam.getAmount()} pu, ${beam.getMeanEnergy()} keV, focus ${beam.getFocus()}`)

// Use a decimal string to preserve exact energies above JavaScript's safe integer range.
const highEnergyBeam = QMDParticles.stack('photon', 1, '9223372036854775807', 1.0)
```

Unknown or blank particle names fail with an error identifying the invalid name. Amount must be an integer from 0 through 2147483647. Numeric mean energy must be an integer from 0 through 9007199254740991; the four-argument overload accepts a decimal string through 9223372036854775807. The Java stack stores that exact long value; later JavaScript numeric conversion or arithmetic can lose precision above its safe integer range. Focus must be finite and non-negative. Invalid input raises a script error instead of silently clamping it.

These helpers inspect particle definitions and construct values. Creating a stack does not insert particles into a machine or register a recipe. Custom particle registration and definition mutation are not supported script APIs at this stage; their lifecycle, validation and synchronization need to be implemented together.

## Available recipe APIs

The plugin registers one-object convenience schemas for all 18 QMD machine serializers. Use `event.recipes.qmd.ore_leacher({...})` (or the corresponding serializer name) with native recipe fields and no `type` field. The serializer still validates the resulting data. `event.custom` remains available. The tracked ore-leacher fixture now uses this builder and passed initial loading and asynchronous reloads in the 120-test KubeJS rescue run, with zero script errors or failed recipes.

The ore leacher has a data-driven `qmd:ore_leacher` recipe type with validated data and network codecs. KubeJS can use `ServerEvents.recipes(event => event.custom({...}))` and normal recipe filters to add, remove and replace its recipes. See [ORE_LEACHER_MIGRATION.md](ORE_LEACHER_MIGRATION.md) for the format and a complete example.

Atmosphere and liquid collectors use `qmd:atmosphere_collector` and `qmd:liquid_collector`. Both use the same fields:

| Field | Meaning |
| --- | --- |
| `output` | Required fluid stack: `{ id: 'namespace:fluid', amount: positiveInteger }`; amount is mB per tick at full collection efficiency |
| `world.blocks` | Optional alternatives: `{ block: 'namespace:block', properties: { propertyName: 'value' } }`; omitted properties are unrestricted, and named properties/values must exist on that block |
| `world.biomes` | Optional biome resource locations |
| `world.biome_tags` | Optional biome tag resource locations, without a leading `#`; biome IDs and tags form one set of alternatives |
| `world.dimensions` | Optional dimension resource locations, replacing legacy numeric dimension IDs |
| `priority` | Optional integer, default `0`; higher matching priority wins, with recipe ID order breaking ties |

`world` itself is optional. Empty or omitted block, biome/tag and dimension categories impose no restriction; alternatives within a category are ORed and the categories are ANDed. Liquid collectors test the block directly below the machine; atmosphere collectors supply an air block state. The machines still apply area efficiency, energy and tank requirements after recipe selection. Normal scans refresh the selected recipe within 100 ticks of resource reload.

For example, save this rule in `kubejs/server_scripts/qmd_collectors.js`. It adds a higher-priority ocean/beach rule while retaining the default rules elsewhere:

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'qmd:pack/ocean_salt_water' })
  event.custom({
    type: 'qmd:liquid_collector',
    world: {
      blocks: [{ block: 'minecraft:water', properties: { level: '0' } }],
      biomes: ['minecraft:ocean', 'minecraft:deep_ocean'],
      biome_tags: ['minecraft:is_beach'],
      dimensions: ['minecraft:overworld']
    },
    output: { id: 'qmd:salt_water', amount: 1500 },
    priority: 10
  }).id('qmd:pack/ocean_salt_water')
})
```

Use `type: 'qmd:atmosphere_collector'` for atmosphere rules, normally omitting `world.blocks`; its default output is `qmd:compressed_air`. To replace a bundled recipe, remove its ID and assign that same ID to the replacement. All three recipe types use their actual serializers through `event.custom`; specialized `event.recipes.qmd.*` convenience builders are not registered.

Content registration belongs in `kubejs/startup_scripts/` and requires restarting the game. Recipe changes belong in `kubejs/server_scripts/` and support `/reload`. Avoid retaining mutable recipe-manager or level references across reloads.

## Expanded machine recipes

All active processing families support ordinary server recipe events, removal/replacement and `/reload`. Use the exact bundled JSON in `src/main/resources/data/qmd/recipe/` as a schema reference; IDs use modern flattened items and singular `recipe` resource directories. QMD defines no CraftTweaker or GroovyScript APIs.

| Serializer IDs | Schema and runtime details |
| --- | --- |
| `qmd:accelerator_source`, `qmd:accelerator_cooling`, `qmd:mass_spectrometer` | [Accelerators](ACCELERATOR_MIGRATION.md); source item/fluid and particles, coolant properties, spectrometer products and chance data |
| `qmd:target_chamber`, `qmd:decay_chamber`, `qmd:collision_chamber`, `qmd:beam_dump` | [Chambers](CHAMBERS_MIGRATION.md); ordered output channels and shapeless beam inputs, energy/focus and cross sections |
| `qmd:neutral_containment`, `qmd:cell_filling`, `qmd:nucleosynthesis_chamber`, `qmd:vacuum_chamber_heating` | [Vacuum chambers](CHAMBERS_MIGRATION.md); cell/fluid exchange, plasma, cryogenic and casing thermal properties |
| `qmd:irradiator`, `qmd:irradiator_fuel` | [Irradiator examples](IRRADIATOR_MIGRATION.md); process stacks, time/work multipliers, radiation and reservoir fuel speed |
| `qmd:liquefier`, `qmd:liquefier_coolant` | [Liquefier schema](LIQUEFIER_MIGRATION.md); fluid conversion, heat, temperatures, energy and pressure coefficient |

QMD's fission, waste recovery, material progression and mercury cycle additions use the corresponding native `nuclearcraftneohaul:*_recipe` serializers. They support the same standard KubeJS recipe events. The accelerator computer port also exposes `computerSnapshot()` and `computerSetPercentage(-1..100)` through normal server block-entity access; `-1` selects redstone.

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'qmd:liquefier/argon' })
  event.custom({
    type: 'qmd:liquefier',
    ingredient: { tag: 'c:argon' }, amount: 100,
    output: { id: 'qmd:liquid_argon', amount: 1 },
    heat: 20.0, input_temperature: 723, output_temperature: 300,
    energy: 100.0, pressure_coefficient: 0.1
  }).id('kubejs:liquefier/argon')
})
```

These values are an example customization, not the bundled balance. Resource reload replaces recipe instances and machine progress resets when its recipe changes. Avoid retaining recipe-manager references in scripts. The tracked integration smoke currently exercises ore-leacher/collector replacement and particle helper lifecycle; the new families' serializer/runtime reload behavior has Java GameTest coverage. This does not establish dedicated KubeJS builders or startup registration support.

## Remaining compatibility work

The legacy sources define the parity target. The following behavior still needs a KubeJS equivalent:

- Custom particle registration including texture, mass, charge, spin, weak and strong interactions; antiparticle and constituent relationships; particle ingredient alternatives and recipe matching.
- Startup registration of accelerator coolers and their placement rules, RF cavities, magnets, ion sources, particle chamber detectors, vacuum chamber heaters, liquefier compressors, and item particle sources. Source item configuration includes capacity, stack limit, empty item, explosion size and radiation.
- Typed particle-specific builders beyond the registered native-payload convenience schemas; scripted startup extension registration is separate work.
- GroovyScript recipe builders, registry lookup, recipe removal and reload behavior, translated to KubeJS conventions.

Collector world predicates now use dimension resource locations, biome IDs/tags and validated block-state properties. The expanded machine serializers and native-payload builders are active and use the current recipe manager. The startup extension lifecycle still needs migration. `QMDStartupCatalogue` restores the missing immutable definition types so the merged event compiles, but the plugin does not register/post `QMDStartupEvents.registry`, and no lifecycle code applies its snapshot. Those startup declarations are not an available script API yet.

## References and verification

- [KubeJS wiki](https://kubejs.com/wiki)
- [Startup scripts](https://kubejs.com/wiki/folder-structure/startup-scripts)
- [Server scripts](https://kubejs.com/wiki/folder-structure/server-scripts)
- [Editing recipes](https://kubejs.com/wiki/tutorials/recipes)
- Neohaul reference plugin: `E:/Nuclearcraft-Neohaul/src/main/java/com/nred/nuclearcraft/compat/kubejs/ModKubeJSPlugin.java`.
- Legacy compatibility references: `legacy-src/main/java/lach_01298/qmd/crafttweaker/` and `legacy-src/main/java/lach_01298/qmd/groovyscript/`.

The plugin structure follows Neohaul's KubeJS plugin API. On 2026-11-07, the final `.\gradlew.bat runGameTestServer` passed all 34 required tests with KubeJS absent, and `.\gradlew.bat runGameTestServer -PwithKubeJS` passed all 35 with zero startup/server script errors and zero failed recipes across initial load and resource reloads. Both commands completed in 26 seconds. `.\gradlew.bat build` also passed in 13 seconds with all 39 unit tests successful. These results include the collector assertions and GUI corrections below.

The 2026-11-10 particle-source follow-up also passed `.\gradlew.bat runGameTestServer -PwithKubeJS --console=plain`: all 47 required tests passed in 51 seconds, including the added source reload batch and the existing scripting smoke. Startup/server scripts reported zero errors and zero failed recipes across loads/reloads. The base run passed 46 tests and the release build passed all 47 unit tests. This adds runtime particle-source behavior without introducing new KubeJS content-registration APIs. See [PARTICLE_RUNTIME_MIGRATION.md](PARTICLE_RUNTIME_MIGRATION.md) and the current [handoff](DEVELOPMENT_HANDOFF.md).

`.\gradlew.bat runGameTestServer -PwithKubeJS` syncs the tracked startup/server fixtures from `src/gameTest/kubejs/` into the corresponding script directories under `run/gametest/kubejs/`. These directories belong to the fixtures; stale/untracked scripts are removed on the next sync. Test-only datapack recipes provide existing IDs for removal and replacement. When KubeJS is installed, `KubeJSMigrationGameTests` adds one required test through a conditional generator; the base runtime has no hard KubeJS test dependency. The tracked assertions now cover:

- Startup and server particle bindings, all 64 definitions, exact Java long energy and invalid-input rejection.
- `event.remove` deleting existing ore-leacher and liquid-collector test recipes and `event.custom` replacing one of each through QMD's real serializers. Ore-leacher assertions retain all inputs, three padded output slots, output count and time; collector assertions check fluid identity/amount, priority and decoded block-state/biome-tag/dimension fields.
- A new actual server recipe manager and new recipe instances after an asynchronous resource reload, with generation-dependent values, removal still applied, exactly one replacement per ID and startup scripts not rerun.
- Startup/server script errors being surfaced as required GameTest failures through both a Java bridge and the KubeJS error state.

The collector smoke fixtures require the intentionally absent `qmd:smoke/collector_only` biome tag, keeping their manager/codec checks from changing collector behavior in other world tests. Fixtures and their Java bridge are selected only for the GameTest run and excluded from the distributed mod jar. A generous tick ceiling and a 30-second wall-clock watchdog allow asynchronous reloads to finish on the unthrottled test server while still failing stalled tests. A separate Gradle summary guard rejects startup aborts even if the Java process exits with code zero. See [TESTING.md](TESTING.md) for repeatable commands and [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md) for evidence locations and current results. Headless menu and synchronization tests establish server contracts and packet data/application, while rendered GUI behavior and actual client interaction remain unverified. The remaining legacy scripting features above are still migration work.
