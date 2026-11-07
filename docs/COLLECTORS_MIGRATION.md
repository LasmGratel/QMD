# Collector and chemistry migration

The migrated blocks are `qmd:liquid_collector` and `qmd:atmosphere_collector`. They use Neohaul's energy and fluid storage, side capabilities, bucket transfer and adjacent-tank output. QMD supplies their world-conditioned recipes and scanning behavior. Both retain the legacy nondirectional cube models, textures and crafting patterns.

The legacy collectors have no menu or GUI. Their interaction is an empty-main-hand status message showing the collection fluid and potential rate, plus normal fluid-container access. Preserving this interaction is collector parity; it does not replace an existing legacy GUI. The ore leacher and other machines that had GUI must retain their complete screens separately.

Implementation and verification are separate. On 2026-11-07, `.\gradlew.bat runGameTestServer` passed all 34 required tests, `.\gradlew.bat runGameTestServer -PwithKubeJS` passed all 35, and `.\gradlew.bat build` passed all 39 unit tests and built the main jar. Both GameTest runs completed in 26 seconds; the build completed in 13 seconds. Current evidence and commands belong in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md) and [TESTING.md](TESTING.md). No rendered collector or new-fluid verification is claimed here.

## Storage, power and collection

Each collector has one 32,000 mB output tank. Every side initially accepts energy and exposes fluid extraction; automated fluid insertion is disabled. The inherited configurable fluid-side sorption contract and non-default settings survive NBT. Stored output can be pushed to loaded adjacent tanks even while collection is stopped. The blocks have no item slots or upgrade slots. Crouching suppresses bucket transfer, as in legacy, while empty-hand status still works.

| Collector | Power setting | Default cost per productive tick | Default energy capacity |
| --- | --- | --- | --- |
| Liquid | `processors.liquid_collector_power` | 50 FE | 1,000 FE |
| Atmosphere | `processors.atmosphere_collector_power` | 100 FE | 2,000 FE |

Capacity is twenty times the configured power. A configured zero cost intentionally permits collection without stored energy. Changes to the configured cost update the storage capacity during server ticking.

The first server tick after loading refreshes the recipe and scan. Normal scans repeat every 100 server ticks, nominally five seconds. A data reload replaces the recipe-manager contents; each collector sees the new recipes at its next scan, without retaining old recipe instances.

The liquid collector samples the complete block state immediately below itself when choosing a recipe. Its efficiency counts blocks of the same block type in the 5×5×5 volume from offsets `(-2,-4,-2)` through `(2,0,2)`, excluding the collector itself. There are 124 scanned neighbors. Thus the default recipe requires source water/lava immediately below, while flowing blocks of that same fluid can contribute to the surrounding-volume efficiency, as in the legacy implementation.

The atmosphere collector uses the 5×5×5 volume from offsets `(-2,0,-2)` through `(2,4,2)`, also excluding itself. Empty space permits collection; solid and transparent obstructions reduce efficiency. The modern obstruction predicate preserves the legacy distinction between empty/foliage materials and blocking glass or fluid. Non-full wood/iron shapes such as doors, trapdoors, ladders, bars and pressure plates retain their legacy opening behavior; glass panes and nonblocking circuit-like shapes still obstruct. See the implementation and GameTests for the precise block-property mapping.

Scans require every intersecting chunk column to be fully loaded, using non-loading lookups before block or biome reads. An unavailable column clears the current recipe/rate and retries on the normal 100-tick interval, preserving energy and stored output. Adjacent output likewise skips unloaded neighbors. The collectors never force neighboring chunks to load.

For either collector, the potential rate is the integer part of the recipe's full-efficiency amount multiplied by efficiency. It generates only when the rate is positive, there is enough energy, and the tank has room for that fluid and its components. A partly full tank accepts the remaining capacity. A productive tick pays the collector's fixed configured cost; blocked or zero-rate collection pays nothing. Collection does not consume the sampled world blocks.

The status message reports the potential rate when sufficient energy is available, including when the output tank is full. This preserves the legacy status semantics. The `isProcessing` state instead describes actual successful generation.

## Default world recipes

All rates below are mB per tick at full efficiency. The corresponding recipe IDs are under `qmd:liquid_collector/` or `qmd:atmosphere_collector/`.

| Recipe | Sampled block | World restriction | Output |
| --- | --- | --- | --- |
| `liquid_collector/water` | Source water | Overworld rivers, frozen rivers, swamps and mangrove swamps | 5,000 water |
| `liquid_collector/salt_water` | Source water | Overworld ocean/deep-ocean variants, beaches, snowy beaches and stony shores | 1,000 salt water |
| `liquid_collector/lava` | Source lava | Nether, any biome | 100 lava |
| `atmosphere_collector/compressed_air` | Air, supplied by the atmosphere collector | Overworld, any biome | 1,000 compressed air |

The modern ocean, shore and swamp variants replace the legacy biome IDs. Dimension restrictions use resource keys such as `minecraft:overworld` and `minecraft:the_nether`, replacing numeric dimension IDs. The collectors retain all legacy collection categories and rates.

Crafting uses the legacy `ASA / BMB / ASA` pattern: advanced plating in the corners, steel ingots for the atmosphere collector or bronze ingots for the liquid collector, two buckets and an electric motor. Neohaul provides the plating and motor. The ingot ingredients use `c:ingots/steel` and `c:ingots/bronze`.

## Recipe format and KubeJS

Both the recipe type and serializer are `qmd:liquid_collector` or `qmd:atmosphere_collector`. `output` is a NeoForge fluid stack with a registered fluid `id` and positive integer `amount`. Its amount represents a full-efficiency rate, rather than one batch. `world` and `priority` are optional.

```json
{
  "type": "qmd:liquid_collector",
  "output": { "id": "qmd:salt_water", "amount": 1000 },
  "world": {
    "blocks": [
      { "block": "minecraft:water", "properties": { "level": "0" } }
    ],
    "biomes": ["minecraft:ocean"],
    "biome_tags": [],
    "dimensions": ["minecraft:overworld"]
  },
  "priority": 0
}
```

Alternatives inside each category are ORed. Block, biome and dimension categories must all match. Named biomes and biome tags form one combined set of alternatives. Missing or empty lists are unrestricted. Block properties omitted from a match are unrestricted; specified properties must exist on that block and accept the supplied value. Invalid properties fail recipe decoding. Each category accepts up to 4,096 alternatives, and a block match accepts up to 32 properties.

When recipes overlap, the highest numeric priority wins. Equal priorities use the lexically first recipe ID, making the result deterministic. An atmosphere collector supplies an air state to matching; normally atmosphere recipes omit the block restriction.

KubeJS uses its standard server recipe event and QMD's real serializer. For example, place this in a server script to replace the Overworld atmosphere output and reload server resources:

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'qmd:atmosphere_collector/compressed_air' })
  event.custom({
    type: 'qmd:atmosphere_collector',
    output: { id: 'qmd:compressed_air', amount: 750 },
    world: { dimensions: ['minecraft:overworld'] },
    priority: 10
  }).id('qmd:atmosphere_collector/compressed_air')
})
```

No specialized `event.recipes.qmd` collector builder is registered. Custom block/fluid content registration belongs in startup scripts; modifying collector recipes belongs in server scripts. The tracked KubeJS GameTest fixtures exercise recipe removal, replacement and resource reload rather than requiring unsupported CraftTweaker or GroovyScript integration.

## Salt and acid bootstrap

The ocean collector supplies the initial salt water without requiring an existing salt item. Neohaul's crystallizer can turn it directly into QMD sodium-chloride dust, which belongs to `c:dusts/salt`. This supplies the previously missing start of the existing ore-leacher acid chain.

| Processor | Inputs | Outputs | Time multiplier | Power multiplier |
| --- | --- | --- | --- | --- |
| Crystallizer | 10,000 salt water | 1 salt dust | 2 | 4 |
| Multiblock distiller | 10,000 salt water | 666 sodium chloride solution + 9,000 water | 1 | 1 |
| Crystallizer | 666 sodium chloride solution | 1 salt dust | 1 | 1 |
| Fluid enricher | 1 salt dust + 1,000 water | 666 sodium chloride solution | 1 | 1 |
| Electrolyzer | 1,332 sodium chloride solution | 1,000 hydrogen + 1,000 chlorine + 1,332 sodium hydroxide solution | 0.5 | 1 |
| Chemical reactor | 1,000 hydrogen + 1,000 chlorine | 2,000 hydrochloric acid | 1 | 1 |

Fluid quantities are mB and all listed processor recipes have zero recipe radiation. The distiller separates two liquid products and therefore requires one sieve-tray level under Neohaul's existing rules. It is an alternative route to concentration; the direct salt-water crystallizer recipe bootstraps salt without building a distiller. Nitric-acid production uses the existing nitrogen/oxygen chemistry described in [ORE_LEACHER_MIGRATION.md](ORE_LEACHER_MIGRATION.md).

## Atmospheric gas path and cooling policy

QMD registers `salt_water`, `compressed_air`, `liquid_air`, `argon` and `neon`, because the pinned Neohaul release supplies none of these fluids. Each has a source and flowing fluid, a fluid type, a world block, a bucket, a common fluid tag, localized names and client rendering setup. Nitrogen, oxygen, helium and their existing cryogenic products remain Neohaul fluids.

The salt-water tint retains the legacy 50:50 blend of `0x2F43F4` with `0x2974FF` and uses Neohaul's salt-solution family. Compressed air, argon and neon retain legacy tints `0xBDF0FF`, `0xFF75DD` and `0xFF9F7A`, using Neohaul's gas family. Liquid air retains tint `0x6CDEFF`, density 870, temperature 79 K, viscosity 170 and zero luminosity. Its properties are supplied explicitly through `NCFluid.TypeInfo`, avoiding ambiguity in overload parameter forwarding. Gas bucket models use `neoforge:item/bucket`; liquids use `neoforge:item/bucket_drip`, with gas flipping and fluid tinting enabled as appropriate.

The atmosphere collector's compressed air can be cooled in Neohaul's supercooler, then separated in Neohaul's multiblock distiller:

| Input | Outputs | Time multiplier | Power multiplier |
| --- | --- | --- | --- |
| 1,000 liquid air | 38,400 nitrogen + 12,800 oxygen + 6,400 argon + 5,120 neon + 1,280 helium | 1 | 1 |

All five products are gases, so the liquid-product sieve-tray requirement is zero. The source quantity and gas ratios are the unchanged legacy recipe. Nitrogen and oxygen now have an atmospheric production path for the existing nitric-acid recipes.

`recipes.override_nc_recipes` defaults to `true`, as in the legacy mod. The `qmd:override_nc_recipes` NeoForge condition chooses exactly one JSON recipe per supported gas when resources load or reload:

| Gas input → liquid product | Enabled policy: input → output; time/power | Disabled policy: input → output; time/power |
| --- | --- | --- |
| Compressed air → liquid air | 64 → 1; `2/150` / 1 | 8,000 → 25; 0.5 / 0.5 |
| Nitrogen → liquid nitrogen | 64 → 1; `2/150` / 2.5 | 8,000 → 25; 0.5 / 0.5 |
| Helium → liquid helium | 64 → 1; `2/150` / 5 | 8,000 → 25; 1 / 1 |

Amounts are mB. Enabled values preserve the legacy QMD overrides. Disabled air cooling preserves the legacy alternative, while disabled nitrogen/helium cooling reproduces the actual pinned Neohaul recipe values. Neohaul suppresses its own nitrogen and helium cooling recipes whenever QMD is loaded, independently of QMD's setting; therefore QMD supplies both policy branches to prevent those existing functions from disappearing. The two branches have distinct IDs ending in `_override` and `_standard`, and only one is loaded at a time. A policy change takes effect on server resource reload.

This policy currently covers these three migrated cooling families. Other legacy gas/liquid families, argon/neon lamps and detectors, and QMD's separate liquefier remain migration work. The new atmospheric route supplies useful nitrogen, oxygen and helium without claiming those downstream systems are ported.

## Intentional corrections and validation limits

The migrated collector fixes the legacy liquid collector charging `processor_power[1]` even though its readiness check used `processor_power[2]`. It consistently uses its own liquid-collector power setting. Generation also avoids charging when the computed rate is zero or stored fluid cannot accept the new output. Successful energy and fluid mutations mark the block entity dirty, and persisted scan state is revalidated after loading.

Efficiency explicitly excludes the collector position and stays within zero through one. Recipe selection is stable across overlapping definitions. State-property validation, resource-key dimensions and reloaded recipe-manager lookups replace the legacy mutable maps and numeric-dimension assumptions. The collectors retain the original collection volumes, integer rate truncation, output capacity, default power settings and no-consumption behavior for world fluids.

`CollectorRecipeGameTests` covers loaded defaults, world-condition matching, codecs and invalid data. `CollectorChemistryGameTests` adds three required checks: exact loaded salt/air ratios and modifiers; default cooling selection without duplicates; and actual ticking crystallizers supplying their produced salt through enricher, electrolyzer and chemical reactor to hydrochloric acid. The last test installs real speed/energy upgrades and supplies fixture power; it transfers actual produced materials without replacing recipes or forcing progress. `CoolingPolicyGameTests` verifies both policies through actual reloads and restores the original setting. Eight collector-specific tests cover scans, generation, capability behavior, buckets, side-setting persistence, NBT, redstone independence and loaded/unloaded boundaries; representative atmosphere obstruction cases cover the modern property mapping.

The two collector textures are byte-identical to their legacy originals, and the expanded main JSON set was parsed successfully during static validation. These checks do not prove client appearance. The GameTests verify server contracts and selected actual processing paths; they do not establish rendered bucket/fluid behavior, a player-built distiller, full survival progression, or complete legacy feature parity. See the handoff for the latest executed build/test commands and results.
