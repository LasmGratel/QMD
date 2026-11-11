package lach_01298.qmd.registry;

import lach_01298.qmd.QuantumMinecraftDynamics;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

/** Armor materials shared by QMD equipment. */
public final class QMDArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister
        .create(BuiltInRegistries.ARMOR_MATERIAL, QuantumMinecraftDynamics.MODID);

    public static final Holder<ArmorMaterial> HEV = ARMOR_MATERIALS
        .register("hev", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 1);
            map.put(ArmorItem.Type.LEGGINGS, 3);
            map.put(ArmorItem.Type.CHESTPLATE, 4);
            map.put(ArmorItem.Type.HELMET, 1);
            map.put(ArmorItem.Type.BODY, 4);
        }),
            0,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.EMPTY,
            List.of(
                new ArmorMaterial.Layer(id("hev"), "", true),
                new ArmorMaterial.Layer(id("hev"), "_overlay", false)),
            0.0F,
            0.0F));

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(QuantumMinecraftDynamics.MODID, path);
    }

    private QMDArmorMaterials() {}
}
