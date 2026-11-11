package lach_01298.qmd.item.equipment;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/** Legacy tungsten-carbide material: tier 3, 3122 uses, speed 8, damage bonus 3, enchantability 12. */
public enum TungstenCarbideTier implements Tier {

    INSTANCE;

    private static final TagKey<Item> REPAIR_MATERIAL = TagKey
        .create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/tungsten_carbide"));

    @Override
    public int getUses() {
        return 3122;
    }

    @Override
    public float getSpeed() {
        return 8.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 3.0F;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
    }

    @Override
    public int getEnchantmentValue() {
        return 12;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(REPAIR_MATERIAL);
    }
}
