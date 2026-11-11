package lach_01298.qmd.recipe.chamber;

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

/** Loaded chamber data must retain its concrete type through data and client synchronization codecs. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class ChamberRecipeGameTests {

    @GameTest(template = "empty")
    public static void loadedChamberRecipesRetainConcreteTypesAndEverySerializedField(GameTestHelper helper) {
        verify(helper, QMDRecipes.TARGET_CHAMBER_TYPE.get(), QMDRecipes.TARGET_CHAMBER_SERIALIZER.get(), TargetChamberRecipe.class);
        verify(helper, QMDRecipes.DECAY_CHAMBER_TYPE.get(), QMDRecipes.DECAY_CHAMBER_SERIALIZER.get(), DecayChamberRecipe.class);
        verify(helper, QMDRecipes.COLLISION_CHAMBER_TYPE.get(), QMDRecipes.COLLISION_CHAMBER_SERIALIZER.get(), CollisionChamberRecipe.class);
        verify(helper, QMDRecipes.BEAM_DUMP_TYPE.get(), QMDRecipes.BEAM_DUMP_SERIALIZER.get(), BeamDumpRecipe.class);
        verify(helper, QMDRecipes.NEUTRAL_CONTAINMENT_TYPE.get(), QMDRecipes.NEUTRAL_CONTAINMENT_SERIALIZER.get(), NeutralContainmentRecipe.class);
        verify(helper, QMDRecipes.CELL_FILLING_TYPE.get(), QMDRecipes.CELL_FILLING_SERIALIZER.get(), CellFillingRecipe.class);
        verify(helper, QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_TYPE.get(), QMDRecipes.NUCLEOSYNTHESIS_CHAMBER_SERIALIZER.get(), NucleosynthesisChamberRecipe.class);
        verify(helper, QMDRecipes.VACUUM_CHAMBER_HEATING_TYPE.get(), QMDRecipes.VACUUM_CHAMBER_HEATING_SERIALIZER.get(), VacuumChamberHeatingRecipe.class);
        helper.succeed();
    }

    private static <R extends AbstractChamberRecipe> void verify(GameTestHelper helper, RecipeType<R> type,
        AbstractChamberRecipe.Serializer<R> serializer, Class<R> recipeClass) {
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

    private ChamberRecipeGameTests() {}
}
