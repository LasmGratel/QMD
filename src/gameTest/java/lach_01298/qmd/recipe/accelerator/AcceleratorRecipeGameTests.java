package lach_01298.qmd.recipe.accelerator;

import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

/** Loaded accelerator data must retain its concrete type through data and client synchronization codecs. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class AcceleratorRecipeGameTests {

    @GameTest(template = "empty")
    public static void loadedAcceleratorRecipesRetainConcreteTypesAndEverySerializedField(GameTestHelper helper) {
        verify(helper, QMDRecipes.ACCELERATOR_SOURCE_TYPE.get(), QMDRecipes.ACCELERATOR_SOURCE_SERIALIZER.get(), AcceleratorSourceRecipe.class);
        verify(helper, QMDRecipes.ACCELERATOR_COOLING_TYPE.get(), QMDRecipes.ACCELERATOR_COOLING_SERIALIZER.get(), AcceleratorCoolingRecipe.class);
        verify(helper, QMDRecipes.MASS_SPECTROMETER_TYPE.get(), QMDRecipes.MASS_SPECTROMETER_SERIALIZER.get(), MassSpectrometerRecipe.class);
        helper.succeed();
    }

    private static <R extends AbstractAcceleratorRecipe> void verify(GameTestHelper helper, RecipeType<R> type,
        AbstractAcceleratorRecipe.Serializer<R> serializer, Class<R> recipeClass) {
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(type);
        helper.assertTrue(!recipes.isEmpty(), "The real data pack must load recipes for " + type);
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var codec = serializer.codec().codec();
        for (var holder : recipes) {
            R original = holder.value();
            helper.assertTrue(original.getClass() == recipeClass && original.getType() == type
                && original.getSerializer() == serializer, "Loaded recipe must use its concrete registered type: " + holder.id());
            var encoded = codec.encodeStart(ops, original).getOrThrow();
            R decoded = codec.parse(ops, encoded).getOrThrow();
            helper.assertTrue(decoded.getClass() == recipeClass
                && codec.encodeStart(ops, decoded).getOrThrow().equals(encoded),
                "Data codec must preserve every ingredient, physical channel, exact energy and reaction value: " + holder.id());
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
            try {
                serializer.streamCodec().encode(buffer, original);
                R synced = serializer.streamCodec().decode(buffer);
                helper.assertTrue(synced.getClass() == recipeClass && synced.getType() == type
                    && synced.getSerializer() == serializer && buffer.readableBytes() == 0
                    && codec.encodeStart(ops, synced).getOrThrow().equals(encoded),
                    "Network codec must preserve the concrete recipe and its complete payload: " + holder.id());
            } finally {
                buffer.release();
            }
        }
    }

    private AcceleratorRecipeGameTests() {}
}
