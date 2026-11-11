# Resource data generation

QMD uses NeoForge's [Minecraft 1.21.1 data generation lifecycle](https://docs.neoforged.net/docs/1.21.1/resources/#data-generation). The mod-bus `QMDDataGenerators.gatherData` listener registers client providers for models/blockstates and server providers for recipes, tags, loot and native data. Normal gameplay does not run the providers.

## Source ownership

| Source | Ownership |
| --- | --- |
| `src/data/java/lach_01298/qmd/datagen/client/` | Registry-driven model and state families, rotation/activity/output variants, fluid and item models |
| `src/data/java/lach_01298/qmd/datagen/recipe/` | Direct native recipe builders and typed custom-recipe factories, including particle reactions |
| `src/data/java/lach_01298/qmd/datagen/server/` | Registry-driven self-drop loot, common material/fluid/isotope tags, native data |
| `src/generated/resources/` | Checked-in generated assets and data; edit their providers rather than the output |
| `src/main/resources/` | Textures, translations, mixin configuration and exceptional authored geometry |

Providers construct output through reusable families and typed declarations. They do not read the old handwritten JSON or previous generated files. Reaction declarations retain exact integers and decimal values, fixed channel arrays, conditions and recipe IDs. Definitions are Java source, so no extra runtime resource catalogue is shipped.

The client providers extend NeoForge's `BlockStateProvider` and `ItemModelProvider`, using model builders, typed blockstate variants and `ExistingFileHelper` validation. Buckets use `DynamicFluidContainerModelBuilder`. See the [1.21.1 model generation documentation](https://docs.neoforged.net/docs/1.21.1/resources/client/models/datagen); newer documentation targets a different model API.

Server generation uses the standard provider APIs:

| Content | Provider/API |
| --- | --- |
| Recipes | `QMDRecipeProvider extends RecipeProvider`; typed recipes through `RecipeOutput.withConditions` |
| Block loot | `QMDBlockLootProvider extends LootTableProvider`; `BlockLootSubProvider.dropSelf` |
| Tags | Separate `BlockTagsProvider`, `ItemTagsProvider`, `FluidTagsProvider` and `DamageTypeTagsProvider` subclasses |
| Reflector data | `QMDNativeDataProvider extends DataMapProvider`; Neohaul's registered data-map type and `FissionReflectorData` |
| Damage types | `QMDDamageTypeProvider extends DatapackBuiltinEntriesProvider`; registry bootstrap shared with damage tags |

All three `Minecraft*Recipes` classes extend `RecipeProvider` and declare recipes directly with vanilla builders, following `VanillaRecipeProvider`. The aggregate `QMDRecipeProvider` delegates to those providers and the Neohaul/QMD families through one `RecipeOutput`: Minecraft 1.21.1 makes `RecipeProvider.getName()` final, so the subclasses cannot be registered separately under distinct names. Neohaul declarations use `ProcessorRecipeBuilder`/`BasicRecipeBuilder`, following `com.nred.nuclearcraft.datagen.recipes.processor` in `G:/Nuclearcraft-Neohaul/src/main/java`.

`QmdRecipeBuilders` supplies typed chamber, accelerator, collector, liquefier, irradiator and ore-leacher builders. Their functional factory interfaces reference the runtime recipe constructors, and the builders implement `RecipeBuilder`, including conditions, criteria and save behavior. The chamber builder is generic over `AbstractChamberRecipe` and receives one of eight concrete constructor factories, such as `TargetChamberRecipe::new`. The accelerator builder similarly uses `AbstractAcceleratorRecipe.Factory<R>` with `AcceleratorSourceRecipe`, `AcceleratorCoolingRecipe` and `MassSpectrometerRecipe` constructors. Neither builder constructs a mode-switched recipe. Each recipe declares typed ingredients and physical channels directly; `QmdRecipeIngredients` and `NeohaulRecipeIngredients` only provide typed registry/ingredient helpers. Exact particle energies and flux are Java `long` literals. There are no delimited recipe strings, column schemas, production parsers or handwritten JSON. The registered codecs control serialization, including omission of optional defaults. Explicit empty physical slots remain part of nonempty channel arrays. Loaded-channel GameTests remain necessary when extending native recipes because Neohaul can silently discard malformed optional arrays.

`RecipeOutputs` preserves the published QMD IDs instead of Neohaul's automatic namespace/family prefix and retains the existing recipe-only output without adding advancements. Patchouli's API supplies the guide stack with its book component. The Minecraft material-smelting provider reads the configured material policy through typed inputs and retains deterministic preferred-ingot selection.

The server APIs follow the NeoForge 1.21.1 documentation for [recipes](https://docs.neoforged.net/docs/1.21.1/resources/server/recipes/#data-generation), [loot tables](https://docs.neoforged.net/docs/1.21.1/resources/server/loottables/#datagen), [data maps](https://docs.neoforged.net/docs/1.21.1/resources/server/datamaps/#data-generation) and [tags](https://docs.neoforged.net/docs/1.21.1/resources/server/tags#datagen).

The dedicated `data` source set is loaded together with `main` only for `runData`. Its Java providers and catalogues are excluded from the release jar, normal game runs, unit tests and GameTests. Neohaul 0.10.10 eagerly links its optional Curios data provider during `GatherDataEvent`, even with `--mod qmd`; the pinned Curios dependency therefore belongs only to `dataRuntimeOnly`.

The data run also passes `--existing-mod nuclearcraftneohaul` so `ExistingFileHelper` can validate inherited texture references against the pinned dependency's assets.

Gradle includes both resource roots in the main source set and excludes the generator `.cache/`. `processResources` rejects duplicate relative paths; do not keep an authored copy of a generated file. Exceptional geometry may remain authored when generation adds no useful abstraction.

## Workflow

From the project root:

```powershell
.\gradlew.bat runData --console=plain
.\gradlew.bat build --console=plain
```

Update the provider or domain table, regenerate, and review the source and generated diff together. Include generated output in the same change. CI runs data generation before building and fails when tracked output changes or new generated files are missing from Git. `.cache/` files remain ignored.

Run the headless GameTest suite after source/resource modifications are finished. In addition to the normal machine/menu/automation checks, it verifies actual loaded recipes, native channel retention, resolved materials and resource reload. A resource refactor must preserve these contracts.

## Migration verification

The migration generated 2,433 JSON files: 198 blockstates, 254 block models, 344 item models, 1,235 recipes, 262 tags, 138 loot tables and two native data definitions. All match the previous handwritten content semantically, including array order and exact decimal/integer values. Their handwritten copies were removed only after comparison. The three translations, mixin configuration and authored `assets/qmd/models/block/beamline.json` remain in the main tree.

After moving the first generated output aside, `runData` recreated all 2,433 files byte-for-byte without the old handwritten files. There are zero duplicate resource paths, missing files or added resource IDs. The subsequent full build passed all 80 unit tests; the final headless server passed all 98 required GameTests. The release jar includes each generated resource once and excludes generator classes, cache and test fixtures.

The previous handwritten snapshot, semantic audit and SHA-256 comparison are retained under ignored `build/reports/migration/`. Exact commands, timings and transcripts are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). No rendered appearance is established by these checks.

## Rescue integration

The recipe catalog now includes equipment, player content and weapons. Luminous-paint blockstates/models, player item models, paint loot tables, recipes and weapon damage types/tags are generated. Equipment and weapon item models remain authored to preserve their geometry, overlay layers and display transforms, and the generic item provider skips them. PNG textures, OGG sounds, translations, and Patchouli content remain authored assets.

The rescue integration generated 2,479 JSON resources. The material-recipe config is registered and generation uses its declared defaults before configs load. The lepton recipe uses the pinned Neohaul item `nuclearcraftneohaul:copper_solenoid`; a GameTest crafts its original pattern using the loaded recipe. The other two weapon recipe IDs are also checked in the actual recipe manager. No generated JSON was hand-edited.

## Native model provider rewrite (2026-11-11)

`QMDBlockStateProvider` and `QMDItemModelProvider` now use the standard NeoForge generators; the manual `AssetJson` helper was removed. Model parents, textures and variant properties are checked by the native builders. Authored equipment overlays and beamline geometry remain in the main resource tree.

Generation preserves all 810 client resource paths. A semantic comparison against the pre-rewrite snapshot found only three changes: liquefier controller, energy-port and fluid-port variants now use the registered `nuclearcraftneohaul_active` property instead of the invalid `active` property. Other model content and rotations are unchanged; native serialization omits zero rotations and orders state property keys.

`.\gradlew.bat compileJava compileDataJava --console=plain` passed in 14 seconds. The final `.\gradlew.bat runData --console=plain` compiled the updated providers and passed in 17 seconds, emitting 2,479 JSON resources. Transcripts are `build/reports/migration/compile-model-providers.log` and `datagen-model-providers-2.log`; the comparison is `model-provider-parity.json` in the same directory. No GameTests or rendered client checks were run for this generator rewrite.

## Native server provider rewrite (2026-11-11)

The recipe and server packages no longer construct or write JSON manually. `QMDServerDataProvider` and the combined `QMDTagProvider` were removed. `GatherDataEvent` now registers ten providers, including the four tag registries, block-to-item storage-tag copying and the generated damage-type lookup.

Generation preserves all 2,479 resource paths, including 1,261 recipes, 262 tags and 141 loot tables. Semantic comparison against the preceding generated snapshot found no unexpected changes to conditions, amounts, exact particle energies, optional tag entries, fluid membership, channel order, guide components or other content. The 1,241 strict JSON differences are registered codec defaults, loot-table zero bonus rolls and deterministic random-sequence metadata, and four ore-leacher recipes serializing their existing empty third output slot. `OreLeacherRecipe` already pads that slot when reading the old definitions. Authored/generated resource roots have zero duplicate paths.

`.\gradlew.bat compileJava compileDataJava --console=plain` passed in 3 seconds. Final `.\gradlew.bat runData --console=plain` passed in 16 seconds. `.\gradlew.bat runGameTestServer --console=plain` passed in 46 seconds with all 119 required tests passing (18.73-second suite), including native processor channel checks, fission reflector data and actual recipe reload. Logs and strict/semantic comparisons are under `build/reports/migration/server-datagen/`. No release build, unit tests, optional KubeJS run or rendered client checks were performed for this rewrite.

## Explicit recipe declarations (2026-11-11)

Three isolated slices rewrote all 51 recipe-family classes: Minecraft, Neohaul and QMD. The integrated package has 56 Java files including its aggregate provider, output adapter and typed helpers/builders. Removed `RecipeTable`, `RecipeValues`, `RecipeFactories` and `RecipeCatalog`. A source audit found no JSON construction, column/row schemas, packed text blocks or delimiter parsers in the recipe package.

Final `.\gradlew.bat compileJava compileDataJava --console=plain` passed in 6 seconds, and `.\gradlew.bat runData --console=plain` passed in 17 seconds. All 2,479 generated resources, including 1,261 recipes, are byte-for-byte identical to the preceding snapshot, with no missing, added, changed or duplicate resource paths. `.\gradlew.bat runGameTestServer --console=plain` passed in 40 seconds with all 119 required tests passing (19.36-second suite). Comparison, source-audit reports and transcripts are under `build/reports/migration/recipe-builders/`. This checkpoint did not run a release build, unit tests, the optional KubeJS configuration or a rendered client.

## Authored weapon models (2026-11-11)

`gluon_gun`, `lepton_cannon` and `antimatter_launcher` item models are owned by `src/main/resources/assets/qmd/models/item/`. They were copied from `legacy-src/`, preserving all geometry, UVs, display transforms and credits. Only texture identifiers changed from `qmd:items/tools/` to the migrated `qmd:item/tools/` paths. `QMDItemModelProvider` skips `ParticleWeaponItem` models.

`.\gradlew.bat compileJava compileDataJava runData --console=plain` passed in 19 seconds and removed the three stale generated models. There are now 2,476 generated JSON resources. `.\gradlew.bat processResources --console=plain` passed in 2 seconds after adding the authored models. Structural comparison confirms the copies match legacy with only the texture-path change, and all referenced textures exist. Reports are under `build/reports/migration/weapon-models/`. Generation also picked up the existing guide recipe provider's book-ID change to `qmd:qmd`; that source edit was preserved. No GameTests or rendered client checks were run for this model-only change.

## Authored machine and JEI textures (2026-11-11)

Twenty-five machine and JEI GUI textures are owned by `src/main/resources/assets/qmd/textures/gui/`. They were copied byte-for-byte from `legacy-src/`; the hash audit is `build/reports/migration/gui/assets.json`. The ore-leacher texture is intentionally available at both the raw GUI path for JEI and `textures/gui/sprites/screen/ore_leacher.png` for Neohaul's sprite-based screen API.

These are authored binary assets; no data provider or generated JSON changed, so this GUI migration did not require `runData`. See [GUI_MIGRATION.md](GUI_MIGRATION.md) for screen ownership and [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md) for the passing compilation and 121 required GameTests. These checks do not establish rendered appearance.

## Shared registry owners (2026-11-11)

Providers now reference the consolidated `QMDBlocks`, `QMDItems`, `QMDRecipes` and `QMDDamageTypes` owners. `QMDItemModelProvider` uses the explicit `QMDItems.EQUIPMENT` family to skip authored equipment models; the shared item register also contains all other item families. Fluid blocks and buckets now use the common block/item registers without changing their IDs or models.

`.\gradlew.bat runData --console=plain` passed in 26 seconds on `G:/QMD`. SHA-256 comparison confirms all 2,476 generated resources are byte-for-byte unchanged, with no added or removed paths. The final headless server passed all 121 required GameTests, including resource reload and native recipe-channel checks. Transcripts and `generated-parity.json` are under `build/reports/migration/registry/`. See [REGISTRY_REFACTOR.md](REGISTRY_REFACTOR.md) for class ownership.
