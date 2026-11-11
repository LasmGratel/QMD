# Registry consolidation

The registry package is organized by registered content, replacing the 23 feature-specific classes with 12 shared owners. Registry paths remain unchanged, so existing worlds, recipes and resource references retain their IDs.

## Plan and ownership

1. Move blocks, items, block entities, menus and recipes out of the accelerator, chamber, liquefier, irradiator, fission and other feature registries.
2. Give each registry one deferred register and update runtime, JEI, client, datagen and test callers to its shared owner. Keep typed family maps where callers need a material or machine-part family.
3. Register each owner once from `QuantumMinecraftDynamics`, preserve its lifecycle hooks, regenerate resources and run the existing GameTests.

| Class in `lach_01298.qmd.registry` | Ownership |
| --- | --- |
| `QMDBlocks` | Blocks, accelerator part definitions, chamber parts, discharge lamps, liquefier parts and Neohaul fission/RTG/turbine part types |
| `QMDItems` | Materials, nuclear materials, particle sources/cells, equipment, weapons, consumables, paint, block items, item capabilities, default components, radiation and equipment event hooks |
| `QMDFluids` | Fluid types and source/flowing families; the Neohaul fluid maker uses `QMDBlocks.BLOCKS` and `QMDItems.ITEMS` for blocks and buckets |
| `QMDEntities` | Projectile, beam and gamma-flash entity types |
| `QMDBlockEntities` | All block entity types, Neohaul ore-leacher initialization and block capability bindings |
| `QMDMenus` | All nineteen container menu types, including six chamber controllers and eight accelerator/source/component views |
| `QMDRecipes` | Recipe types, serializers, conditions and Neohaul processor handlers; three concrete accelerator families and eight concrete chamber recipe types/serializers |
| `QMDArmorMaterials` | HEV armor material |
| `QMDDataComponents` | Particle amount and cell-fluid components |
| `QMDSounds` | Registered QMD sound events |
| `QMDDamageTypes` | Annihilation/weapon damage keys and indirect weapon damage sources |
| `QMDCreativeTabs` | QMD creative tab, item ordering and equipment configuration filtering |

`QMDMachines`, `QMDAccelerators`, `QMDChambers`, `QMDLiquefier`, `QMDIrradiator`, `QMDFission`, `QMDTurbines`, `QMDBeamlines`, `QMDLamps`, `QMDParticleItems`, `QMDNuclearMaterials`, `QMDEquipment`, `QMDPlayerContent`, `QMDWeapons`, `QMDMachineRecipes`, `QMDChamberRecipes`, `QMDWeaponDamage` and `QMDWeaponSounds` are removed. There are no forwarding classes or parallel feature-specific deferred registers.

## Initialization and behavior

Block declarations have no item registration side effects. `QMDItems` registers the corresponding block items after the block holders exist. Fluid family initialization adds its blocks and buckets to those same shared registers before they attach to the mod bus. Holder suppliers resolve only during the appropriate registration event.

The entry point registers the data components, armor materials, fluids, blocks, items, entities, block entities, menus, recipes, sounds and creative tab once. Item and block capabilities retain their original implementations. Radiation callbacks, irradiated food behavior, HEV components, equipment events and configured creative entries remain connected. Accelerator payload registration now lives in `AcceleratorStatePayload`, alongside its codec and handler; the entry point also registers the beam-meter payload directly.

Datagen retains the typed material catalog and explicitly identifies the authored equipment models through `QMDItems.EQUIPMENT`. Models, recipe/data-map output, textures and resource IDs are unchanged. The reference-only `legacy-src/` tree is untouched.

## Verification

On `G:/QMD`, `.\gradlew.bat compileJava compileTestJava --console=plain` passed in 13 seconds. `.\gradlew.bat runData --console=plain` passed in 26 seconds, preserving all 2,476 generated resources byte-for-byte. `.\gradlew.bat runGameTestServer --console=plain` passed in 53 seconds with all 121 required tests passing (23.97-second suite).

Source snapshots, compile/data/GameTest transcripts and generated-resource SHA-256 comparisons are retained under ignored `build/reports/migration/registry/`. The existing tests exercise machine construction, menus, automation, persistence, resource reload, radiation and weapon behavior through the consolidated registries. Unit tests were compiled but not executed; this refactor did not run a release build, the optional KubeJS configuration or a rendered client check. See [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md) for the checkpoint.
