package lach_01298.qmd.recipe;

import com.mojang.serialization.JsonOps;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import io.netty.buffer.Unpooled;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.List;

/** Exercise the real serializer with the server's loaded item/fluid registries and tags. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class OreLeacherRecipeGameTests {
    private static final ResourceLocation GOLD_RECIPE = ResourceLocation.parse("qmd:ore_leacher/gold");

    private OreLeacherRecipeGameTests() {
    }

    @GameTest(template = "empty")
    public static void loadedGoldRecipeSurvivesDataAndNetworkCodecs(GameTestHelper helper) {
        OreLeacherRecipe loaded = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(QMDRecipes.ORE_LEACHER_TYPE.get()).stream()
                .filter(holder -> holder.id().equals(GOLD_RECIPE)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Default gold ore leacher recipe must load")).value();
        helper.assertTrue(loaded.getItemIngredients().size() == 1
                        && loaded.getFluidIngredients().size() == 3
                        && loaded.getItemProducts().size() == 3 && loaded.getFluidProducts().isEmpty(),
                "Loaded recipe must have one item input, three fluid inputs, three padded products and no fluid output");
        helper.assertTrue(loaded.getItemIngredients().getFirst().test(new ItemStack(Items.GOLD_ORE)),
                "Loaded gold recipe must accept vanilla gold ore");
        ItemStack primary = loaded.getItemProducts().getFirst().getStackRaw();
        helper.assertTrue(primary.is(Items.GOLD_INGOT) && primary.getCount() == 3,
                "Empty gold dust tag must fall back to three gold ingots, without a Barrier product");
        helper.assertTrue(loaded.getItemProducts().get(1).chancePercent() == 10
                        && loaded.getItemProducts().get(1).count() == 1
                        && loaded.getItemProducts().get(1).minStackSize() == 0
                        && loaded.getItemProducts().get(2).isEmpty(),
                "Loaded gold recipe must retain its ten-percent silver byproduct and empty third output");
        String[] acids = {"qmd:nitric_acid", "qmd:hydrochloric_acid", "nuclearcraftneohaul:sulfuric_acid"};
        for (int tank = 0; tank < 3; ++tank) {
            SizedChanceFluidIngredient ingredient = loaded.getFluidIngredients().get(tank);
            helper.assertTrue(ingredient.amount() == 16
                            && BuiltInRegistries.FLUID.getKey(ingredient.getStackRaw().getFluid()).equals(ResourceLocation.parse(acids[tank])),
                    "Each acid must resolve to the expected registered fluid and consume exactly sixteen mB");
        }

        assertRoundTrips(helper, loaded);

        // Non-default values expose lost fields that an all-1x default recipe would hide.
        OreLeacherRecipe modified = new OreLeacherRecipe(loaded.getItemIngredients(), List.of(
                loaded.getItemProducts().getFirst(),
                new SizedChanceItemIngredient(loaded.getItemProducts().get(1).ingredient(), 2, 50, 1)),
                loaded.getFluidIngredients(), List.of(), 1.25, 0.75, 0.125);
        assertRoundTrips(helper, modified);
        helper.succeed();
    }

    private static void assertRoundTrips(GameTestHelper helper, OreLeacherRecipe original) {
        var serializer = QMDRecipes.ORE_LEACHER_SERIALIZER.get();
        var registryOps = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var encoded = serializer.codec().codec().encodeStart(registryOps, original).getOrThrow();
        OreLeacherRecipe fromData = serializer.codec().codec().parse(registryOps, encoded).getOrThrow();
        assertEquivalent(helper, original, fromData, "data codec");

        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
        try {
            serializer.streamCodec().encode(buffer, original);
            OreLeacherRecipe fromNetwork = serializer.streamCodec().decode(buffer);
            helper.assertTrue(buffer.readableBytes() == 0, "Network decoder must consume the complete recipe payload");
            assertEquivalent(helper, original, fromNetwork, "network codec");
        } finally {
            buffer.release();
        }
    }

    private static void assertEquivalent(GameTestHelper helper, OreLeacherRecipe original,
                                         OreLeacherRecipe decoded, String codec) {
        helper.assertTrue(decoded.getItemIngredients().size() == 1
                        && decoded.getFluidIngredients().size() == 3
                        && decoded.getItemProducts().size() == 3 && decoded.getFluidProducts().isEmpty(),
                codec + " must preserve machine dimensions and padded outputs");
        helper.assertTrue(original.getProcessTimeMultiplier() == decoded.getProcessTimeMultiplier()
                        && original.getProcessPowerMultiplier() == decoded.getProcessPowerMultiplier()
                        && original.getBaseProcessRadiation() == decoded.getBaseProcessRadiation(),
                codec + " must preserve time, power and radiation modifiers");
        assertItems(helper, original.getItemIngredients().getFirst(), decoded.getItemIngredients().getFirst(), codec);
        for (int product = 0; product < 3; ++product) {
            assertItems(helper, original.getItemProducts().get(product), decoded.getItemProducts().get(product), codec);
        }
        for (int tank = 0; tank < 3; ++tank) {
            SizedChanceFluidIngredient before = original.getFluidIngredients().get(tank);
            SizedChanceFluidIngredient after = decoded.getFluidIngredients().get(tank);
            helper.assertTrue(before.amount() == after.amount() && before.chancePercent() == after.chancePercent()
                            && before.minStackSize() == after.minStackSize() && before.increment() == after.increment(),
                    codec + " must preserve fluid quantity and deterministic-consumption fields");
            FluidStack[] beforeStacks = before.getFluidsRaw();
            FluidStack[] afterStacks = after.getFluidsRaw();
            helper.assertTrue(beforeStacks.length == afterStacks.length, codec + " must preserve all fluid alternatives");
            for (int index = 0; index < beforeStacks.length; ++index) {
                helper.assertTrue(FluidStack.matches(beforeStacks[index], afterStacks[index]),
                        codec + " must preserve registered fluid identities and amounts");
            }
        }
    }

    private static void assertItems(GameTestHelper helper, SizedChanceItemIngredient before,
                                    SizedChanceItemIngredient after, String codec) {
        helper.assertTrue(before.count() == after.count() && before.chancePercent() == after.chancePercent()
                        && before.minStackSize() == after.minStackSize() && before.isEmpty() == after.isEmpty(),
                codec + " must preserve item counts, chances, minima and empty products");
        ItemStack[] beforeStacks = before.getItemsRaw();
        ItemStack[] afterStacks = after.getItemsRaw();
        helper.assertTrue(beforeStacks.length == afterStacks.length, codec + " must preserve all item alternatives");
        for (int index = 0; index < beforeStacks.length; ++index) {
            helper.assertTrue(ItemStack.matches(beforeStacks[index], afterStacks[index]),
                    codec + " must preserve ordered item identities and counts");
        }
    }
}
