package lach_01298.qmd.compat.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** One-object convenience builders that preserve the serializers' native JSON field names. */
final class QMDRecipeSchemas {

    private static final List<String> SERIALIZERS = List.of(
        "ore_leacher",
        "liquid_collector",
        "atmosphere_collector",
        "accelerator_source",
        "accelerator_cooling",
        "mass_spectrometer",
        "target_chamber",
        "decay_chamber",
        "collision_chamber",
        "beam_dump",
        "neutral_containment",
        "cell_filling",
        "nucleosynthesis_chamber",
        "vacuum_chamber_heating",
        "irradiator",
        "irradiator_fuel",
        "liquefier",
        "liquefier_coolant");

    private QMDRecipeSchemas() {}

    static void registerComponents(RecipeComponentTypeRegistry registry) {
        registry.register(PayloadComponent.TYPE);
    }

    static void registerSchemas(RecipeSchemaRegistry registry) {
        var namespace = registry.namespace("qmd");
        for (String serializer : SERIALIZERS) {
            namespace.register(serializer, new RecipeSchema(PayloadComponent.INSTANCE.otherKey("recipe")));
        }
    }

    static final class PayloadComponent implements RecipeComponent<JsonObject> {

        static final RecipeComponentType<JsonObject> TYPE = RecipeComponentType
            .unit(ResourceLocation.fromNamespaceAndPath("qmd", "codec_payload"), PayloadComponent::new);
        static final PayloadComponent INSTANCE = (PayloadComponent) TYPE.instance();
        private static final Codec<JsonObject> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
            JsonElement value = dynamic.convert(JsonOps.INSTANCE)
                .getValue();
            return value instanceof JsonObject object ? DataResult.success((JsonObject) JsonUtils.copy(object))
                : DataResult.error(() -> "Expected a QMD recipe object");
        }, object -> new Dynamic<>(JsonOps.INSTANCE, JsonUtils.copy(object)));

        private final RecipeComponentType<JsonObject> type;

        private PayloadComponent(RecipeComponentType<JsonObject> type) {
            this.type = type;
        }

        @Override
        public RecipeComponentType<?> type() {
            return type;
        }

        @Override
        public Codec<JsonObject> codec() {
            return CODEC;
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.RAW_MAP;
        }

        @Override
        public JsonObject wrap(RecipeScriptContext context, Object from) {
            JsonObject object = JsonUtils.objectOf(context.cx(), from);
            if (object == null) {
                throw new IllegalArgumentException("QMD recipe builder requires one object argument");
            }
            if (object.has("type")) {
                throw new IllegalArgumentException("QMD recipe builder payload must not contain 'type'");
            }
            return (JsonObject) JsonUtils.copy(object);
        }

        @Override
        public void writeToJson(KubeRecipe recipe, RecipeComponentValue<JsonObject> value, JsonObject json) {
            json.entrySet()
                .removeIf(
                    entry -> !entry.getKey()
                        .equals("type"));
            for (var entry : value.value.entrySet()) {
                if (!entry.getKey()
                    .equals("type")
                    && !entry.getKey()
                        .equals(KubeRecipe.CHANGED_MARKER)) {
                    json.add(entry.getKey(), JsonUtils.copy(entry.getValue()));
                }
            }
        }

        @Override
        public void readFromJson(KubeRecipe recipe, RecipeComponentValue<JsonObject> value, JsonObject json) {
            JsonObject payload = new JsonObject();
            for (var entry : json.entrySet()) {
                if (!entry.getKey()
                    .equals("type")
                    && !entry.getKey()
                        .equals(KubeRecipe.CHANGED_MARKER)) {
                    payload.add(entry.getKey(), JsonUtils.copy(entry.getValue()));
                }
            }
            value.value = payload;
        }

        @Override
        public boolean isEmpty(JsonObject value) {
            return value.isEmpty();
        }

        @Override
        public void buildUniqueId(dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder builder, JsonObject value) {
            builder.append(JsonUtils.toString(value));
        }
    }
}
