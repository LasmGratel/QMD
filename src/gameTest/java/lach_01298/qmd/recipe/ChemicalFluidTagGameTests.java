package lach_01298.qmd.recipe;

import com.mojang.serialization.JsonOps;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.registry.QMDFluids;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class ChemicalFluidTagGameTests {
    @GameTest(template = "empty")
    public static void loadedCommonChemicalInputsResolveAndRespectAmounts(GameTestHelper helper) {
        int checked = 0;
        for (var holder : helper.getLevel().getRecipeManager().getRecipes()) {
            if (!holder.id().getNamespace().equals("qmd")) continue;
            var resource = ResourceLocation.fromNamespaceAndPath("qmd", "recipe/" + holder.id().getPath() + ".json");
            if (helper.getLevel().getServer().getResourceManager().getResource(resource).isEmpty()) continue;
            var recipe = holder.value();
            List<SizedChanceFluidIngredient> inputs;
            if (recipe instanceof ProcessorRecipe processor) inputs = processor.getFluidIngredients();
            else if (recipe instanceof AbstractChamberRecipe chamber) inputs = chamber.getFluidIngredients();
            else if (recipe instanceof AbstractAcceleratorRecipe accelerator) inputs = List.of(accelerator.fluid());
            else if (recipe instanceof LiquefierRecipe liquefier)
                inputs = List.of(new SizedChanceFluidIngredient(liquefier.ingredient(), liquefier.amount()));
            else continue;
            for (var input : inputs) {
                if (input.isEmpty()) continue;
                var encoded = FluidIngredient.CODEC.encodeStart(JsonOps.INSTANCE, input.ingredient()).getOrThrow().getAsJsonObject();
                if (!encoded.has("tag")) continue;
                helper.assertTrue(encoded.get("tag").getAsString().startsWith("c:"),
                    "Chemical input must use a common tag: " + holder.id());
                helper.assertTrue(input.getFluidsRaw().length > 0,
                    "Chemical tag must resolve actual fluids, including Neohaul chemicals: " + holder.id() + " " + encoded);
                for (var stack : input.getFluidsRaw()) {
                    helper.assertTrue(input.sized().test(stack), "Every displayed fluid alternative must match: " + holder.id());
                    helper.assertTrue(!input.sized().test(stack.copyWithAmount(input.amount() - 1)),
                        "An alternative must still supply the full amount: " + holder.id());
                }
                checked++;
            }
        }
        helper.assertTrue(checked > 0, "The generated chemical recipe catalog must be loaded");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void taggedAlternativesWorkAcrossNativeMachinesAndQmdCooling(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        var chemical = (ProcessorRecipe) manager.byKey(ResourceLocation.parse("qmd:chemical_reactor/hydrochloric_acid"))
            .orElseThrow().value();
        var chlorine = new FluidStack(QMDFluids.CHLORINE.flowing.get(), 1000);
        helper.assertTrue(chemical.getFluidIngredients().stream().anyMatch(input -> input.sized().test(chlorine)),
            "The native chemical reactor accepts an equivalent fluid from the chlorine tag");
        var cooling = manager.getAllRecipesFor(QMDRecipes.ACCELERATOR_COOLING_TYPE.get()).stream()
            .filter(holder -> holder.id().equals(ResourceLocation.parse("qmd:accelerator_cooling/water")))
            .findFirst().orElseThrow().value();
        helper.assertTrue(cooling.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY,
            new FluidStack(Fluids.FLOWING_WATER, 1)), helper.getLevel()),
            "Accelerator and vacuum cooling accept every water-tag alternative");
        helper.assertTrue(!cooling.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY,
            new FluidStack(Fluids.LAVA, 1)), helper.getLevel()), "Unrelated fluid cannot cool the apparatus");
        var liquefier = manager.getAllRecipesFor(QMDRecipes.LIQUEFIER_GAS_TYPE.get()).stream()
            .filter(holder -> holder.id().equals(ResourceLocation.parse("qmd:liquefier/argon")))
            .findFirst().orElseThrow().value();
        helper.assertTrue(liquefier.ingredient().test(new FluidStack(QMDFluids.ARGON.flowing.get(), 64)),
            "Liquefaction accepts the common argon-tag alternative");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void legacyAcceleratorStackInputsRetainStrictComponents(GameTestHelper helper) {
        var serializer = QMDRecipes.ACCELERATOR_COOLING_SERIALIZER.get();
        var original = helper.getLevel().getRecipeManager().getAllRecipesFor(QMDRecipes.ACCELERATOR_COOLING_TYPE.get())
            .getFirst().value();
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var json = serializer.codec().codec().encodeStart(ops, original).getOrThrow().getAsJsonObject();
        var expected = new FluidStack(Fluids.WATER, 17);
        expected.set(DataComponents.CUSTOM_NAME, Component.literal("Component-sensitive coolant"));
        json.add("fluid", FluidStack.CODEC.encodeStart(ops, expected).getOrThrow());
        var legacy = serializer.codec().codec().parse(ops, json).getOrThrow();
        helper.assertTrue(legacy.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY, expected), helper.getLevel()),
            "Existing stack-based datapacks remain readable");
        helper.assertTrue(!legacy.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY,
            new FluidStack(Fluids.WATER, 17)), helper.getLevel()), "Legacy component matching remains strict");
        helper.assertTrue(!legacy.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY,
            expected.copyWithAmount(16)), helper.getLevel()), "Legacy volume is still required");
        var canonical = serializer.codec().codec().encodeStart(ops, legacy).getOrThrow();
        var decoded = serializer.codec().codec().parse(ops, canonical).getOrThrow();
        helper.assertTrue(decoded.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY, expected), helper.getLevel())
            && !decoded.matches(new AbstractAcceleratorRecipe.Input(ItemStack.EMPTY, new FluidStack(Fluids.WATER, 17)), helper.getLevel()),
            "Converting a legacy input to the native ingredient format retains components");
        helper.succeed();
    }

    private ChemicalFluidTagGameTests() {}
}
