package lach_01298.qmd.datagen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/** Registers the explicit QMD factory builders and native Neohaul machine recipe builders. */
public final class QMDRecipeProvider extends RecipeProvider {
    private final MinecraftCraftingShapedRecipes shaped;
    private final MinecraftCraftingShapelessRecipes shapeless;
    private final MinecraftSmeltingRecipes smelting;

    public QMDRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        shaped = new MinecraftCraftingShapedRecipes(output, registries);
        shapeless = new MinecraftCraftingShapelessRecipes(output, registries);
        smelting = new MinecraftSmeltingRecipes(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        output = RecipeOutputs.withoutAdvancements(output);
        shaped.buildRecipes(output);
        shapeless.buildRecipes(output);
        smelting.buildRecipes(output);
        QmdEquipmentRecipes.add(output);
        QmdWeaponRecipes.add(output);
        QmdPlayerContentRecipes.add(output);
        QmdAcceleratorCoolingRecipes.add(output);
        QmdAcceleratorSourceRecipes.add(output);
        NeohaulAlloyFurnaceRecipeRecipes.add(output);
        NeohaulAssemblerRecipeRecipes.add(output);
        QmdAtmosphereCollectorRecipes.add(output);
        QmdBeamDumpRecipes.add(output);
        QmdCellFillingRecipes.add(output);
        NeohaulChemicalReactorRecipeRecipes.add(output);
        QmdCollisionChamberRecipes.add(output);
        NeohaulCondenserRecipeRecipes.add(output);
        NeohaulCrystallizerRecipeRecipes.add(output);
        QmdDecayChamberRecipes.add(output);
        NeohaulElectrolyzerRecipeRecipes.add(output);
        NeohaulEnricherRecipeRecipes.add(output);
        NeohaulFissionHeatingRecipeRecipes.add(output);
        NeohaulFissionIrradiatorRecipeRecipes.add(output);
        NeohaulInfuserRecipeRecipes.add(output);
        NeohaulFuelReprocessorRecipeRecipes.add(output);
        NeohaulHeatExchangerRecipeRecipes.add(output);
        NeohaulIngotFormerRecipeRecipes.add(output);
        QmdIrradiatorRecipes.add(output);
        QmdIrradiatorFuelRecipes.add(output);
        QmdLiquefierRecipes.add(output);
        QmdLiquefierCoolantRecipes.add(output);
        QmdLiquidCollectorRecipes.add(output);
        NeohaulManufactoryRecipeRecipes.add(output);
        QmdMassSpectrometerRecipes.add(output);
        NeohaulMelterRecipeRecipes.add(output);
        NeohaulMultiblockDistillerRecipeRecipes.add(output);
        QmdNeutralContainmentRecipes.add(output);
        QmdNucleosynthesisChamberRecipes.add(output);
        QmdOreLeacherRecipes.add(output);
        NeohaulPebbleFissionRecipeRecipes.add(output);
        NeohaulPressurizerRecipeRecipes.add(output);
        NeohaulSeparatorRecipeRecipes.add(output);
        NeohaulSolidFissionRecipeRecipes.add(output);
        NeohaulSupercoolerRecipeRecipes.add(output);
        QmdTargetChamberRecipes.add(output);
        NeohaulTurbineRecipeRecipes.add(output);
        QmdVacuumChamberHeatingRecipes.add(output);
    }

}
