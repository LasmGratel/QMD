package lach_01298.qmd.datagen.recipe;

import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.recipe.chamber.DecayChamberRecipe;
import net.minecraft.data.recipes.RecipeOutput;

import static lach_01298.qmd.datagen.recipe.QmdRecipeBuilders.*;
import static lach_01298.qmd.datagen.recipe.QmdRecipeIngredients.*;

/** Typed DecayChamber recipe declarations. */
final class QmdDecayChamberRecipes {
    static void add(RecipeOutput output) {
        part0(output);
        part1(output);
    }

    private static void part0(RecipeOutput output) {
        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:neutron")))
                .beamOutput(beam("proton", 1, 0L, 0D))
                .beamOutput(beam("electron_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("electron", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(1490L)
                .save(output, id("qmd:decay_chamber/00_neutron"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antineutron")))
                .beamOutput(beam("positron", 1, 0L, 0D))
                .beamOutput(beam("electron_neutrino", 1, 0L, 0D))
                .beamOutput(beam("antiproton", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(1490L)
                .save(output, id("qmd:decay_chamber/01_antineutron"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:pion_naught")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("photon", 2, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.98D)
                .energyReleased(135000L)
                .save(output, id("qmd:decay_chamber/02_pion_naught"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:pion_plus")))
                .beamOutput(beam("antimuon", 1, 0L, 0D))
                .beamOutput(beam("muon_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.99D)
                .energyReleased(34000L)
                .save(output, id("qmd:decay_chamber/03_pion_plus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:pion_minus")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("muon_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("muon", 1, 0L, 0D))
                .crossSection(0.99D)
                .energyReleased(34000L)
                .save(output, id("qmd:decay_chamber/04_pion_minus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:muon")))
                .beamOutput(beam("electron_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("muon_neutrino", 1, 0L, 0D))
                .beamOutput(beam("electron", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(105000L)
                .save(output, id("qmd:decay_chamber/05_muon"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antimuon")))
                .beamOutput(beam("positron", 1, 0L, 0D))
                .beamOutput(beam("muon_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("electron_neutrino", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(105000L)
                .save(output, id("qmd:decay_chamber/06_antimuon"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:tau")))
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .beamOutput(beam("tau_neutrino", 1, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.25D)
                .energyReleased(1500000L)
                .save(output, id("qmd:decay_chamber/07_tau"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antitau")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(beam("tau_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .crossSection(0.25D)
                .energyReleased(1500000L)
                .save(output, id("qmd:decay_chamber/08_antitau"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:kaon_plus")))
                .beamOutput(beam("antimuon", 1, 0L, 0D))
                .beamOutput(beam("muon_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.63D)
                .energyReleased(388000L)
                .save(output, id("qmd:decay_chamber/09_kaon_plus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:kaon_minus")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("muon_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("muon", 1, 0L, 0D))
                .crossSection(0.63D)
                .energyReleased(388000L)
                .save(output, id("qmd:decay_chamber/10_kaon_minus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:kaon_naught")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.77D)
                .energyReleased(218000L)
                .save(output, id("qmd:decay_chamber/11_kaon_naught"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antikaon_naught")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.77D)
                .energyReleased(218000L)
                .save(output, id("qmd:decay_chamber/12_antikaon_naught"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:w_minus_boson")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.32D)
                .energyReleased(80300000L)
                .save(output, id("qmd:decay_chamber/13_w_minus_boson"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:w_plus_boson")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.32D)
                .energyReleased(80300000L)
                .save(output, id("qmd:decay_chamber/14_w_plus_boson"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:z_boson")))
                .beamOutput(beam("electron_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("electron_antineutrino", 1, 0L, 0D))
                .crossSection(0.068D)
                .energyReleased(91200000L)
                .save(output, id("qmd:decay_chamber/15_z_boson"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:higgs_boson")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("bottom_eta", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.57D)
                .energyReleased(116000000L)
                .save(output, id("qmd:decay_chamber/16_higgs_boson"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:eta")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.33D)
                .energyReleased(133000L)
                .save(output, id("qmd:decay_chamber/17_eta"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:eta_prime")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(beam("eta", 1, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.33D)
                .energyReleased(130000L)
                .save(output, id("qmd:decay_chamber/18_eta_prime"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:charmed_eta")))
                .beamOutput(beam("kaon_naught", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("antikaon_naught", 1, 0L, 0D))
                .crossSection(0.07D)
                .energyReleased(1980000L)
                .save(output, id("qmd:decay_chamber/19_charmed_eta"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:bottom_eta")))
                .beamOutput(beam("antitau", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("tau", 1, 0L, 0D))
                .crossSection(0.08D)
                .energyReleased(5840000L)
                .save(output, id("qmd:decay_chamber/20_bottom_eta"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:glueball")))
                .beamOutput(beam("kaon_plus", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("kaon_minus", 1, 0L, 0D))
                .crossSection(0.33D)
                .energyReleased(742000L)
                .save(output, id("qmd:decay_chamber/21_glueball"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:sigma_plus")))
                .beamOutput(beam("proton", 1, 0L, 0D))
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.52D)
                .energyReleased(117000L)
                .save(output, id("qmd:decay_chamber/22_sigma_plus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antisigma_plus")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_naught", 1, 0L, 0D))
                .beamOutput(beam("antiproton", 1, 0L, 0D))
                .crossSection(0.52D)
                .energyReleased(117000L)
                .save(output, id("qmd:decay_chamber/23_antisigma_plus"));
    }

    private static void part1(RecipeOutput output) {
        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:sigma_minus")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("neutron", 1, 0L, 0D))
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(0.99D)
                .energyReleased(120000L)
                .save(output, id("qmd:decay_chamber/24_sigma_minus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antisigma_minus")))
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .beamOutput(beam("antineutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.99D)
                .energyReleased(120000L)
                .save(output, id("qmd:decay_chamber/25_antisigma_minus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:delta_plus_plus")))
                .beamOutput(beam("proton", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(152000L)
                .save(output, id("qmd:decay_chamber/26_delta_plus_plus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antidelta_plus_plus")))
                .beamOutput(beam("antiproton", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(152000L)
                .save(output, id("qmd:decay_chamber/27_antidelta_plus_plus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:delta_minus")))
                .beamOutput(beam("neutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_minus", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(150000L)
                .save(output, id("qmd:decay_chamber/28_delta_minus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antidelta_minus")))
                .beamOutput(beam("antineutron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("pion_plus", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(150000L)
                .save(output, id("qmd:decay_chamber/29_antidelta_minus"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:electron_neutrino")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("muon_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(0L)
                .save(output, id("qmd:decay_chamber/30_electron_neutrino"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:muon_neutrino")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("tau_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(0L)
                .save(output, id("qmd:decay_chamber/31_muon_neutrino"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:tau_neutrino")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("electron_neutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(0L)
                .save(output, id("qmd:decay_chamber/32_tau_neutrino"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:electron_antineutrino")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("muon_antineutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(0L)
                .save(output, id("qmd:decay_chamber/33_electron_antineutrino"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:muon_antineutrino")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("tau_antineutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(0L)
                .save(output, id("qmd:decay_chamber/34_muon_antineutrino"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:tau_antineutrino")))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("electron_antineutrino", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .crossSection(0.1D)
                .energyReleased(0L)
                .save(output, id("qmd:decay_chamber/35_tau_antineutrino"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:triton")))
                .beamOutput(beam("helion", 1, 0L, 0D))
                .beamOutput(beam("electron_antineutrino", 1, 0L, 0D))
                .beamOutput(beam("electron", 1, 0L, 0D))
                .crossSection(1D)
                .energyReleased(19L)
                .save(output, id("qmd:decay_chamber/explicit_00_triton"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 0L, Long.MAX_VALUE, 0D, id("qmd:antitriton")))
                .beamOutput(beam("positron", 1, 0L, 0D))
                .beamOutput(beam("electron_neutrino", 1, 0L, 0D))
                .beamOutput(beam("antihelion", 1, 0L, 0D))
                .crossSection(1D)
                .energyReleased(19L)
                .save(output, id("qmd:decay_chamber/explicit_01_antitriton"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 1120L, 230000L, 0D, id("qmd:photon")))
                .beamOutput(beam("electron", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("positron", 1, 0L, 0D))
                .crossSection(1.0D)
                .energyReleased(-1020L)
                .save(output, id("qmd:decay_chamber/explicit_02_photon"));

        chamber(DecayChamberRecipe::new)
                .beamInput(beamInput(1, 233000L, Long.MAX_VALUE, 0D, id("qmd:photon")))
                .beamOutput(beam("muon", 1, 0L, 0D))
                .beamOutput(ParticleBeam.EMPTY)
                .beamOutput(beam("antimuon", 1, 0L, 0D))
                .crossSection(0.5D)
                .energyReleased(-211000L)
                .save(output, id("qmd:decay_chamber/explicit_03_photon"));
    }

    private QmdDecayChamberRecipes() {}
}
