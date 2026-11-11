package lach_01298.qmd.datagen.client;

import lach_01298.qmd.item.player.LuminousPaintItem;
import lach_01298.qmd.item.weapon.ParticleWeaponItem;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDFluids;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;

/** Item models follow the live registries and the material catalogue's legacy texture mapping. */
public final class QMDItemModelProvider extends ItemModelProvider {
    public QMDItemModelProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, "qmd", existing);
    }

    @Override
    protected void registerModels() {
        Map<String, QMDItems.Material> materials = new HashMap<>();
        QMDItems.materials().forEach(material -> materials.put(material.id(), material));
        for (var entry : BuiltInRegistries.ITEM.entrySet()) {
            var id = entry.getKey().location();
            if (!id.getNamespace().equals("qmd")) continue;
            String name = id.getPath();
            if (entry.getValue() instanceof BucketItem) continue;
            // Weapons retain their authored geometry and display transforms.
            if (entry.getValue() instanceof ParticleWeaponItem) continue;
            // Equipment models retain authored overlay layers and tool display transforms.
            if (QMDItems.EQUIPMENT.stream()
                    .anyMatch(item -> item.getId().equals(id))) continue;
            if (entry.getValue() instanceof LuminousPaintItem) {
                singleTexture(name, mcLoc("item/generated"), "layer0", modLoc("item/" + name));
            } else if (entry.getValue() instanceof BlockItem) {
                String model = name;
                if (QMDBlocks.ACCELERATOR_PARTS.containsKey(name) && QMDBlockStateProvider.acceleratorSwitch(QMDBlocks.ACCELERATOR_PARTS.get(name).role)) model += "_off_in";
                else model += switch (name) {
                    case "irradiator", "neutron_shield_hafnium" -> "_off";
                    case "liquefier_fluid_port" -> "_in";
                    default -> "";
                };
                withExistingParent(name, modLoc("block/" + model));
            } else if (materials.containsKey(name)) {
                var material = materials.get(name);
                String group = material.legacyItem();
                String parent = "minecraft:item/generated";
                String key = "layer0";
                String texture = switch (group) {
                    case "dust", "dust2" -> "materials/dust/" + material.variant();
                    case "ingot", "ingot2" -> "materials/ingot/" + material.variant();
                    case "ingot_alloy" -> "materials/alloy/" + name;
                    case "chemical_dust" -> "materials/dust/chemical/" + material.variant();
                    case "waste_spallation", "waste_spallation2" -> "waste/spallation/" + material.variant();
                    case "waste_fission" -> "waste/fission/" + material.variant();
                    default -> group + "/" + material.variant();
                };
                if (name.equals("part_empty_cooler") || name.equals("part_detector_casing") || name.equals("part_wire_chamber_casing")) {
                    parent = "minecraft:block/cube_all";
                    key = "all";
                    if (name.equals("part_detector_casing")) texture = "part/empty_detector";
                }
                singleTexture(name, mcLoc(parent), key, modLoc("item/" + texture));
            } else singleTexture(name, mcLoc("item/generated"), "layer0", modLoc("item/" + name));
        }
        // Retain the shared depleted-fuel icon, even though only its physical fuel forms are items.
        singleTexture("depleted_mix_291", mcLoc("item/generated"), "layer0", modLoc("item/depleted_mix_291"));
        QMDFluids.entries().forEach(fluid -> {
            String id = fluid.still.getId().getPath();
            withExistingParent(id + "_bucket", ResourceLocation.parse("neoforge:item/bucket_drip"))
                    .customLoader(DynamicFluidContainerModelBuilder::begin)
                    .fluid(fluid.still.get()).flipGas(fluid.gaseous).applyTint(true);
        });
    }
}
