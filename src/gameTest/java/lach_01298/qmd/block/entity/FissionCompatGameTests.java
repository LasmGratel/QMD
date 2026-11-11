package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.fission.FissionShieldEntity;
import com.nred.nuclearcraft.registration.DataMapTypeRegistration;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class FissionCompatGameTests {
    @GameTest(template = "empty", batch = "qmd.fission")
    public static void nativeShieldHasItsOwnValidPersistentType(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        helper.getLevel().setBlock(pos, QMDBlocks.HAFNIUM_SHIELD.get().defaultBlockState(), 3);
        BlockEntity entity = helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(entity instanceof HafniumShieldBlockEntity && entity instanceof FissionShieldEntity,
                "QMD shielding must run the native Neohaul fission component");
        helper.assertTrue(entity.getType() == QMDBlockEntities.HAFNIUM_SHIELD.get() && entity.getType().isValid(entity.getBlockState()),
                "The registered block entity type must accept QMD's actual block");
        var restored = BlockEntity.loadStatic(pos, entity.getBlockState(), entity.saveWithFullMetadata(helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
        helper.assertTrue(restored instanceof HafniumShieldBlockEntity shield && shield.heatPerFlux == 15 && shield.efficiency == 1,
                "Native shield behavior and QMD type survive save/load");
        var reflector = BuiltInRegistries.BLOCK.wrapAsHolder(QMDBlocks.TUNGSTEN_CARBIDE_REFLECTOR.get()).getData(DataMapTypeRegistration.FISSION_REFLECTOR_DATA);
        helper.assertTrue(reflector != null && reflector.efficiency() == 0.75 && reflector.reflectivity() == 0.75,
                "Native fission tracing must receive the QMD reflector data map");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.fission", timeoutTicks = 100)
    public static void strontiumRtgProducesThroughNativeController(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        helper.getLevel().setBlock(pos, QMDBlocks.STRONTIUM_RTG.get().defaultBlockState(), 3);
        helper.succeedWhen(() -> {
            var entity = (StrontiumRtgBlockEntity) helper.getLevel().getBlockEntity(pos);
            var capability = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
            helper.assertTrue(entity.getType() == QMDBlockEntities.STRONTIUM_RTG.get() && entity.getType().isValid(entity.getBlockState()), "RTG type accepts QMD block");
            helper.assertTrue(capability != null && capability.canExtract() && capability.getEnergyStored() > 0,
                    "Native RTG world lifecycle must generate real extractable energy");
        });
    }

    @GameTest(template = "empty", batch = "qmd.fission")
    public static void nativeFuelAndProductionRecipesLoadAllFourForms(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        for (String form : new String[]{"tr", "ox", "ni", "za"}) {
            String family = form.equals("tr") ? "pebble_fission" : "solid_fission";
            var recipe = manager.byKey(net.minecraft.resources.ResourceLocation.parse("qmd:" + family + "/depleted_mix_291_" + form));
            helper.assertTrue(recipe.isPresent() && recipe.get().value() instanceof com.nred.nuclearcraft.recipe.fission.ItemFissionRecipe,
                    "MIX-291 " + form + " must load as an actual native fission recipe");
            var fuel = (com.nred.nuclearcraft.recipe.fission.ItemFissionRecipe) recipe.orElseThrow().value();
            helper.assertTrue(fuel.getFissionFuelEfficiencyRaw() == 5 && fuel.getFissionFuelSelfPriming(), "Legacy fuel physics must survive migration");
            helper.assertTrue(Math.abs(fuel.getFissionFuelRadiationRaw() - lach_01298.qmd.registry.QMDItems.MIX_RADIATION) < 1E-15,
                    "Processing radiation retains the legacy configured MIX-291 value");
            helper.assertTrue(manager.byKey(net.minecraft.resources.ResourceLocation.parse("qmd:fuel_reprocessor/mix_291_" + form)).isPresent(),
                    "Every depleted fuel form has native reprocessing");
            var reprocessing = (com.nred.nuclearcraft.recipe.ProcessorRecipe) manager.byKey(
                    net.minecraft.resources.ResourceLocation.parse("qmd:fuel_reprocessor/mix_291_" + form)).orElseThrow().value();
            helper.assertTrue(reprocessing.getItemProducts().size() == (form.equals("tr") ? 8 : form.equals("za") ? 7 : 6),
                    "Native reprocessing retains every isotope, waste and casing-material output");
            assertProduct(helper, reprocessing, "ruthenium_106_dust", 2, 30);
            assertProduct(helper, reprocessing, "europium_155_dust", 2, 20);
            if (form.equals("tr")) {
                assertProduct(helper, reprocessing, "graphite_dust", 3, 100);
                assertProduct(helper, reprocessing, "silicon_carbide_alloy", 1, 100);
            } else if (form.equals("za")) assertProduct(helper, reprocessing, "zirconium_dust", 1, 100);
        }
        for (String id : new String[]{"assembler/mix_291", "assembler/mix_291_tr", "fluid_infuser/mix_291_ox", "fluid_infuser/mix_291_ni",
                "crafting/cell_empty", "crafting/neutron_shield_hafnium", "crafting/reflector_tungsten_carbide", "crafting/rtg_strontium"}) {
            helper.assertTrue(manager.byKey(net.minecraft.resources.ResourceLocation.parse("qmd:" + id)).isPresent(), "Required progression recipe is missing: " + id);
        }
        lach_01298.qmd.registry.QMDItems.materials().stream()
                .filter(material -> material.id().startsWith("waste_fission_") || material.id().startsWith("waste_spallation_"))
                .forEach(material -> {
                    var source = com.nred.nuclearcraft.radiation.RadiationHelper.getRadiationSource(
                            new net.minecraft.world.item.ItemStack(lach_01298.qmd.registry.QMDItems.get(material.id()).get()));
                    double expected = com.nred.nuclearcraft.radiation.RadSources.CAESIUM_137
                            * (material.id().startsWith("waste_spallation_") ? 0.5 : 1);
                    helper.assertTrue(source != null && source.getRadiationLevel() == expected,
                            "Native item radiation must retain every legacy waste hazard: " + material.id());
                });
        helper.succeed();
    }

    private static void assertProduct(GameTestHelper helper, com.nred.nuclearcraft.recipe.ProcessorRecipe recipe,
                                      String path, int count, int chance) {
        var item = BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("nuclearcraftneohaul", path));
        helper.assertTrue(recipe.getItemProducts().stream().anyMatch(product -> product.count() == count
                        && product.chancePercent() == chance && product.minStackSize() == 0
                        && product.ingredient().test(new net.minecraft.world.item.ItemStack(item))),
                "Native reprocessing must retain " + count + "x " + path + " at " + chance + "% per unit");
    }

    @GameTest(template = "empty", batch = "qmd.fission")
    public static void nativeProcessorCodecsRetainEveryDeclaredChannel(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        var resources = helper.getLevel().getServer().getResourceManager().listResources("recipe", id ->
                id.getNamespace().equals("qmd") && id.getPath().endsWith(".json"));
        int checked = 0;
        for (var entry : resources.entrySet()) {
            String path = entry.getKey().getPath();
            var id = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("qmd", path.substring(7, path.length() - 5));
            var holder = manager.byKey(id);
            if (holder.isEmpty() || !(holder.get().value() instanceof com.nred.nuclearcraft.recipe.ProcessorRecipe recipe)) continue;
            try (var reader = entry.getValue().openAsReader()) {
                var json = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
                // QMD's leacher intentionally pads outputs to three machine slots; this checks the native lenient codec.
                if (!json.get("type").getAsString().startsWith("nuclearcraftneohaul:")) continue;
                String[] fields = {"itemIngredients", "itemProducts", "fluidIngredients", "fluidProducts"};
                int[] actual = {recipe.getItemIngredients().size(), recipe.getItemProducts().size(), recipe.getFluidIngredients().size(), recipe.getFluidProducts().size()};
                for (int i = 0; i < fields.length; i++) {
                    int declared = json.has(fields[i]) ? json.getAsJsonArray(fields[i]).size() : 0;
                    helper.assertTrue(actual[i] == declared, "Native recipe codec silently discarded " + fields[i] + " in " + id);
                }
                checked++;
            } catch (java.io.IOException ex) { throw new IllegalStateException("Cannot read " + id, ex); }
        }
        helper.assertTrue(checked >= 30, "Check the complete migrated native production recipe set");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.fission")
    public static void loadedParticleRecipesResolveEveryDeclaredMaterialChannel(GameTestHelper helper) {
        int checked = 0;
        for (var holder : helper.getLevel().getRecipeManager().getRecipes()) {
            if (!holder.id().getNamespace().equals("qmd")) continue;
            String id = holder.id().toString();
            if (holder.value() instanceof lach_01298.qmd.recipe.chamber.AbstractChamberRecipe recipe) {
                var kind = recipe.kind();
                helper.assertTrue(recipe.getItemIngredients().size() == kind.itemInputs
                                && recipe.getFluidIngredients().size() == kind.fluidInputs
                                && recipe.particleInputs().size() == kind.particleInputs
                                && recipe.getItemProducts().size() == kind.itemOutputs
                                && recipe.getFluidProducts().size() == kind.fluidOutputs
                                && recipe.particleOutputs().size() == kind.particleOutputs,
                        "Loaded chamber recipe retains all fixed channels: " + id);
                assertResolvedMaterials(helper, id + " input", recipe.getItemIngredients(), recipe.getFluidIngredients());
                assertResolvedMaterials(helper, id + " product", recipe.getItemProducts(), recipe.getFluidProducts());
                checked++;
            } else if (holder.value() instanceof lach_01298.qmd.recipe.accelerator.AbstractAcceleratorRecipe recipe) {
                if (recipe.item() != net.minecraft.world.item.crafting.Ingredient.EMPTY)
                    helper.assertTrue(java.util.Arrays.stream(recipe.item().getItems()).anyMatch(stack -> !stack.isEmpty()),
                            "Loaded accelerator item input resolves a real material: " + id);
                assertResolvedMaterials(helper, id + " product", recipe.itemProducts(), recipe.fluidProducts());
                helper.assertTrue(recipe.itemProducts().size() <= 4 && recipe.fluidProducts().size() <= 4,
                        "Loaded accelerator products fit every real output channel: " + id);
                checked++;
            }
        }
        helper.assertTrue(checked >= 100, "Check the loaded reaction and accelerator catalogue, including conditional recipes that actually loaded");
        helper.succeed();
    }

    private static void assertResolvedMaterials(GameTestHelper helper, String id,
                                                java.util.List<com.nred.nuclearcraft.recipe.SizedChanceItemIngredient> items,
                                                java.util.List<com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient> fluids) {
        for (int slot = 0; slot < items.size(); slot++) {
            var ingredient = items.get(slot);
            if (ingredient.count() > 0)
                helper.assertTrue(java.util.Arrays.stream(ingredient.getItemsRaw()).anyMatch(stack -> !stack.isEmpty()),
                        "Nonempty declared item channel resolves a real stack: " + id + " slot " + slot);
        }
        for (int slot = 0; slot < fluids.size(); slot++) {
            var ingredient = fluids.get(slot);
            if (ingredient.amount() > 0)
                helper.assertTrue(java.util.Arrays.stream(ingredient.getFluidsRaw()).anyMatch(stack -> !stack.isEmpty()),
                        "Nonempty declared fluid channel resolves a real stack: " + id + " slot " + slot);
        }
    }
}
