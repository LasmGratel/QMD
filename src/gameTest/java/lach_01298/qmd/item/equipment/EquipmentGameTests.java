package lach_01298.qmd.item.equipment;

import com.nred.nuclearcraft.capability.radiation.resistance.RadiationResistanceItem;
import com.nred.nuclearcraft.registration.DamageTypeRegistration;
import com.nred.nuclearcraft.registration.DataComponentRegistration;
import lach_01298.qmd.registry.QMDItems;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class EquipmentGameTests {
    private static final String BATCH = "qmd.equipment";

    @GameTest(template = "empty", batch = BATCH)
    public static void carbideToolHarvestAndHevEquipmentSlotsAreFunctional(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack pickaxe = new ItemStack(QMDItems.PICKAXE_TUNGSTEN_CARBIDE.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, pickaxe);
        BlockPos relative = new BlockPos(2, 2, 2);
        BlockPos pos = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.DIAMOND_ORE);
        BlockState ore = helper.getLevel().getBlockState(pos);
        helper.assertTrue(pickaxe.getMaxDamage() == 3122 && pickaxe.isCorrectToolForDrops(ore)
                        && ore.canHarvestBlock(helper.getLevel(), pos, player),
                "Tungsten-carbide pickaxe must retain tier-3 harvesting and 3122 durability");
        pickaxe.mineBlock(helper.getLevel(), ore, pos, player);
        helper.getLevel().destroyBlock(pos, false, player);
        helper.assertTrue(helper.getLevel().getBlockState(pos).isAir() && pickaxe.getDamageValue() == 1,
                "A headless harvest must remove the block and consume one tool durability");

        equip(helper, player, QMDItems.HELM_HEV.get(), EquipmentSlot.HEAD);
        equip(helper, player, QMDItems.CHEST_HEV.get(), EquipmentSlot.CHEST);
        equip(helper, player, QMDItems.LEGS_HEV.get(), EquipmentSlot.LEGS);
        equip(helper, player, QMDItems.BOOTS_HEV.get(), EquipmentSlot.FEET);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = BATCH)
    public static void drillModesMineTheirPlaneAndConsumeEnergy(GameTestHelper helper) {
        ItemStack basic = charged(new ItemStack(QMDItems.DRILL_BASIC.get()), 2_000);
        DrillItem drill = (DrillItem) basic.getItem();
        helper.assertTrue(drill.additionalBlocks(BlockPos.ZERO, Direction.UP).size() == 8
                        && ((DrillItem) QMDItems.DRILL_ADVANCED.get()).additionalBlocks(BlockPos.ZERO, Direction.NORTH).size() == 24,
                "Basic and advanced fixed modes must expose 3x3 and 5x5 face-oriented planes");

        ServerPlayer serverPlayer = net.neoforged.neoforge.common.util.FakePlayerFactory.get(helper.getLevel(),
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "DrillTest"));
        serverPlayer.setGameMode(GameType.SURVIVAL);
        serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, basic);
        BlockPos origin = helper.absolutePos(new BlockPos(2, 3, 2));
        helper.getLevel().setBlock(origin, Blocks.STONE.defaultBlockState(), 3);
        for (BlockPos pos : drill.additionalBlocks(origin, Direction.UP)) {
            helper.getLevel().setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
        }
        EquipmentEvents.mineAdditionalBlocks(serverPlayer, origin, Direction.UP);
        for (BlockPos pos : drill.additionalBlocks(origin, Direction.UP)) {
            helper.assertTrue(helper.getLevel().getBlockState(pos).isAir(),
                    "The server must remove every harvestable block in the basic drill plane");
        }
        helper.assertTrue(energy(basic) == 1_200,
                "The eight additional blocks must consume 100 FE each");
        helper.assertTrue(helper.getLevel().getBlockState(origin).is(Blocks.STONE),
                "Area mining must leave the primary block to the normal break operation");

        ItemStack single = charged(new ItemStack(QMDItems.DRILL_BASIC.get()), 2_000);
        Player survival = helper.makeMockPlayer(GameType.SURVIVAL);
        survival.setItemInHand(InteractionHand.MAIN_HAND, single);
        drill.mineBlock(single, helper.getLevel(), Blocks.STONE.defaultBlockState(), origin, survival);
        helper.assertTrue(energy(single) == 1_900 && !single.has(DataComponents.MAX_DAMAGE),
                "A survival drill break must consume exactly 100 FE and never use item durability");
        serverPlayer.connection.disconnect(Component.literal("QMD equipment GameTest complete"));
        helper.succeed();
    }

    @GameTest(template = "empty", batch = BATCH)
    public static void hevChargeControlsProtectionAndArmorBonuses(GameTestHelper helper) {
        ItemStack helmet = new ItemStack(QMDItems.HELM_HEV.get());
        ItemStack chest = new ItemStack(QMDItems.CHEST_HEV.get());
        ItemStack legs = new ItemStack(QMDItems.LEGS_HEV.get());
        ItemStack boots = new ItemStack(QMDItems.BOOTS_HEV.get());
        helper.assertTrue(attribute(helmet, Attributes.ARMOR, EquipmentSlot.HEAD) == 1.0D
                        && attribute(chest, Attributes.ARMOR, EquipmentSlot.CHEST) == 4.0D
                        && attribute(legs, Attributes.ARMOR, EquipmentSlot.LEGS) == 3.0D
                        && attribute(boots, Attributes.ARMOR, EquipmentSlot.FEET) == 1.0D,
                "Empty HEV pieces must total the legacy nine armor points");

        for (ItemStack stack : List.of(helmet, chest, legs, boots)) charge(stack, 5_000);
        helper.assertTrue(attribute(helmet, Attributes.ARMOR, EquipmentSlot.HEAD) == 4.0D
                        && attribute(chest, Attributes.ARMOR, EquipmentSlot.CHEST) == 9.0D
                        && attribute(legs, Attributes.ARMOR, EquipmentSlot.LEGS) == 7.0D
                        && attribute(boots, Attributes.ARMOR, EquipmentSlot.FEET) == 4.0D,
                "Charged HEV pieces must total the legacy twenty-four armor points");
        helper.assertTrue(attribute(helmet, Attributes.ARMOR_TOUGHNESS, EquipmentSlot.HEAD) == 4.0D
                        && attribute(legs, Attributes.MOVEMENT_SPEED, EquipmentSlot.LEGS) == 0.25D
                        && attribute(boots, Attributes.STEP_HEIGHT, EquipmentSlot.FEET) == 0.402D,
                "Charged HEV toughness, movement speed and step assist must be stack-sensitive attributes");

        double radiation = 0.0D;
        double special = 0.0D;
        for (ItemStack stack : List.of(helmet, chest, legs, boots)) {
            RadiationResistanceItem resistance = stack.get(DataComponentRegistration.RADIATION_RESISTANCE_ITEM.get());
            helper.assertTrue(resistance != null, "Every HEV piece must carry Neohaul radiation resistance");
            radiation += resistance.getBaseRadResistance();
            special += ((HevArmorItem) stack.getItem()).specialProtection();
        }
        helper.assertTrue(radiation == 90.0D && special == 1.0D,
                "Full HEV must retain 90 radiation resistance and 100% special-damage coverage");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.HEAD, helmet);
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        player.setItemSlot(EquipmentSlot.LEGS, legs);
        player.setItemSlot(EquipmentSlot.FEET, boots);
        var damageType = helper.getLevel().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypeRegistration.ACID_BURN);
        LivingIncomingDamageEvent incoming = new LivingIncomingDamageEvent(player,
                new DamageContainer(new net.minecraft.world.damagesource.DamageSource(damageType), 10.0F));
        EquipmentEvents.onIncomingDamage(incoming);
        helper.assertTrue(incoming.getAmount() == 0.0F,
                "A complete HEV suit must cancel its legacy acid/radiation damage classes");

        EquipmentEvents.onLivingDamage(new LivingDamageEvent.Pre(player,
                new DamageContainer(player.damageSources().generic(), 2.5F)));
        helper.assertTrue(energy(helmet) == 4_750 && energy(chest) == 4_750
                        && energy(legs) == 4_750 && energy(boots) == 4_750,
                "Each charged HEV piece must consume 100 FE per original damage point");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = BATCH)
    public static void hevBonusHandlerRetainsLegacyCosts(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack helmet = charged(new ItemStack(QMDItems.HELM_HEV.get()), 2_000);
        ItemStack chest = charged(new ItemStack(QMDItems.CHEST_HEV.get()), 2_000);
        ItemStack legs = charged(new ItemStack(QMDItems.LEGS_HEV.get()), 2_000);
        ItemStack boots = charged(new ItemStack(QMDItems.BOOTS_HEV.get()), 2_000);
        player.setItemSlot(EquipmentSlot.HEAD, helmet);
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        player.setItemSlot(EquipmentSlot.LEGS, legs);
        player.setItemSlot(EquipmentSlot.FEET, boots);
        player.setSprinting(true);
        player.setYRot(0.0F);
        player.setDeltaMovement(0.0D, 0.0D, 0.0D);
        EquipmentEvents.onLivingJump(new LivingEvent.LivingJumpEvent(player));
        helper.assertTrue(energy(legs) == 1_900 && energy(chest) == 1_750
                        && player.getDeltaMovement().y == 0.2D && player.getDeltaMovement().z == 1.0D,
                "HEV jump and sprint-jump must consume 100/250 FE and apply their legacy velocity");

        LivingFallEvent fall = new LivingFallEvent(player, 10.0F, 1.0F);
        EquipmentEvents.onLivingFall(fall);
        helper.assertTrue(energy(boots) == 1_900 && fall.getDistance() == 6.0F && fall.getDamageMultiplier() == 0.5F,
                "HEV boots must consume 100 FE, subtract four fall blocks and halve damage");

        MobEffectEvent.Applicable poison = new MobEffectEvent.Applicable(player,
                new MobEffectInstance(MobEffects.POISON, 200), null);
        EquipmentEvents.onEffectApplicable(poison);
        helper.assertTrue(poison.getResult() == MobEffectEvent.Applicable.Result.DO_NOT_APPLY && energy(helmet) == 1_000,
                "HEV helmet must spend 1000 FE to reject poison");
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
        EquipmentEvents.onPlayerTick(new PlayerTickEvent.Post(player));
        MobEffectInstance wither = player.getEffect(MobEffects.WITHER);
        helper.assertTrue(energy(helmet) == 0 && wither != null
                        && wither.getAmplifier() == 0 && wither.getDuration() == 80,
                "HEV helmet must spend 1000 FE to reduce strong/long wither to Wither I for 80 ticks");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = BATCH)
    public static void armorDyeRecipePreservesEnergyNameAndCount(GameTestHelper helper) {
        ItemStack helmet = charged(new ItemStack(QMDItems.HELM_HEV.get(), 3), 12_345);
        helmet.set(DataComponents.CUSTOM_NAME, Component.literal("Rescue suit"));
        int originalColor = DyedItemColor.getOrDefault(helmet, HevArmorItem.DEFAULT_COLOR);
        var loaded = helper.getLevel().getRecipeManager().byKey(
                ResourceLocation.fromNamespaceAndPath("qmd", "armour_dye"));
        helper.assertTrue(loaded.isPresent(), "The generated qmd:armour_dye recipe must be loaded");
        var value = loaded.orElseThrow().value();
        helper.assertTrue(value instanceof HevArmorDyeRecipe,
                "qmd:armour_dye must decode through the registered custom serializer");
        HevArmorDyeRecipe recipe = (HevArmorDyeRecipe) value;
        CraftingInput input = CraftingInput.of(2, 1,
                List.of(helmet, new ItemStack(DyeItem.byColor(DyeColor.RED), 64)));
        helper.assertTrue(recipe.matches(input, helper.getLevel()), "One HEV item plus a dye must match qmd:armour_dye");
        ItemStack output = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(output.is(QMDItems.HELM_HEV.get()) && output.getCount() == 1
                        && energy(output) == 12_345
                        && Component.literal("Rescue suit").equals(output.get(DataComponents.CUSTOM_NAME))
                        && DyedItemColor.getOrDefault(output, HevArmorItem.DEFAULT_COLOR) != originalColor,
                "Dye output must be one recolored HEV item retaining its name and energy components");
        helper.assertTrue(recipe.getSerializer() == QMDRecipes.ARMOUR_DYE_SERIALIZER.get(),
                "The dynamic output must use the qmd:armour_dye serializer");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = BATCH)
    public static void equipmentComponentsSurviveItemPersistence(GameTestHelper helper) {
        ItemStack drill = charged(new ItemStack(QMDItems.DRILL_ADVANCED.get()), 123_456);
        drill.set(DataComponents.CUSTOM_NAME, Component.literal("Persistent drill"));
        ItemStack restored = ItemStack.parseOptional(helper.getLevel().registryAccess(),
                (CompoundTag) drill.save(helper.getLevel().registryAccess()));
        helper.assertTrue(restored.is(QMDItems.DRILL_ADVANCED.get()) && energy(restored) == 123_456
                        && Component.literal("Persistent drill").equals(restored.get(DataComponents.CUSTOM_NAME)),
                "Drill identity, FE and ordinary components must survive registry-aware serialization");
        helper.succeed();
    }

    private static ItemStack charged(ItemStack stack, int amount) {
        charge(stack, amount);
        return stack;
    }

    private static void equip(GameTestHelper helper, Player player, Item item, EquipmentSlot slot) {
        ItemStack stack = new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(player.getItemBySlot(slot).is(item) && player.getMainHandItem().isEmpty(),
                item + " must equip through the normal armor interaction in " + slot.getName());
    }

    private static void charge(ItemStack stack, int amount) {
        var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null || storage.receiveEnergy(amount, false) != amount) {
            throw new IllegalStateException("Equipment energy capability did not accept " + amount + " FE");
        }
    }

    private static int energy(ItemStack stack) {
        var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return storage == null ? -1 : storage.getEnergyStored();
    }

    private static double attribute(ItemStack stack, net.minecraft.core.Holder<Attribute> attribute, EquipmentSlot slot) {
        double value = 0.0D;
        for (var entry : stack.getAttributeModifiers().modifiers()) {
            if (!entry.attribute().equals(attribute) || !entry.slot().test(slot)) continue;
            AttributeModifier modifier = entry.modifier();
            if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE
                    || modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    || modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                value += modifier.amount();
            }
        }
        return value;
    }

    private EquipmentGameTests() {}
}
