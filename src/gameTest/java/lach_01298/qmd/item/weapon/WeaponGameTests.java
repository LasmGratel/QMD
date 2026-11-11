package lach_01298.qmd.item.weapon;

import lach_01298.qmd.entity.AntimatterProjectileEntity;
import lach_01298.qmd.entity.GammaFlashEntity;
import lach_01298.qmd.entity.GluonBeamEntity;
import lach_01298.qmd.entity.LeptonBeamEntity;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.registry.QMDEntities;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class WeaponGameTests {
    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void leptonShootsThroughBlocksAndExpires(GameTestHelper helper) {
        ServerPlayer player = player(helper, QMDItems.LEPTON_CANNON.get().getDefaultInstance());
        ItemStack cell = cell("positronium", 500);
        player.getInventory().setItem(1, cell);
        helper.setBlock(new BlockPos(2, 2, 2), Blocks.STONE);
        var target = EntityType.COW.create(helper.getLevel());
        Vec3 location = Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(2, 2, 3)));
        target.setPos(location); target.setNoAi(true);
        target.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(20);
        helper.getLevel().addFreshEntity(target);
        float health = target.getHealth();
        var result = QMDItems.LEPTON_CANNON.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        helper.assertTrue(result.getResult().consumesAction() && target.getHealth() == health - 7,
                "Positronium must deal seven armor-bypassing damage through a solid block");
        helper.assertTrue(target.getLastDamageSource().getDirectEntity() instanceof LeptonBeamEntity
                        && target.getLastDamageSource().getEntity() == player
                        && !target.getLastDamageSource().is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE),
                "Legacy weapon damage retains its beam and shooter attribution");
        helper.assertTrue(player.getInventory().getItem(1).is(QMDItems.EMPTY_CELL.get())
                        && player.getCooldowns().isOnCooldown(QMDItems.LEPTON_CANNON.get()),
                "A shot consumes 500 units, returns an empty cell and applies cooldown");
        var beams = helper.getLevel().getEntitiesOfClass(LeptonBeamEntity.class,
                new AABB(player.blockPosition()).inflate(3));
        helper.assertTrue(beams.size() == 1 && beams.getFirst().length() < 3,
                "The visible lepton beam must end at the actual target");
        var beam = beams.getFirst();
        for (int tick = 0; tick < 4; tick++) beam.tick();
        helper.assertTrue(beam.isRemoved(), "Lepton beam expires after four ticks");
        target.discard();
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void firstCellAndCreativeAmmoRulesArePreserved(GameTestHelper helper) {
        ServerPlayer player = player(helper, QMDItems.LEPTON_CANNON.get().getDefaultInstance());
        player.getInventory().setItem(1, cell("muonium", 499));
        player.getInventory().setItem(2, cell("tauonium", 1000));
        var gun = QMDItems.LEPTON_CANNON.get();
        helper.assertTrue(!gun.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult().consumesAction()
                        && ParticleReservoirItem.stored(player.getInventory().getItem(2)) == 1000,
                "An insufficient first matching cell must prevent using a later cell");
        player.setGameMode(GameType.CREATIVE);
        helper.assertTrue(!gun.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult().consumesAction(),
                "Creative firing still requires sufficient matching ammo");
        player.getInventory().setItem(1, cell("muonium", 500));
        helper.assertTrue(gun.use(helper.getLevel(), player, InteractionHand.MAIN_HAND).getResult().consumesAction()
                        && ParticleReservoirItem.stored(player.getInventory().getItem(1)) == 500,
                "Creative firing must retain its ammunition");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void gluonRespectsBreakCancellationAndStopsWhenEmpty(GameTestHelper helper) {
        ServerPlayer player = player(helper, QMDItems.GLUON_GUN.get().getDefaultInstance());
        player.getInventory().setItem(1, cell("glueballs", 20));
        BlockPos relative = new BlockPos(2, 2, 3);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.DIAMOND_ORE);
        var gun = QMDItems.GLUON_GUN.get();
        gun.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        var beam = new GluonBeamEntity(QMDEntities.GLUON_BEAM.get(), helper.getLevel());
        beam.configure(player, InteractionHand.MAIN_HAND, 40, 0xFFFFFF);
        java.util.function.Consumer<BlockEvent.BreakEvent> cancel = event -> {
            if (event.getPos().equals(absolute)) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(cancel);
        try { gun.fire(player, InteractionHand.MAIN_HAND, beam); }
        finally { NeoForge.EVENT_BUS.unregister(cancel); }
        helper.assertTrue(helper.getLevel().getBlockState(absolute).is(Blocks.DIAMOND_ORE)
                        && ParticleReservoirItem.stored(player.getInventory().getItem(1)) == 10,
                "Protection cancellation preserves the block while an active beam still consumes ammo");
        gun.fire(player, InteractionHand.MAIN_HAND, beam);
        helper.assertTrue(helper.getLevel().getBlockState(absolute).isAir()
                        && player.getInventory().getItem(1).is(QMDItems.EMPTY_CELL.get()),
                "Gluon mining ignores harvest level and consumes ten units per tick");
        gun.fire(player, InteractionHand.MAIN_HAND, beam);
        helper.assertTrue(!player.isUsingItem() && beam.isRemoved(),
                "Exhaustion must stop continuous use and discard the beam");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void gluonSilkModePersistsAndDropsTheBlock(GameTestHelper helper) {
        ItemStack stack = QMDItems.GLUON_GUN.get().getDefaultInstance();
        ServerPlayer player = player(helper, stack);
        player.setShiftKeyDown(true);
        QMDItems.GLUON_GUN.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        ItemStack restored = ItemStack.parseOptional(helper.getLevel().registryAccess(),
                (CompoundTag) stack.save(helper.getLevel().registryAccess()));
        helper.assertTrue(GluonGunItem.silkTouch(restored), "Silk mode survives item serialization");
        player.setItemInHand(InteractionHand.MAIN_HAND, restored);
        player.setShiftKeyDown(false);
        player.getInventory().setItem(1, cell("glueballs", 10));
        helper.setBlock(new BlockPos(2, 2, 3), Blocks.DIAMOND_ORE);
        QMDItems.GLUON_GUN.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        var beam = new GluonBeamEntity(QMDEntities.GLUON_BEAM.get(), helper.getLevel());
        beam.configure(player, InteractionHand.MAIN_HAND, 40, 0xFFFFFF);
        QMDItems.GLUON_GUN.get().fire(player, InteractionHand.MAIN_HAND, beam);
        var drops = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                new AABB(helper.absolutePos(new BlockPos(2, 2, 3))).inflate(0.5));
        helper.assertTrue(drops.stream().anyMatch(item -> item.getItem().is(Blocks.DIAMOND_ORE.asItem())),
                "Silk mode must produce diamond ore rather than diamonds");
        player.stopUsingItem(); beam.discard();
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void antimatterLaunchPersistsAndDetonatesInWater(GameTestHelper helper) {
        ServerPlayer player = player(helper, QMDItems.ANTIMATTER_LAUNCHER.get().getDefaultInstance());
        player.getInventory().setItem(1, cell("antihelium", 5000));
        QMDItems.ANTIMATTER_LAUNCHER.get().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        var projectiles = helper.getLevel().getEntitiesOfClass(AntimatterProjectileEntity.class,
                new AABB(player.blockPosition()).inflate(3));
        helper.assertTrue(projectiles.size() == 1 && player.getInventory().getItem(1).is(QMDItems.EMPTY_CELL.get()),
                "Launcher consumes 5000 units and spawns one projectile");
        var projectile = projectiles.getFirst();
        helper.assertTrue(projectile.mass() == 4 && projectile.getDeltaMovement().length() > 2.9,
                "Antihelium launches with mass four and legacy speed three");
        CompoundTag saved = new CompoundTag(); projectile.saveWithoutId(saved);
        var restored = new AntimatterProjectileEntity(QMDEntities.ANTIMATTER_PROJECTILE.get(), helper.getLevel());
        restored.load(saved); projectile.discard();
        helper.assertTrue(restored.mass() == 4 && restored.color() == 0xC57B81 && restored.getOwner() == player,
                "Mass, color and shooter survive projectile persistence");
        BlockPos water = helper.absolutePos(new BlockPos(2, 2, 3));
        helper.getLevel().setBlock(water, Blocks.WATER.defaultBlockState(), 3);
        restored.setPos(Vec3.atCenterOf(water)); restored.setDeltaMovement(Vec3.ZERO);
        restored.tick();
        var flashes = helper.getLevel().getEntitiesOfClass(GammaFlashEntity.class, new AABB(water).inflate(2));
        helper.assertTrue(restored.isRemoved() && flashes.stream().anyMatch(flash -> flash.size() == 4),
                "Water contact detonates the projectile and creates the mass-scaled gamma flash");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void weaponRecipesLoadAndCraftTheLeptonCannon(GameTestHelper helper) {
        for (String weapon : java.util.List.of("lepton_cannon", "gluon_gun", "antimatter_launcher")) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(
                    net.minecraft.resources.ResourceLocation.parse("qmd:item.qmd." + weapon)).isPresent(),
                    "Generated weapon recipe must load: " + weapon);
        }
        var recipe = (net.minecraft.world.item.crafting.CraftingRecipe) helper.getLevel().getRecipeManager().byKey(
                net.minecraft.resources.ResourceLocation.parse("qmd:item.qmd.lepton_cannon")).orElseThrow().value();
        var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, java.util.List.of(
                item("qmd:semiconductor_elite_processor"), item("qmd:part_laser_assembly"), ItemStack.EMPTY,
                item("qmd:ingot_osmiridium"), item("qmd:part_accelerating_barrel"), item("nuclearcraftneohaul:copper_solenoid"),
                item("qmd:ingot_super_alloy"), ItemStack.EMPTY, ItemStack.EMPTY));
        helper.assertTrue(recipe.matches(input, helper.getLevel())
                        && recipe.assemble(input, helper.getLevel().registryAccess()).is(QMDItems.LEPTON_CANNON.get()),
                "The legacy lepton pattern must craft using the migrated components");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons")
    public static void gluonBeamRestoresOwnerAndStopsWithTheActiveHand(GameTestHelper helper) {
        ServerPlayer player = player(helper, QMDItems.GLUON_GUN.get().getDefaultInstance());
        player.setItemInHand(InteractionHand.OFF_HAND, QMDItems.GLUON_GUN.get().getDefaultInstance());
        player.getInventory().setItem(1, cell("glueballs", 20));
        player.startUsingItem(InteractionHand.OFF_HAND);
        var beam = new GluonBeamEntity(QMDEntities.GLUON_BEAM.get(), helper.getLevel());
        beam.configure(player, InteractionHand.OFF_HAND, 12.5D, 0xFFFFFF);
        CompoundTag saved = new CompoundTag(); beam.saveWithoutId(saved);
        var restored = new GluonBeamEntity(QMDEntities.GLUON_BEAM.get(), helper.getLevel());
        restored.load(saved);
        helper.assertTrue(restored.owner() == player && restored.hand() == InteractionHand.OFF_HAND
                        && restored.length() == 12.5F,
                "Continuous beam persists owner, hand and length");
        player.setPos(player.position().add(0.25, 0, 0));
        restored.tick();
        helper.assertTrue(restored.position().equals(player.getEyePosition())
                        && ParticleReservoirItem.stored(player.getInventory().getItem(1)) == 10,
                "The actual beam tick follows its shooter and consumes ten cell units");
        player.stopUsingItem();
        restored.tick();
        helper.assertTrue(restored.isRemoved() && ParticleReservoirItem.stored(player.getInventory().getItem(1)) == 10,
                "Releasing the active hand removes the beam without consuming another tick of ammo");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.weapons.impact")
    public static void antimatterSweptImpactHonorsCancellation(GameTestHelper helper) {
        ServerPlayer player = player(helper, QMDItems.ANTIMATTER_LAUNCHER.get().getDefaultInstance());
        helper.setBlock(new BlockPos(2, 2, 3), Blocks.STONE);
        var projectile = new AntimatterProjectileEntity(QMDEntities.ANTIMATTER_PROJECTILE.get(), helper.getLevel());
        projectile.configure(player, 1, 0xB37AC4);
        projectile.setDeltaMovement(0, 0, 1);
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.ProjectileImpactEvent> cancel = event -> {
            if (event.getProjectile() == projectile) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(cancel);
        try { projectile.tick(); projectile.tick(); }
        finally { NeoForge.EVENT_BUS.unregister(cancel); }
        helper.assertTrue(!projectile.isRemoved(), "Canceled swept collision must not detonate antimatter");
        projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ() + 1);
        projectile.setDeltaMovement(0, 0, 1);
        projectile.tick();
        helper.assertTrue(projectile.isRemoved(), "An uncanceled solid-block collision must detonate antimatter");
        helper.succeed();
    }

    private static ItemStack item(String id) {
        return new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                net.minecraft.resources.ResourceLocation.parse(id)));
    }

    private static ServerPlayer player(GameTestHelper helper, ItemStack gun) {
        ServerPlayer player = net.neoforged.neoforge.common.util.FakePlayerFactory.get(helper.getLevel(),
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "WeaponTest"));
        player.setGameMode(GameType.SURVIVAL);
        helper.getLevel().addNewPlayer(player);
        player.setPos(Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(2, 1, 1))));
        player.setYRot(0); player.setXRot(0);
        player.setItemInHand(InteractionHand.MAIN_HAND, gun);
        return player;
    }
    private static ItemStack cell(String name, int amount) {
        ItemStack stack = QMDItems.PARTICLE_CELLS.get(name).get().getDefaultInstance();
        ParticleReservoirItem.setStored(stack, amount);
        return stack;
    }
    private WeaponGameTests() {}
}
