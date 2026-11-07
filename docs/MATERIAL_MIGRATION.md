# Material migration

The first content slice ports 83 ordinary crafting materials from `MaterialTypes.java` and `QMDItems.java` in `legacy-src/`. It includes all 23 dusts, 19 elemental ingots, 7 alloy ingots, 15 parts, 7 semiconductors and 12 chemical dusts. Deprecated terbium dust and strontium chloride remain present with their original deprecated labels.

Each former metadata variant now has an individual registry ID. The `dust2` and `ingot2` families join `dust` and `ingot`; alloys also use `ingot_`. Parts, semiconductors and chemical dusts retain their family prefixes. `QMDItems.fromLegacy(path, metadata)` provides the explicit lookup needed while migrating recipes. This is a developer mapping, not a Minecraft 1.12 world conversion.

## Preserved resources and recipes

- Original texture PNGs are copied without modification into the singular `textures/item/` hierarchy.
- Models use the matching singular `models/item/` location. Empty cooler, empty detector casing and empty wire chamber retain their original cube geometry. Other materials retain generated flat item geometry.
- English names preserve all 83 entries. Existing Russian (81 entries) and Simplified Chinese (80 entries) translations are carried over; missing translations use Minecraft's English fallback. The legacy British English file has no translated names for this slice.
- 88 common `c` item tag files cover the material families, semiconductor forms and the legacy wire, rod and magnet aliases. Sodium nitrate keeps the niter and saltpeter aliases, and sodium chloride keeps the salt alias. The deprecated materials also receive their ordinary material tags.
- 18 furnace recipes convert the 16 first-group dusts plus yttrium and neodymium dust into their matching ingots, with zero experience and the standard 200-tick duration. The input is the QMD item, preserving the legacy local-input path. The legacy configurable shared-ore input/prioritized-output policy still needs migration alongside the general recipe settings. No mercury smelting recipe is added.
- No machine recipes are claimed for this slice: alloy furnace, assembler and chemical processing production paths remain part of the recipe migration.

Radioactive isotopes and waste, particle sources/cells, copernicium fuels, tools, weapons, armor, medicine and luminous paint are outside this registration slice because they require their full behavior and integrations. They must be ported before release.

## Verification boundary

The material resource validation passed for all 83 registered variants: each has a model, an existing texture byte-identical to the legacy PNG and an English name. It parsed 109 language/tag/recipe JSON files and checked every generated common-tag reference. These checks cannot establish in-game appearance. The three cube item models and the full material creative inventory still require a fresh-client visual check. Consult `DEVELOPMENT_HANDOFF.md` for build results.

## Registry mapping

| Legacy item | Metadata | Variant | New ID |
|---|---:|---|---|
| `qmd:dust` | 0 | `tungsten` | `qmd:dust_tungsten` |
| `qmd:dust` | 1 | `niobium` | `qmd:dust_niobium` |
| `qmd:dust` | 2 | `chromium` | `qmd:dust_chromium` |
| `qmd:dust` | 3 | `titanium` | `qmd:dust_titanium` |
| `qmd:dust` | 4 | `cobalt` | `qmd:dust_cobalt` |
| `qmd:dust` | 5 | `nickel` | `qmd:dust_nickel` |
| `qmd:dust` | 6 | `hafnium` | `qmd:dust_hafnium` |
| `qmd:dust` | 7 | `zinc` | `qmd:dust_zinc` |
| `qmd:dust` | 8 | `osmium` | `qmd:dust_osmium` |
| `qmd:dust` | 9 | `iridium` | `qmd:dust_iridium` |
| `qmd:dust` | 10 | `platinum` | `qmd:dust_platinum` |
| `qmd:dust` | 11 | `sodium` | `qmd:dust_sodium` |
| `qmd:dust` | 12 | `potassium` | `qmd:dust_potassium` |
| `qmd:dust` | 13 | `calcium` | `qmd:dust_calcium` |
| `qmd:dust` | 14 | `strontium` | `qmd:dust_strontium` |
| `qmd:dust` | 15 | `barium` | `qmd:dust_barium` |
| `qmd:dust2` | 0 | `yttrium` | `qmd:dust_yttrium` |
| `qmd:dust2` | 1 | `neodymium` | `qmd:dust_neodymium` |
| `qmd:dust2` | 2 | `iodine` | `qmd:dust_iodine` |
| `qmd:dust2` | 3 | `samarium` | `qmd:dust_samarium` |
| `qmd:dust2` | 4 | `terbium` | `qmd:dust_terbium` |
| `qmd:dust2` | 5 | `erbium` | `qmd:dust_erbium` |
| `qmd:dust2` | 6 | `ytterbium` | `qmd:dust_ytterbium` |
| `qmd:ingot` | 0 | `tungsten` | `qmd:ingot_tungsten` |
| `qmd:ingot` | 1 | `niobium` | `qmd:ingot_niobium` |
| `qmd:ingot` | 2 | `chromium` | `qmd:ingot_chromium` |
| `qmd:ingot` | 3 | `titanium` | `qmd:ingot_titanium` |
| `qmd:ingot` | 4 | `cobalt` | `qmd:ingot_cobalt` |
| `qmd:ingot` | 5 | `nickel` | `qmd:ingot_nickel` |
| `qmd:ingot` | 6 | `hafnium` | `qmd:ingot_hafnium` |
| `qmd:ingot` | 7 | `zinc` | `qmd:ingot_zinc` |
| `qmd:ingot` | 8 | `osmium` | `qmd:ingot_osmium` |
| `qmd:ingot` | 9 | `iridium` | `qmd:ingot_iridium` |
| `qmd:ingot` | 10 | `platinum` | `qmd:ingot_platinum` |
| `qmd:ingot` | 11 | `sodium` | `qmd:ingot_sodium` |
| `qmd:ingot` | 12 | `potassium` | `qmd:ingot_potassium` |
| `qmd:ingot` | 13 | `calcium` | `qmd:ingot_calcium` |
| `qmd:ingot` | 14 | `strontium` | `qmd:ingot_strontium` |
| `qmd:ingot` | 15 | `barium` | `qmd:ingot_barium` |
| `qmd:ingot2` | 0 | `yttrium` | `qmd:ingot_yttrium` |
| `qmd:ingot2` | 1 | `neodymium` | `qmd:ingot_neodymium` |
| `qmd:ingot2` | 2 | `mercury` | `qmd:ingot_mercury` |
| `qmd:ingot_alloy` | 0 | `tungsten_carbide` | `qmd:ingot_tungsten_carbide` |
| `qmd:ingot_alloy` | 1 | `niobium_tin` | `qmd:ingot_niobium_tin` |
| `qmd:ingot_alloy` | 2 | `stainless_steel` | `qmd:ingot_stainless_steel` |
| `qmd:ingot_alloy` | 3 | `niobium_titanium` | `qmd:ingot_niobium_titanium` |
| `qmd:ingot_alloy` | 4 | `osmiridium` | `qmd:ingot_osmiridium` |
| `qmd:ingot_alloy` | 5 | `nichrome` | `qmd:ingot_nichrome` |
| `qmd:ingot_alloy` | 6 | `super_alloy` | `qmd:ingot_super_alloy` |
| `qmd:part` | 0 | `empty_cooler` | `qmd:part_empty_cooler` |
| `qmd:part` | 1 | `detector_casing` | `qmd:part_detector_casing` |
| `qmd:part` | 2 | `scintillator_pwo` | `qmd:part_scintillator_pwo` |
| `qmd:part` | 3 | `scintillator_plastic` | `qmd:part_scintillator_plastic` |
| `qmd:part` | 4 | `wire_bscco` | `qmd:part_wire_bscco` |
| `qmd:part` | 5 | `rod_nd_yag` | `qmd:part_rod_nd_yag` |
| `qmd:part` | 6 | `wire_gold_tungsten` | `qmd:part_wire_gold_tungsten` |
| `qmd:part` | 7 | `wire_chamber_casing` | `qmd:part_wire_chamber_casing` |
| `qmd:part` | 8 | `magnet_nd` | `qmd:part_magnet_nd` |
| `qmd:part` | 9 | `accelerating_barrel` | `qmd:part_accelerating_barrel` |
| `qmd:part` | 10 | `laser_assembly` | `qmd:part_laser_assembly` |
| `qmd:part` | 11 | `wire_ssfaf` | `qmd:part_wire_ssfaf` |
| `qmd:part` | 12 | `wire_ybco` | `qmd:part_wire_ybco` |
| `qmd:part` | 13 | `magnet_smc` | `qmd:part_magnet_smc` |
| `qmd:part` | 14 | `wire_hbcco` | `qmd:part_wire_hbcco` |
| `qmd:semiconductor` | 0 | `silicon_p_doped` | `qmd:semiconductor_silicon_p_doped` |
| `qmd:semiconductor` | 1 | `silicon_n_doped` | `qmd:semiconductor_silicon_n_doped` |
| `qmd:semiconductor` | 2 | `silicon_boule` | `qmd:semiconductor_silicon_boule` |
| `qmd:semiconductor` | 3 | `silicon_wafer` | `qmd:semiconductor_silicon_wafer` |
| `qmd:semiconductor` | 4 | `basic_processor` | `qmd:semiconductor_basic_processor` |
| `qmd:semiconductor` | 5 | `advanced_processor` | `qmd:semiconductor_advanced_processor` |
| `qmd:semiconductor` | 6 | `elite_processor` | `qmd:semiconductor_elite_processor` |
| `qmd:chemical_dust` | 0 | `tungsten_oxide` | `qmd:chemical_dust_tungsten_oxide` |
| `qmd:chemical_dust` | 1 | `bscco` | `qmd:chemical_dust_bscco` |
| `qmd:chemical_dust` | 2 | `sodium_nitrate` | `qmd:chemical_dust_sodium_nitrate` |
| `qmd:chemical_dust` | 3 | `sodium_chloride` | `qmd:chemical_dust_sodium_chloride` |
| `qmd:chemical_dust` | 4 | `copper_oxide` | `qmd:chemical_dust_copper_oxide` |
| `qmd:chemical_dust` | 5 | `hafnium_oxide` | `qmd:chemical_dust_hafnium_oxide` |
| `qmd:chemical_dust` | 6 | `strontium_chloride` | `qmd:chemical_dust_strontium_chloride` |
| `qmd:chemical_dust` | 7 | `zinc_sulfide` | `qmd:chemical_dust_zinc_sulfide` |
| `qmd:chemical_dust` | 8 | `iron_fluoride` | `qmd:chemical_dust_iron_fluoride` |
| `qmd:chemical_dust` | 9 | `ssfaf` | `qmd:chemical_dust_ssfaf` |
| `qmd:chemical_dust` | 10 | `ybco` | `qmd:chemical_dust_ybco` |
| `qmd:chemical_dust` | 11 | `hbcco` | `qmd:chemical_dust_hbcco` |

## Common tag aliases

The parent family tags include their individual material tags.

| Common tag | Item |
|---|---|
| `c:dusts/tungsten` | `qmd:dust_tungsten` |
| `c:dusts/niobium` | `qmd:dust_niobium` |
| `c:dusts/chromium` | `qmd:dust_chromium` |
| `c:dusts/titanium` | `qmd:dust_titanium` |
| `c:dusts/cobalt` | `qmd:dust_cobalt` |
| `c:dusts/nickel` | `qmd:dust_nickel` |
| `c:dusts/hafnium` | `qmd:dust_hafnium` |
| `c:dusts/zinc` | `qmd:dust_zinc` |
| `c:dusts/osmium` | `qmd:dust_osmium` |
| `c:dusts/iridium` | `qmd:dust_iridium` |
| `c:dusts/platinum` | `qmd:dust_platinum` |
| `c:dusts/sodium` | `qmd:dust_sodium` |
| `c:dusts/potassium` | `qmd:dust_potassium` |
| `c:dusts/calcium` | `qmd:dust_calcium` |
| `c:dusts/strontium` | `qmd:dust_strontium` |
| `c:dusts/barium` | `qmd:dust_barium` |
| `c:dusts/yttrium` | `qmd:dust_yttrium` |
| `c:dusts/neodymium` | `qmd:dust_neodymium` |
| `c:dusts/iodine` | `qmd:dust_iodine` |
| `c:dusts/samarium` | `qmd:dust_samarium` |
| `c:dusts/terbium` | `qmd:dust_terbium` |
| `c:dusts/erbium` | `qmd:dust_erbium` |
| `c:dusts/ytterbium` | `qmd:dust_ytterbium` |
| `c:ingots/tungsten` | `qmd:ingot_tungsten` |
| `c:ingots/niobium` | `qmd:ingot_niobium` |
| `c:ingots/chromium` | `qmd:ingot_chromium` |
| `c:ingots/titanium` | `qmd:ingot_titanium` |
| `c:ingots/cobalt` | `qmd:ingot_cobalt` |
| `c:ingots/nickel` | `qmd:ingot_nickel` |
| `c:ingots/hafnium` | `qmd:ingot_hafnium` |
| `c:ingots/zinc` | `qmd:ingot_zinc` |
| `c:ingots/osmium` | `qmd:ingot_osmium` |
| `c:ingots/iridium` | `qmd:ingot_iridium` |
| `c:ingots/platinum` | `qmd:ingot_platinum` |
| `c:ingots/sodium` | `qmd:ingot_sodium` |
| `c:ingots/potassium` | `qmd:ingot_potassium` |
| `c:ingots/calcium` | `qmd:ingot_calcium` |
| `c:ingots/strontium` | `qmd:ingot_strontium` |
| `c:ingots/barium` | `qmd:ingot_barium` |
| `c:ingots/yttrium` | `qmd:ingot_yttrium` |
| `c:ingots/neodymium` | `qmd:ingot_neodymium` |
| `c:ingots/mercury` | `qmd:ingot_mercury` |
| `c:ingots/tungsten_carbide` | `qmd:ingot_tungsten_carbide` |
| `c:ingots/niobium_tin` | `qmd:ingot_niobium_tin` |
| `c:ingots/stainless_steel` | `qmd:ingot_stainless_steel` |
| `c:ingots/niobium_titanium` | `qmd:ingot_niobium_titanium` |
| `c:ingots/osmiridium` | `qmd:ingot_osmiridium` |
| `c:ingots/nichrome` | `qmd:ingot_nichrome` |
| `c:ingots/super_alloy` | `qmd:ingot_super_alloy` |
| `c:dusts/tungsten_oxide` | `qmd:chemical_dust_tungsten_oxide` |
| `c:dusts/bscco` | `qmd:chemical_dust_bscco` |
| `c:dusts/sodium_nitrate` | `qmd:chemical_dust_sodium_nitrate` |
| `c:dusts/sodium_chloride` | `qmd:chemical_dust_sodium_chloride` |
| `c:dusts/copper_oxide` | `qmd:chemical_dust_copper_oxide` |
| `c:dusts/hafnium_oxide` | `qmd:chemical_dust_hafnium_oxide` |
| `c:dusts/strontium_chloride` | `qmd:chemical_dust_strontium_chloride` |
| `c:dusts/zinc_sulfide` | `qmd:chemical_dust_zinc_sulfide` |
| `c:dusts/iron_fluoride` | `qmd:chemical_dust_iron_fluoride` |
| `c:dusts/ssfaf` | `qmd:chemical_dust_ssfaf` |
| `c:dusts/ybco` | `qmd:chemical_dust_ybco` |
| `c:dusts/hbcco` | `qmd:chemical_dust_hbcco` |
| `c:boules/silicon` | `qmd:semiconductor_silicon_boule` |
| `c:wafers/silicon` | `qmd:semiconductor_silicon_wafer` |
| `c:silicon/p_doped` | `qmd:semiconductor_silicon_p_doped` |
| `c:silicon/n_doped` | `qmd:semiconductor_silicon_n_doped` |
| `c:processors/basic` | `qmd:semiconductor_basic_processor` |
| `c:processors/advanced` | `qmd:semiconductor_advanced_processor` |
| `c:processors/elite` | `qmd:semiconductor_elite_processor` |
| `c:wires/bscco` | `qmd:part_wire_bscco` |
| `c:wires/gold_tungsten` | `qmd:part_wire_gold_tungsten` |
| `c:rods/nd_yag` | `qmd:part_rod_nd_yag` |
| `c:magnets/neodymium` | `qmd:part_magnet_nd` |
| `c:wires/ssfaf` | `qmd:part_wire_ssfaf` |
| `c:wires/ybco` | `qmd:part_wire_ybco` |
| `c:magnets/samarium_cobalt` | `qmd:part_magnet_smc` |
| `c:wires/hbcco` | `qmd:part_wire_hbcco` |
| `c:dusts/niter` | `qmd:chemical_dust_sodium_nitrate` |
| `c:dusts/saltpeter` | `qmd:chemical_dust_sodium_nitrate` |
| `c:dusts/salt` | `qmd:chemical_dust_sodium_chloride` |

