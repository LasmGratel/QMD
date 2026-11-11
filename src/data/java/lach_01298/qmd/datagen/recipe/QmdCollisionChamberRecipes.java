package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.chamber.CollisionChamberRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed CollisionChamber recipe declarations. */
final class QmdCollisionChamberRecipes {
    static void add(RecipeOutput output) {
        part0(output);
        part1(output);
    }

    private static void part0(RecipeOutput output) {
        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:neutron")))
                .beamOutput(beam("deuteron", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(2220L)
                .save(output, id("qmd:collision_chamber/00_proton_neutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:antineutron")))
                .beamOutput(beam("antideuteron", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(2220L)
                .save(output, id("qmd:collision_chamber/01_antiproton_antineutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:deuteron")))
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:neutron")))
                .beamOutput(beam("triton", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(6260L)
                .save(output, id("qmd:collision_chamber/02_deuteron_neutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:antideuteron")))
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:antineutron")))
                .beamOutput(beam("antitriton", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(6260L)
                .save(output, id("qmd:collision_chamber/03_antideuteron_antineutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:helion")))
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:neutron")))
                .beamOutput(beam("alpha", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(20600L)
                .save(output, id("qmd:collision_chamber/04_helion_neutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:antihelion")))
                .beamInput(beamInput(1, 0L, 30000L, 5D, id("qmd:antineutron")))
                .beamOutput(beam("antialpha", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(20600L)
                .save(output, id("qmd:collision_chamber/05_antihelion_antineutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:proton")))
                .beamOutput(beam("deuteron", 1, 0L, 0D))
                .beamOutput(beam("positron", 1, 0L, 0D))
                .beamOutput(beam("electron_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.25D)
                .energyReleased(420L)
                .save(output, id("qmd:collision_chamber/06_proton_proton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("antideuteron", 1, 0L, 0D))
                .beamOutput(beam("electron", 1, 0L, 0D))
                .beamOutput(beam("electron_antineutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.25D)
                .energyReleased(420L)
                .save(output, id("qmd:collision_chamber/07_antiproton_antiproton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:deuteron")))
                .beamOutput(beam("helion", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(5490L)
                .save(output, id("qmd:collision_chamber/08_proton_deuteron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antideuteron")))
                .beamOutput(beam("antihelion", 1, 0L, 0D))
                .beamOutput(beam("photon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(5490L)
                .save(output, id("qmd:collision_chamber/09_antiproton_antideuteron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:helion")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:helion")))
                .beamOutput(beam("alpha", 1, 0L, 0D))
                .beamOutput(beam("proton", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(12900L)
                .save(output, id("qmd:collision_chamber/10_helion_helion"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antihelion")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antihelion")))
                .beamOutput(beam("antialpha", 1, 0L, 0D))
                .beamOutput(beam("antiproton", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(12900L)
                .save(output, id("qmd:collision_chamber/11_antihelion_antihelion"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:deuteron")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:triton")))
                .beamOutput(beam("alpha", 1, 0L, 0D))
                .beamOutput(beam("neutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(17600L)
                .save(output, id("qmd:collision_chamber/12_deuteron_triton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antideuteron")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antitriton")))
                .beamOutput(beam("antialpha", 1, 0L, 0D))
                .beamOutput(beam("antineutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(17600L)
                .save(output, id("qmd:collision_chamber/13_antideuteron_antitriton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:triton")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:triton")))
                .beamOutput(beam("alpha", 1, 0L, 0D))
                .beamOutput(beam("neutron", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(11300L)
                .save(output, id("qmd:collision_chamber/14_triton_triton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antitriton")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antitriton")))
                .beamOutput(beam("antialpha", 1, 0L, 0D))
                .beamOutput(beam("antineutron", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(11300L)
                .save(output, id("qmd:collision_chamber/15_antitriton_antitriton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:deuteron")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:helion")))
                .beamOutput(beam("alpha", 1, 0L, 0D))
                .beamOutput(beam("proton", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(18400L)
                .save(output, id("qmd:collision_chamber/16_deuteron_helion"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antideuteron")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antihelion")))
                .beamOutput(beam("antialpha", 1, 0L, 0D))
                .beamOutput(beam("antiproton", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(18400L)
                .save(output, id("qmd:collision_chamber/17_antideuteron_antihelion"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:helion")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:triton")))
                .beamOutput(beam("alpha", 1, 0L, 0D))
                .beamOutput(beam("proton", 1, 0L, 0D))
                .beamOutput(beam("neutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(12100L)
                .save(output, id("qmd:collision_chamber/18_helion_triton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antihelion")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antitriton")))
                .beamOutput(beam("antialpha", 1, 0L, 0D))
                .beamOutput(beam("antiproton", 1, 0L, 0D))
                .beamOutput(beam("antineutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(12100L)
                .save(output, id("qmd:collision_chamber/19_antihelion_antitriton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:deuteron")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:deuteron")))
                .beamOutput(beam("triton", 1, 0L, 0D))
                .beamOutput(beam("proton", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(4030L)
                .save(output, id("qmd:collision_chamber/20_deuteron_deuteron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antideuteron")))
                .beamInput(beamInput(1, 700L, 10000L, 5D, id("qmd:antideuteron")))
                .beamOutput(beam("antitriton", 1, 0L, 0D))
                .beamOutput(beam("antiproton", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.5D)
                .energyReleased(4030L)
                .save(output, id("qmd:collision_chamber/21_antideuteron_antideuteron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("pion_plus", 4, 0L, 0D))
                .beamOutput(beam("pion_naught", 4, 0L, 0D))
                .beamOutput(beam("pion_minus", 4, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(1.0D)
                .energyReleased(220000L)
                .save(output, id("qmd:collision_chamber/22_proton_antiproton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:neutron")))
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:antineutron")))
                .beamOutput(beam("pion_plus", 4, 0L, 0D))
                .beamOutput(beam("pion_naught", 4, 0L, 0D))
                .beamOutput(beam("pion_minus", 4, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(1.0D)
                .energyReleased(223000L)
                .save(output, id("qmd:collision_chamber/23_neutron_antineutron"));
    }

    private static void part1(RecipeOutput output) {
        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:antineutron")))
                .beamOutput(beam("pion_plus", 5, 0L, 0D))
                .beamOutput(beam("pion_naught", 4, 0L, 0D))
                .beamOutput(beam("pion_minus", 4, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(1.0D)
                .energyReleased(81800L)
                .save(output, id("qmd:collision_chamber/24_proton_antineutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 0L, 50000000L, 5D, id("qmd:neutron")))
                .beamOutput(beam("pion_plus", 4, 0L, 0D))
                .beamOutput(beam("pion_naught", 4, 0L, 0D))
                .beamOutput(beam("pion_minus", 5, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(1.0D)
                .energyReleased(81800L)
                .save(output, id("qmd:collision_chamber/25_antiproton_neutron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 232000L, 348000L, 5D, id("qmd:electron")))
                .beamInput(beamInput(1, 232000L, 348000L, 5D, id("qmd:electron")))
                .beamOutput(beam("electron_neutrino", 2, 0L, 0D))
                .beamOutput(beam("muon", 2, 0L, 0D))
                .beamOutput(beam("muon_antineutrino", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.025D)
                .energyReleased(-211000L)
                .save(output, id("qmd:collision_chamber/26_electron_electron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 232000L, 348000L, 5D, id("qmd:positron")))
                .beamInput(beamInput(1, 232000L, 348000L, 5D, id("qmd:positron")))
                .beamOutput(beam("electron_antineutrino", 2, 0L, 0D))
                .beamOutput(beam("antimuon", 2, 0L, 0D))
                .beamOutput(beam("muon_neutrino", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.025D)
                .energyReleased(-211000L)
                .save(output, id("qmd:collision_chamber/27_positron_positron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 232000L, 348000L, 5D, id("qmd:electron")))
                .beamInput(beamInput(1, 232000L, 348000L, 5D, id("qmd:positron")))
                .beamOutput(beam("muon", 1, 0L, 0D))
                .beamOutput(beam("antimuon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(-211000L)
                .save(output, id("qmd:collision_chamber/28_electron_positron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 457000L, 686000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 457000L, 686000L, 5D, id("qmd:proton")))
                .beamOutput(beam("proton", 2, 0L, 0D))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .crossSection(0.1D)
                .energyReleased(-415000L)
                .save(output, id("qmd:collision_chamber/29_proton_proton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 457000L, 686000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 457000L, 686000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("antiproton", 2, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .crossSection(0.1D)
                .energyReleased(-415000L)
                .save(output, id("qmd:collision_chamber/30_antiproton_antiproton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 1300000L, 1950000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 1300000L, 1950000L, 5D, id("qmd:proton")))
                .beamOutput(beam("delta_minus", 1, 0L, 0D))
                .beamOutput(beam("sigma_plus", 1, 0L, 0D))
                .beamOutput(beam("kaon_plus", 1, 0L, 0D))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .crossSection(0.025D)
                .energyReleased(-1180000L)
                .save(output, id("qmd:collision_chamber/31_proton_proton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 1300000L, 1950000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 1300000L, 1950000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("antidelta_minus", 1, 0L, 0D))
                .beamOutput(beam("antisigma_plus", 1, 0L, 0D))
                .beamOutput(beam("kaon_minus", 1, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.025D)
                .energyReleased(-1180000L)
                .save(output, id("qmd:collision_chamber/32_antiproton_antiproton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 3910000L, 5870000L, 5D, id("qmd:electron")))
                .beamInput(beamInput(1, 3910000L, 5870000L, 5D, id("qmd:positron")))
                .beamOutput(beam("tau", 1, 0L, 0D))
                .beamOutput(beam("antitau", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.025D)
                .energyReleased(-3560000L)
                .save(output, id("qmd:collision_chamber/33_electron_positron"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 101000000L, 152000000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 101000000L, 152000000L, 5D, id("qmd:proton")))
                .beamOutput(beam("neutron", 1, 0L, 0D))
                .beamOutput(beam("delta_plus_plus", 1, 0L, 0D))
                .beamOutput(beam("z_boson", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.025D)
                .energyReleased(-91500000L)
                .save(output, id("qmd:collision_chamber/34_proton_proton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 101000000L, 152000000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 101000000L, 152000000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("antineutron", 1, 0L, 0D))
                .beamOutput(beam("antidelta_plus_plus", 1, 0L, 0D))
                .beamOutput(beam("z_boson", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.025D)
                .energyReleased(-91500000L)
                .save(output, id("qmd:collision_chamber/35_antiproton_antiproton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 180000000L, 270000000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 180000000L, 270000000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("glueball", 1, 0L, 0D))
                .beamOutput(beam("w_plus_boson", 1, 0L, 0D))
                .beamOutput(beam("w_minus_boson", 1, 0L, 0D))
                .beamOutput(beam("charmed_eta", 1, 0L, 0D))
                .crossSection(0.025D)
                .energyReleased(-164000000L)
                .save(output, id("qmd:collision_chamber/36_proton_antiproton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 276000000L, 414000000L, 5D, id("qmd:proton")))
                .beamInput(beamInput(1, 276000000L, 414000000L, 5D, id("qmd:proton")))
                .beamOutput(beam("higgs_boson", 2, 0L, 0D))
                .beamOutput(beam("delta_plus_plus", 1, 0L, 0D))
                .beamOutput(beam("kaon_plus", 1, 0L, 0D))
                .beamOutput(beam("sigma_minus", 1, 0L, 0D))
                .crossSection(0.025D)
                .energyReleased(-251000000L)
                .save(output, id("qmd:collision_chamber/37_proton_proton"));

        chamber(CollisionChamberRecipe::new)
                .beamInput(beamInput(1, 276000000L, 414000000L, 5D, id("qmd:antiproton")))
                .beamInput(beamInput(1, 276000000L, 414000000L, 5D, id("qmd:antiproton")))
                .beamOutput(beam("higgs_boson", 2, 0L, 0D))
                .beamOutput(beam("antidelta_plus_plus", 1, 0L, 0D))
                .beamOutput(beam("kaon_minus", 1, 0L, 0D))
                .beamOutput(beam("antisigma_minus", 1, 0L, 0D))
                .crossSection(0.025D)
                .energyReleased(-251000000L)
                .save(output, id("qmd:collision_chamber/38_antiproton_antiproton"));
    }

    private QmdCollisionChamberRecipes() {}
}
