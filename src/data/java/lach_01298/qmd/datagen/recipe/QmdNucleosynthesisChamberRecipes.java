package lach_01298.qmd.datagen.recipe;

import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import lach_01298.qmd.recipe.chamber.NucleosynthesisChamberRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed NucleosynthesisChamber recipe declarations. */
final class QmdNucleosynthesisChamberRecipes {
    static void add(RecipeOutput output) {
        part0(output);
        part1(output);
        part2(output);
        part3(output);
        part4(output);
    }

    private static void part0(RecipeOutput output) {
        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .beamInput(beamInput(951300, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:deuterium"), 500))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(1950000D)
                .save(output, id("qmd:nucleosynthesis_chamber/000_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:deuterium"), 500))
                .beamInput(beamInput(143100, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5490000D)
                .save(output, id("qmd:nucleosynthesis_chamber/001_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .beamInput(beamInput(290000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 1000))
                .heat(12900000D)
                .save(output, id("qmd:nucleosynthesis_chamber/002_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:deuterium"), 500))
                .fluidInput(sizedFluid(tagFluid("c:tritium"), 500))
                .beamInput(beamInput(155900, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(17600000D)
                .save(output, id("qmd:nucleosynthesis_chamber/003_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:tritium"), 500))
                .fluidInput(sizedFluid(tagFluid("c:tritium"), 500))
                .beamInput(beamInput(161600, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(11300000D)
                .save(output, id("qmd:nucleosynthesis_chamber/004_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:deuterium"), 500))
                .fluidInput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .beamInput(beamInput(200600, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .heat(18400000D)
                .save(output, id("qmd:nucleosynthesis_chamber/005_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:lithium_7"), 144))
                .beamInput(beamInput(223000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 2000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(17300000D)
                .save(output, id("qmd:nucleosynthesis_chamber/006_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:deuterium"), 500))
                .fluidInput(sizedFluid(tagFluid("c:lithium_6"), 144))
                .beamInput(beamInput(252200, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 2000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(22400000D)
                .save(output, id("qmd:nucleosynthesis_chamber/007_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:tritium"), 500))
                .fluidInput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .beamInput(beamInput(210200, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .heat(12100000D)
                .save(output, id("qmd:nucleosynthesis_chamber/008_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:deuterium"), 500))
                .fluidInput(sizedFluid(tagFluid("c:deuterium"), 500))
                .beamInput(beamInput(151600, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:tritium"), 500))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .heat(4030000D)
                .save(output, id("qmd:nucleosynthesis_chamber/009_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:lithium_6"), 144))
                .beamInput(beamInput(385700, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 2000))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .heat(16900000D)
                .save(output, id("qmd:nucleosynthesis_chamber/010_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:boron_11"), 144))
                .beamInput(beamInput(286700, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 3000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(8680000D)
                .save(output, id("qmd:nucleosynthesis_chamber/011_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:lithium_6"), 144))
                .beamInput(beamInput(222000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidOutput(sizedFluid(tagFluid("c:helium_3"), 1000))
                .heat(4020000D)
                .save(output, id("qmd:nucleosynthesis_chamber/012_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:carbon"), 100))
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 1000))
                .beamInput(beamInput(541800, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:nitrogen"), 500))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(10700000D)
                .save(output, id("qmd:nucleosynthesis_chamber/013_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:nitrogen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:hydrogen"), 1000))
                .beamInput(beamInput(594700, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:carbon"), 100))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .heat(14000000D)
                .save(output, id("qmd:nucleosynthesis_chamber/014_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:helium"), 2000))
                .beamInput(beamInput(604600, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:carbon"), 100))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7160000D)
                .save(output, id("qmd:nucleosynthesis_chamber/015_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:carbon"), 100))
                .beamInput(beamInput(635200, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:oxygen"), 500))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4730000D)
                .save(output, id("qmd:nucleosynthesis_chamber/016_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:oxygen"), 500))
                .beamInput(beamInput(763800, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:neon"), 1000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9320000D)
                .save(output, id("qmd:nucleosynthesis_chamber/017_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:neon"), 1000))
                .beamInput(beamInput(880100, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:magnesium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6980000D)
                .save(output, id("qmd:nucleosynthesis_chamber/018_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:magnesium"), 144))
                .beamInput(beamInput(987300, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:silicon"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6980000D)
                .save(output, id("qmd:nucleosynthesis_chamber/019_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:silicon"), 144))
                .beamInput(beamInput(1088000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:sulfur"), 666))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6950000D)
                .save(output, id("qmd:nucleosynthesis_chamber/020_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:sulfur"), 666))
                .beamInput(beamInput(1182000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:argon"), 1000))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6640000D)
                .save(output, id("qmd:nucleosynthesis_chamber/021_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:argon"), 1000))
                .beamInput(beamInput(1272000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:calcium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7040000D)
                .save(output, id("qmd:nucleosynthesis_chamber/022_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:calcium"), 144))
                .beamInput(beamInput(2456000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:titanium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(18500000D)
                .save(output, id("qmd:nucleosynthesis_chamber/023_muon"));
    }

    private static void part1(RecipeOutput output) {
        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 2000))
                .fluidInput(sizedFluid(tagFluid("c:titanium"), 144))
                .beamInput(beamInput(1443000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:chromium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9350000D)
                .save(output, id("qmd:nucleosynthesis_chamber/024_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:helium"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:chromium"), 144))
                .beamInput(beamInput(1522000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:iron"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7610000D)
                .save(output, id("qmd:nucleosynthesis_chamber/025_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:oxygen"), 500))
                .fluidInput(sizedFluid(tagFluid("c:oxygen"), 500))
                .beamInput(beamInput(2270000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:silicon"), 144))
                .fluidOutput(sizedFluid(tagFluid("c:helium"), 1000))
                .heat(9590000D)
                .save(output, id("qmd:nucleosynthesis_chamber/026_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:carbon"), 100))
                .fluidInput(sizedFluid(tagFluid("c:carbon"), 100))
                .beamInput(beamInput(1480000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:sodium"), 144))
                .fluidOutput(sizedFluid(tagFluid("c:hydrogen"), 500))
                .heat(2240000D)
                .save(output, id("qmd:nucleosynthesis_chamber/027_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:neon"), 1000))
                .fluidInput(sizedFluid(tagFluid("c:neon"), 1000))
                .beamInput(beamInput(3168000, 0L, 1000L, 1D, id("qmd:muon")))
                .fluidOutput(sizedFluid(tagFluid("c:magnesium"), 144))
                .fluidOutput(sizedFluid(tagFluid("c:oxygen"), 500))
                .heat(4590000D)
                .save(output, id("qmd:nucleosynthesis_chamber/028_muon"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:iron"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(15410000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:copper"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7930000D)
                .save(output, id("qmd:nucleosynthesis_chamber/029_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:cobalt"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(10150000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:copper"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5350000D)
                .save(output, id("qmd:nucleosynthesis_chamber/030_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:nickel"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(16990000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:zinc"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9000000D)
                .save(output, id("qmd:nucleosynthesis_chamber/031_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:copper"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(20010000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:arsenic"), 666))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(10400000D)
                .save(output, id("qmd:nucleosynthesis_chamber/032_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:zinc"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(18270000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:arsenic"), 666))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9580000D)
                .save(output, id("qmd:nucleosynthesis_chamber/033_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:arsenic"), 666))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(20800000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:strontium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(12000000D)
                .save(output, id("qmd:nucleosynthesis_chamber/034_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:strontium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6157000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:zirconium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3280000D)
                .save(output, id("qmd:nucleosynthesis_chamber/035_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:strontium_90"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(12250000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6670000D)
                .save(output, id("qmd:nucleosynthesis_chamber/036_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:yttrium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(4605000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:zirconium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2500000D)
                .save(output, id("qmd:nucleosynthesis_chamber/037_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:zirconium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(24490000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:ruthenium_106"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(12700000D)
                .save(output, id("qmd:nucleosynthesis_chamber/038_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:niobium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7591000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4130000D)
                .save(output, id("qmd:nucleosynthesis_chamber/039_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(11990000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:ruthenium_106"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6280000D)
                .save(output, id("qmd:nucleosynthesis_chamber/040_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:ruthenium_106"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(4406000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:silver"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2660000D)
                .save(output, id("qmd:nucleosynthesis_chamber/041_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:palladium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(20560000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:tin"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(11400000D)
                .save(output, id("qmd:nucleosynthesis_chamber/042_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:silver"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(29300000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:iodine"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(16200000D)
                .save(output, id("qmd:nucleosynthesis_chamber/043_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:tin"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(9953000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:iodine"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5440000D)
                .save(output, id("qmd:nucleosynthesis_chamber/044_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:iodine"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(14010000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:caesium_137"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7830000D)
                .save(output, id("qmd:nucleosynthesis_chamber/045_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:caesium_137"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(20590000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:samarium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(10900000D)
                .save(output, id("qmd:nucleosynthesis_chamber/046_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:barium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(12330000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:promethium_147"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6340000D)
                .save(output, id("qmd:nucleosynthesis_chamber/047_neutron"));
    }

    private static void part2(RecipeOutput output) {
        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:neodymium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(17670000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:europium_155"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9080000D)
                .save(output, id("qmd:nucleosynthesis_chamber/048_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:promethium_147"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(14810000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:gadolinium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(8040000D)
                .save(output, id("qmd:nucleosynthesis_chamber/049_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:samarium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(8004000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:gadolinium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4430000D)
                .save(output, id("qmd:nucleosynthesis_chamber/050_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:europium_155"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(11940000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:dysprosium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6680000D)
                .save(output, id("qmd:nucleosynthesis_chamber/051_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:gadolinium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(9238000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:holmium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5070000D)
                .save(output, id("qmd:nucleosynthesis_chamber/052_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:dysprosium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(5224000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:erbium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2930000D)
                .save(output, id("qmd:nucleosynthesis_chamber/053_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:holmium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(16950000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:hafnium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9250000D)
                .save(output, id("qmd:nucleosynthesis_chamber/054_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:erbium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(18220000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:hafnium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9790000D)
                .save(output, id("qmd:nucleosynthesis_chamber/055_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:ytterbium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(12840000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:tungsten"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6950000D)
                .save(output, id("qmd:nucleosynthesis_chamber/056_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hafnium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(12720000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:osmium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6960000D)
                .save(output, id("qmd:nucleosynthesis_chamber/057_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:tungsten"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(13910000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:platinum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7590000D)
                .save(output, id("qmd:nucleosynthesis_chamber/058_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:osmium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6244000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:gold"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3560000D)
                .save(output, id("qmd:nucleosynthesis_chamber/059_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:iridium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(4987000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:gold"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2890000D)
                .save(output, id("qmd:nucleosynthesis_chamber/060_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:platinum"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(16160000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:lead"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(9390000D)
                .save(output, id("qmd:nucleosynthesis_chamber/061_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:gold"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(13630000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:lead"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7940000D)
                .save(output, id("qmd:nucleosynthesis_chamber/062_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:mercury"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(8613000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:bismuth"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4740000D)
                .save(output, id("qmd:nucleosynthesis_chamber/063_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:lead"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(1220000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:bismuth"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(458000D)
                .save(output, id("qmd:nucleosynthesis_chamber/064_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:bismuth"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(1218000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:polonium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(577000D)
                .save(output, id("qmd:nucleosynthesis_chamber/065_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:polonium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(19460000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:radium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(8950000D)
                .save(output, id("qmd:nucleosynthesis_chamber/066_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:radium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7142000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:thorium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3660000D)
                .save(output, id("qmd:nucleosynthesis_chamber/067_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:thorium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7086000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:uranium_238"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3660000D)
                .save(output, id("qmd:nucleosynthesis_chamber/068_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:uranium_238"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(4688000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:plutonium_242"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2490000D)
                .save(output, id("qmd:nucleosynthesis_chamber/069_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:plutonium_242"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(5831000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:curium_247"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2950000D)
                .save(output, id("qmd:nucleosynthesis_chamber/070_neutron"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:curium_247"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(5795000, 0L, 10000L, 1D, id("qmd:neutron")))
                .fluidOutput(sizedFluid(tagFluid("c:californium_252"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2990000D)
                .save(output, id("qmd:nucleosynthesis_chamber/071_neutron"));
    }

    private static void part3(RecipeOutput output) {
        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:iron"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(2702000, 7080L, 17100L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:nickel"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(1420000D)
                .save(output, id("qmd:nucleosynthesis_chamber/072_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:cobalt"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7213000, 7900L, 17900L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:zinc"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3920000D)
                .save(output, id("qmd:nucleosynthesis_chamber/073_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:arsenic"), 666))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(14070000, 9660L, 19700L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:strontium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7120000D)
                .save(output, id("qmd:nucleosynthesis_chamber/074_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:strontium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(9115000, 9810L, 19800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4320000D)
                .save(output, id("qmd:nucleosynthesis_chamber/075_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:strontium_90"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(4338000, 9150L, 19200L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:niobium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2310000D)
                .save(output, id("qmd:nucleosynthesis_chamber/076_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:yttrium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7574000, 9810L, 19800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3610000D)
                .save(output, id("qmd:nucleosynthesis_chamber/077_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:zirconium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6043000, 9810L, 19800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2780000D)
                .save(output, id("qmd:nucleosynthesis_chamber/078_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:molybdenum"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(14270000, 11000L, 21000L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:silver"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6380000D)
                .save(output, id("qmd:nucleosynthesis_chamber/079_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:ruthenium_106"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(15860000, 11400L, 21400L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:tin"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7610000D)
                .save(output, id("qmd:nucleosynthesis_chamber/080_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:palladium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(16220000, 11800L, 21800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:tin"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7140000D)
                .save(output, id("qmd:nucleosynthesis_chamber/081_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:silver"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(14560000, 11800L, 21800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:tin"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(6570000D)
                .save(output, id("qmd:nucleosynthesis_chamber/082_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:tin"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(19540000, 12600L, 22600L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:barium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(8170000D)
                .save(output, id("qmd:nucleosynthesis_chamber/083_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:iodine"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7706000, 11800L, 21800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:barium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3490000D)
                .save(output, id("qmd:nucleosynthesis_chamber/084_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:caesium_137"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7599000, 11900L, 21900L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:neodymium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3580000D)
                .save(output, id("qmd:nucleosynthesis_chamber/085_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:barium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6067000, 11900L, 21900L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:neodymium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2680000D)
                .save(output, id("qmd:nucleosynthesis_chamber/086_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:neodymium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(3059000, 12300L, 22300L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:samarium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(1060000D)
                .save(output, id("qmd:nucleosynthesis_chamber/087_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:promethium_147"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(14410000, 13600L, 23600L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:dysprosium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5500000D)
                .save(output, id("qmd:nucleosynthesis_chamber/088_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:samarium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6133000, 12800L, 22800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:dysprosium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2490000D)
                .save(output, id("qmd:nucleosynthesis_chamber/089_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:europium_155"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(17690000, 14100L, 24100L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:erbium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(7020000D)
                .save(output, id("qmd:nucleosynthesis_chamber/090_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:gadolinium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(12600000, 13700L, 23700L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:erbium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5050000D)
                .save(output, id("qmd:nucleosynthesis_chamber/091_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:dysprosium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(9307000, 13600L, 23600L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:ytterbium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3750000D)
                .save(output, id("qmd:nucleosynthesis_chamber/092_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:holmium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(7741000, 13600L, 23600L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:ytterbium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3130000D)
                .save(output, id("qmd:nucleosynthesis_chamber/093_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:erbium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6182000, 13600L, 23600L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:ytterbium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2400000D)
                .save(output, id("qmd:nucleosynthesis_chamber/094_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:ytterbium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(15920000, 14800L, 24800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:osmium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5810000D)
                .save(output, id("qmd:nucleosynthesis_chamber/095_proton"));
    }

    private static void part4(RecipeOutput output) {
        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:hafnium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(15880000, 15000L, 25000L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:platinum"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(5840000D)
                .save(output, id("qmd:nucleosynthesis_chamber/096_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:tungsten"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(10960000, 14800L, 24800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:iridium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4000000D)
                .save(output, id("qmd:nucleosynthesis_chamber/097_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:osmium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(10880000, 15000L, 25000L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:mercury"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(4320000D)
                .save(output, id("qmd:nucleosynthesis_chamber/098_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:iridium"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(9309000, 15000L, 25000L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:mercury"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(3720000D)
                .save(output, id("qmd:nucleosynthesis_chamber/099_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:platinum"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6147000, 14800L, 24800L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:mercury"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2490000D)
                .save(output, id("qmd:nucleosynthesis_chamber/100_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:gold"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6156000, 15000L, 25000L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:mercury"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2410000D)
                .save(output, id("qmd:nucleosynthesis_chamber/101_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:mercury"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(6121000, 15000L, 25000L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:lead"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(2460000D)
                .save(output, id("qmd:nucleosynthesis_chamber/102_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:lead"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(3023000, 14900L, 24900L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:polonium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(878000D)
                .save(output, id("qmd:nucleosynthesis_chamber/103_proton"));

        chamber(NucleosynthesisChamberRecipe::new)
                .fluidInput(sizedFluid(tagFluid("c:bismuth"), 144))
                .fluidInput(SizedChanceFluidIngredient.EMPTY)
                .beamInput(beamInput(1509000, 14900L, 24900L, 1D, id("qmd:proton")))
                .fluidOutput(sizedFluid(tagFluid("c:polonium"), 144))
                .fluidOutput(SizedChanceFluidIngredient.EMPTY)
                .heat(498000D)
                .save(output, id("qmd:nucleosynthesis_chamber/104_proton"));
    }

    private QmdNucleosynthesisChamberRecipes() {}
}
