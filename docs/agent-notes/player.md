# Player content integration notes

This slice deliberately does not edit the shared entrypoints, generators, language file, or Gradle files. Apply the lines below when merging it into the complete migration tree.

## Common registration and config

Add these imports to `QuantumMinecraftDynamics.java`:

```java
import lach_01298.qmd.item.player.PlayerContentConfig;
import lach_01298.qmd.registry.QMDPlayerContent;
```

Register the facade with the other content registries in the mod constructor:

```java
QMDPlayerContent.register(modEventBus);
```

Register the player-content config with the other common configs:

```java
container.registerConfig(ModConfig.Type.COMMON, PlayerContentConfig.SPEC, "qmd-player.toml");
```

`QMDPlayerContent.register(modEventBus)` registers its blocks, items, menu, meter payload, and persistent luminous-paint radiation callback. Do not register the payload a second time.

## Creative tab

Add both lines to the `qmd:items` tab's `displayItems` callback:

```java
QMDPlayerContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
output.accept(QMDPlayerContent.manualStack());
```

Flesh remains `QMDIrradiator.FLESH`; do not add a second flesh registration.

## Client screen

Add these imports to `QMDClient.java`:

```java
import lach_01298.qmd.client.player.BeamMeterScreen;
import lach_01298.qmd.registry.QMDPlayerContent;
```

Add this line to `registerScreens(RegisterMenuScreensEvent event)`:

```java
event.register(QMDPlayerContent.BEAM_METER_MENU.get(), BeamMeterScreen::new);
```

## Patchouli dependency

Add this exact line to the existing `dependencies` block in `build.gradle`:

```groovy
implementation "maven.modrinth:nU0bVIaL:BIogJv2D"
```

Also add Patchouli as a required `BOTH`-side dependency in `src/main/templates/META-INF/neoforge.mods.toml`:

```toml
[[dependencies.${mod_id}]]
modId="patchouli"
type="required"
versionRange="[1.21.1-93,)"
ordering="AFTER"
side="BOTH"
```

The exact published artifact and rationale are recorded in `docs/agent-notes/player-deps.md`.

## Recipe data generation

Add this line to `RecipeCatalog.add(RecipeTable table)`:

```java
QmdPlayerContentRecipes.add(table);
```

The new table owns the legacy meter recipe, all six luminous-paint recipes, the four-tablet Neohaul assembler recipe, and the component-bearing Patchouli guide recipe. Run the normal `runData` workflow after integration; do not hand-write its output.

## Client asset data generation

Add this call in `QMDBlockStateProvider.run`:

```java
for (String color : java.util.List.of("green", "blue", "orange")) luminousPaint(assets, color);
```

Add this helper to that provider. The rotations match the six legacy attachment faces, while the vanilla carpet parent supplies the required 1/16-block geometry.

```java
private static void luminousPaint(AssetJson assets, String color) {
    String model = "qmd:block/" + color + "_luminous_paint";
    assets.model("block/" + color + "_luminous_paint", "minecraft:block/carpet",
            "wool", "qmd:block/other/" + color + "_luminous_paint");
    JsonObject variants = new JsonObject();
    variants.add("facing=down", AssetJson.variant(model, 180, 0, false));
    variants.add("facing=up", AssetJson.variant(model, 0, 0, false));
    variants.add("facing=north", AssetJson.variant(model, 90, 0, false));
    variants.add("facing=east", AssetJson.variant(model, 90, 90, false));
    variants.add("facing=south", AssetJson.variant(model, 270, 0, false));
    variants.add("facing=west", AssetJson.variant(model, 90, 270, false));
    assets.states("block_" + color + "_luminous_paint", variants);
}
```

In `QMDItemModelProvider`, handle `LuminousPaintItem` before the generic `BlockItem` branch so the legacy inventory icons remain flat:

```java
if (entry.getValue() instanceof lach_01298.qmd.item.player.LuminousPaintItem) {
    assets.model("item/" + name, "minecraft:item/generated", "layer0", "qmd:item/" + name);
} else if (entry.getValue() instanceof BlockItem) {
```

In `QMDBlockLootProvider`, use the associated paint item ID rather than assuming every block and item share a path:

```java
String drop = block instanceof lach_01298.qmd.item.player.LuminousPaintBlock
        ? BuiltInRegistries.ITEM.getKey(block.asItem()).toString() : id.toString();
```

Use `drop` for the loot entry's `name`. The runtime block also returns its associated `BlockItem` directly, so support loss remains correct before and after generated data is loaded.

## Authored textures

Copy the legacy PNGs to these modern, hand-authored texture paths during integration:

```text
items/tools/beam_meter.png               -> textures/item/beam_meter.png
items/misc/potassium_iodine_tablet.png   -> textures/item/potassium_iodine_tablet.png
items/misc/{color}_luminous_paint.png    -> textures/item/{color}_luminous_paint.png
blocks/other/{color}_luminous_paint.png  -> textures/block/other/{color}_luminous_paint.png
```

Do not copy the legacy flesh texture over the migrated `textures/item/flesh.png`.

## Language merge

Merge every key from `docs/agent-notes/player-lang.json` into `src/main/resources/assets/qmd/lang/en_us.json`. The fragment is valid JSON and intentionally lives outside the owned shared language file.

## Data-generation documentation

After `runData`, record these owners in `docs/DATA_GENERATION.md`: player item models, luminous-paint blockstates/models, paint loot tables, and player recipes are generated; the PNG textures, translations, and Patchouli book remain authored resources.
