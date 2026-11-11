# Equipment slice integration notes

The equipment implementation is intentionally self-contained. Add these exact one-line hooks during integration:

```java
QMDEquipment.register(modEventBus);
```

```java
container.registerConfig(ModConfig.Type.COMMON, EquipmentConfig.SPEC, "qmd-equipment.toml");
```

```java
QMDEquipment.creativeEntries(output);
```

```java
QMDEquipment.clientHooks(bus);
```

```java
QmdEquipmentRecipes.add(table);
```

Hook locations are respectively the common entry-point constructor, its config-registration block, the main creative tab's `displayItems` lambda, the client entry-point constructor, and `RecipeCatalog.add`. Add the corresponding imports. Do not register a second recipe provider: the existing `QMDRecipeProvider` calls `RecipeCatalog`.

`QMDEquipment.register` owns the deferred item/component registrations, item energy capabilities, the `qmd:equipment_enabled` recipe condition codec, and common HEV event hooks. `clientHooks` owns client-only tint/model or armor-render hooks and must only be called by `QMDClient`. The two legacy switches default to enabled; disabled families remain out of creative entries and their stable-ID recipes are removed by the generated conditions.

Merge `docs/agent-notes/equip-lang.json` into `en_us.json`, `ru_ru.json`, and `zh_cn.json` by locale. Do not replace the existing language objects.

After adding `QmdEquipmentRecipes.add(table)`, run `./gradlew.bat runData --console=plain` from the integrated tree and include generated recipe changes. Do not hand-edit generated JSON. The special recipe must keep the ID and serializer type `qmd:armour_dye`; its runtime serializer is registered by `QMDEquipment`.

The isolated rescue worktree does not contain the current `RecipeTable`, `RecipeCatalog`, or most classes imported by its tracked `QuantumMinecraftDynamics.java`; they exist only in the read-only `G:/QMD` integration tree. Therefore `compileJava` can fail on unrelated missing config/network/registry classes, and `compileDataJava` cannot compile `QmdEquipmentRecipes` here. Preserve the exact Gradle result in this note if compilation is attempted; do not copy unrelated integration files into this worktree to mask the blocker.
