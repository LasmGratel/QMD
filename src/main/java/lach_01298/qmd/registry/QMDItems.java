package lach_01298.qmd.registry;

import com.nred.nuclearcraft.capability.radiation.resistance.RadiationResistanceItem;
import com.nred.nuclearcraft.radiation.RadArmor;
import com.nred.nuclearcraft.radiation.RadSources;
import com.nred.nuclearcraft.recipe.RecipeHelper;
import com.nred.nuclearcraft.registration.DataComponentRegistration;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.capability.ParticleCellFluidHandler;
import lach_01298.qmd.config.EquipmentConfig;
import lach_01298.qmd.item.ParticleCellItem;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.item.equipment.DrillItem;
import lach_01298.qmd.item.equipment.EnergyEquipment;
import lach_01298.qmd.item.equipment.EquipmentEvents;
import lach_01298.qmd.item.equipment.HevArmorItem;
import lach_01298.qmd.item.equipment.TungstenCarbideTier;
import lach_01298.qmd.item.player.BeamMeterItem;
import lach_01298.qmd.item.player.LuminousPaintItem;
import lach_01298.qmd.item.player.PotassiumIodineTabletItem;
import lach_01298.qmd.item.weapon.AntimatterLauncherItem;
import lach_01298.qmd.item.weapon.GluonGunItem;
import lach_01298.qmd.item.weapon.LeptonCannonItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** All QMD items, material families and item integration hooks. */
public final class QMDItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(QuantumMinecraftDynamics.MODID);

    private static final Map<String, DeferredItem<Item>> MATERIAL_ITEMS = new LinkedHashMap<>();

    private static final Map<LegacyMaterial, DeferredItem<Item>> LEGACY_ITEMS = new LinkedHashMap<>();

    private static final List<Material> MATERIALS = new ArrayList<>();

    static {
        registerGroup(
            "dust",
            "dust",
            "tungsten",
            "niobium",
            "chromium",
            "titanium",
            "cobalt",
            "nickel",
            "hafnium",
            "zinc",
            "osmium",
            "iridium",
            "platinum",
            "sodium",
            "potassium",
            "calcium",
            "strontium",
            "barium");
        registerGroup("dust2", "dust", "yttrium", "neodymium", "iodine", "samarium", "terbium", "erbium", "ytterbium");
        registerGroup(
            "ingot",
            "ingot",
            "tungsten",
            "niobium",
            "chromium",
            "titanium",
            "cobalt",
            "nickel",
            "hafnium",
            "zinc",
            "osmium",
            "iridium",
            "platinum",
            "sodium",
            "potassium",
            "calcium",
            "strontium",
            "barium");
        registerGroup("ingot2", "ingot", "yttrium", "neodymium", "mercury");
        registerGroup(
            "ingot_alloy",
            "ingot",
            "tungsten_carbide",
            "niobium_tin",
            "stainless_steel",
            "niobium_titanium",
            "osmiridium",
            "nichrome",
            "super_alloy");
        registerGroup(
            "part",
            "part",
            "empty_cooler",
            "detector_casing",
            "scintillator_pwo",
            "scintillator_plastic",
            "wire_bscco",
            "rod_nd_yag",
            "wire_gold_tungsten",
            "wire_chamber_casing",
            "magnet_nd",
            "accelerating_barrel",
            "laser_assembly",
            "wire_ssfaf",
            "wire_ybco",
            "magnet_smc",
            "wire_hbcco");
        registerGroup(
            "semiconductor",
            "semiconductor",
            "silicon_p_doped",
            "silicon_n_doped",
            "silicon_boule",
            "silicon_wafer",
            "basic_processor",
            "advanced_processor",
            "elite_processor");
        registerGroup(
            "chemical_dust",
            "chemical_dust",
            "tungsten_oxide",
            "bscco",
            "sodium_nitrate",
            "sodium_chloride",
            "copper_oxide",
            "hafnium_oxide",
            "strontium_chloride",
            "zinc_sulfide",
            "iron_fluoride",
            "ssfaf",
            "ybco",
            "hbcco");
        registerGroup(
            "waste_spallation",
            "waste_spallation",
            "californium",
            "berkelium",
            "curium",
            "americium",
            "plutonium",
            "neptunium",
            "uranium",
            "protactinium",
            "thorium",
            "radium",
            "polonium",
            "bismuth",
            "lead",
            "gold",
            "platinum",
            "iridium");
        registerGroup("waste_spallation2", "waste_spallation", "osmium", "tungsten", "hafnium", "mercury");
        registerGroup("waste_fission", "waste_fission", "light", "heavy");
    }

    public static final DeferredItem<BlockItem> ORE_LEACHER_ITEM = ITEMS.registerSimpleBlockItem(QMDBlocks.ORE_LEACHER);

    public static final DeferredItem<BlockItem> LIQUID_COLLECTOR_ITEM = ITEMS
        .registerSimpleBlockItem(QMDBlocks.LIQUID_COLLECTOR);

    public static final DeferredItem<BlockItem> ATMOSPHERE_COLLECTOR_ITEM = ITEMS
        .registerSimpleBlockItem(QMDBlocks.ATMOSPHERE_COLLECTOR);

    public static final DeferredItem<BlockItem> CREATIVE_PARTICLE_SOURCE_ITEM = ITEMS
        .registerSimpleBlockItem(QMDBlocks.CREATIVE_PARTICLE_SOURCE);

    public static final DeferredItem<Item> EMPTY_CELL = ITEMS
        .registerSimpleItem("cell_empty", new Item.Properties().stacksTo(1));

    public static final Map<String, DeferredItem<ParticleReservoirItem>> PARTICLE_SOURCES;

    public static final Map<String, DeferredItem<ParticleReservoirItem>> PARTICLE_CELLS;

    static {
        Map<String, DeferredItem<ParticleReservoirItem>> sources = new LinkedHashMap<>();
        String[] names = { "tungsten_filament", "sodium_22", "cobalt_60", "iridium_192", "calcium_48" };
        int[] capacities = { 50_000_000, 5_000_000, 1_000_000, 1_000_000, 5_000_000 };
        for (int i = 0; i < names.length; i++) {
            int capacity = capacities[i];
            sources.put(
                names[i],
                ITEMS.register(
                    "source_" + names[i],
                    () -> new ParticleReservoirItem(
                        new Item.Properties().stacksTo(16),
                        capacity,
                        () -> ItemStack.EMPTY)));
        }
        PARTICLE_SOURCES = Collections.unmodifiableMap(sources);
        Map<String, DeferredItem<ParticleReservoirItem>> cells = new LinkedHashMap<>();
        String[] species = { "antihydrogen", "antideuterium", "antitritium", "antihelium3", "antihelium", "positronium",
            "muonium", "tauonium", "glueballs" };
        double[] masses = { 1, 2, 3, 3, 4, 0.00054, 0.11, 1.9, 1.8 };
        for (int i = 0; i < species.length; i++) {
            String name = species[i];
            double mass = masses[i];
            cells.put(name, ITEMS.register("cell_" + name, () -> new ParticleCellItem(name, mass)));
        }
        PARTICLE_CELLS = Collections.unmodifiableMap(cells);
    }

    public static final Map<String, DeferredItem<Item>> NUCLEAR_MATERIALS;

    public static final double MIX_RADIATION = RadSources.getFuelRadiation(0.000833, 8, 0.000833, 1);

    public static final double DEPLETED_RADIATION = RadSources.getDepletedFuelRadiation(
        RadSources.AMERICIUM_243,
        4,
        RadSources.CURIUM_243,
        2,
        RadSources.CURIUM_245,
        1,
        RadSources.BERKELIUM_247,
        1,
        RadSources.RUTHENIUM_106,
        RadSources.EUROPIUM_155,
        1.5,
        0.6);

    static {
        Map<String, DeferredItem<Item>> items = new LinkedHashMap<>();
        for (String name : new String[] { "sodium_22", "beryllium_7", "magnesium_24", "magnesium_26", "uranium_234",
            "protactinium_231", "cobalt_60", "iridium_192", "calcium_48" })
            items.put("isotope_" + name, ITEMS.registerSimpleItem("isotope_" + name));
        for (String base : new String[] { "copernicium_291", "mix_291" }) {
            for (String suffix : new String[] { "", "_c", "_ox", "_ni", "_za" })
                items.put(base + suffix, ITEMS.registerSimpleItem(base + suffix));
        }
        for (String suffix : new String[] { "_tr", "_ox", "_ni", "_za" }) {
            if (suffix.equals("_tr")) items.put("mix_291" + suffix, ITEMS.registerSimpleItem("mix_291" + suffix));
            items.put("depleted_mix_291" + suffix, ITEMS.registerSimpleItem("depleted_mix_291" + suffix));
        }
        NUCLEAR_MATERIALS = Collections.unmodifiableMap(items);
    }

    public static final DeferredItem<SwordItem> SWORD_TUNGSTEN_CARBIDE = ITEMS.register(
        "sword_tungsten_carbide",
        () -> new SwordItem(
            TungstenCarbideTier.INSTANCE,
            new Item.Properties().attributes(SwordItem.createAttributes(TungstenCarbideTier.INSTANCE, 3, -2.4F))));

    public static final DeferredItem<PickaxeItem> PICKAXE_TUNGSTEN_CARBIDE = ITEMS.register(
        "pickaxe_tungsten_carbide",
        () -> new PickaxeItem(
            TungstenCarbideTier.INSTANCE,
            new Item.Properties().attributes(PickaxeItem.createAttributes(TungstenCarbideTier.INSTANCE, 1.0F, -2.8F))));

    public static final DeferredItem<ShovelItem> SHOVEL_TUNGSTEN_CARBIDE = ITEMS.register(
        "shovel_tungsten_carbide",
        () -> new ShovelItem(
            TungstenCarbideTier.INSTANCE,
            new Item.Properties().attributes(ShovelItem.createAttributes(TungstenCarbideTier.INSTANCE, 1.5F, -3.0F))));

    public static final DeferredItem<AxeItem> AXE_TUNGSTEN_CARBIDE = ITEMS.register(
        "axe_tungsten_carbide",
        () -> new AxeItem(
            TungstenCarbideTier.INSTANCE,
            new Item.Properties().attributes(AxeItem.createAttributes(TungstenCarbideTier.INSTANCE, 3.0F, -3.0F))));

    public static final DeferredItem<HoeItem> HOE_TUNGSTEN_CARBIDE = ITEMS.register(
        "hoe_tungsten_carbide",
        () -> new HoeItem(
            TungstenCarbideTier.INSTANCE,
            new Item.Properties().attributes(HoeItem.createAttributes(TungstenCarbideTier.INSTANCE, -3.0F, 0.0F))));

    public static final DeferredItem<DrillItem> DRILL_BASIC = ITEMS
        .register("drill_basic", () -> new DrillItem(1, 250_000, 3, 8.0F));

    public static final DeferredItem<DrillItem> DRILL_ADVANCED = ITEMS
        .register("drill_advanced", () -> new DrillItem(2, 2_500_000, 4, 12.0F));

    public static final DeferredItem<HevArmorItem> HELM_HEV = ITEMS
        .register("helm_hev", () -> hev(ArmorItem.Type.HELMET, 4, 1, 20.0D, 0.2D));

    public static final DeferredItem<HevArmorItem> CHEST_HEV = ITEMS
        .register("chest_hev", () -> hev(ArmorItem.Type.CHESTPLATE, 9, 4, 30.0D, 0.4D));

    public static final DeferredItem<HevArmorItem> LEGS_HEV = ITEMS
        .register("legs_hev", () -> hev(ArmorItem.Type.LEGGINGS, 7, 3, 20.0D, 0.2D));

    public static final DeferredItem<HevArmorItem> BOOTS_HEV = ITEMS
        .register("boots_hev", () -> hev(ArmorItem.Type.BOOTS, 4, 1, 20.0D, 0.2D));

    public static final List<DeferredItem<? extends Item>> EQUIPMENT = List.of(
        SWORD_TUNGSTEN_CARBIDE,
        PICKAXE_TUNGSTEN_CARBIDE,
        SHOVEL_TUNGSTEN_CARBIDE,
        AXE_TUNGSTEN_CARBIDE,
        HOE_TUNGSTEN_CARBIDE,
        DRILL_BASIC,
        DRILL_ADVANCED,
        HELM_HEV,
        CHEST_HEV,
        LEGS_HEV,
        BOOTS_HEV);

    public static final DeferredItem<LeptonCannonItem> LEPTON_CANNON = ITEMS
        .register("lepton_cannon", LeptonCannonItem::new);

    public static final DeferredItem<GluonGunItem> GLUON_GUN = ITEMS.register("gluon_gun", GluonGunItem::new);

    public static final DeferredItem<AntimatterLauncherItem> ANTIMATTER_LAUNCHER = ITEMS
        .register("antimatter_launcher", AntimatterLauncherItem::new);

    private static final ResourceLocation GUIDE_ID = ResourceLocation.fromNamespaceAndPath("qmd", "qmd");

    public static final DeferredItem<LuminousPaintItem> GREEN_LUMINOUS_PAINT_ITEM = ITEMS.register(
        "green_luminous_paint",
        () -> new LuminousPaintItem(QMDBlocks.GREEN_LUMINOUS_PAINT_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<LuminousPaintItem> BLUE_LUMINOUS_PAINT_ITEM = ITEMS.register(
        "blue_luminous_paint",
        () -> new LuminousPaintItem(QMDBlocks.BLUE_LUMINOUS_PAINT_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<LuminousPaintItem> ORANGE_LUMINOUS_PAINT_ITEM = ITEMS.register(
        "orange_luminous_paint",
        () -> new LuminousPaintItem(QMDBlocks.ORANGE_LUMINOUS_PAINT_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BeamMeterItem> BEAM_METER = ITEMS
        .register("beam_meter", () -> new BeamMeterItem(new Item.Properties()));

    public static final DeferredItem<PotassiumIodineTabletItem> POTASSIUM_IODINE_TABLET = ITEMS
        .register("potassium_iodine_tablet", () -> new PotassiumIodineTabletItem(new Item.Properties()));

    public static final DeferredItem<Item> FLESH = ITEMS.registerSimpleItem(
        "flesh",
        new Item.Properties().food(
            new FoodProperties.Builder().nutrition(4)
                .saturationModifier(0.1F)
                .build()));

    public static final DeferredItem<BlockItem> BEAMLINE_ITEM = ITEMS.registerSimpleBlockItem(QMDBlocks.BEAMLINE);

    public static final DeferredItem<BlockItem> SUPER_ALLOY_BLADE_ITEM = ITEMS
        .registerSimpleBlockItem(QMDBlocks.SUPER_ALLOY_TURBINE_BLADE);

    public static final DeferredItem<BlockItem> IRRADIATOR_ITEM = ITEMS.registerSimpleBlockItem(QMDBlocks.IRRADIATOR);

    static {
        QMDBlocks.LIQUEFIER_PARTS.forEach(ITEMS::registerSimpleBlockItem);
        QMDBlocks.ACCELERATOR_BLOCKS.values()
            .forEach(ITEMS::registerSimpleBlockItem);
        QMDBlocks.CHAMBER_PARTS.values()
            .forEach(ITEMS::registerSimpleBlockItem);
        QMDBlocks.FISSION_BLOCKS.forEach(ITEMS::registerSimpleBlockItem);
        QMDBlocks.DISCHARGE_LAMPS.values()
            .forEach(ITEMS::registerSimpleBlockItem);
    }

    private static void registerGroup(String legacyItem, String prefix, String... variants) {
        for (int metadata = 0; metadata < variants.length; metadata++) {
            String id = prefix + "_" + variants[metadata];
            if (MATERIAL_ITEMS.containsKey(id)) {
                throw new IllegalStateException("Duplicate QMD material: " + id);
            }
            DeferredItem<Item> item = ITEMS.registerSimpleItem(id);
            MATERIAL_ITEMS.put(id, item);
            LEGACY_ITEMS.put(new LegacyMaterial(legacyItem, metadata), item);
            MATERIALS.add(new Material(id, legacyItem, metadata, variants[metadata]));
        }
    }

    /** Returns a registered item by its QMD path, for example {@code ingot_tungsten}. */
    public static DeferredItem<Item> get(String id) {
        DeferredItem<Item> item = MATERIAL_ITEMS.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown QMD material: " + id);
        }
        return item;
    }

    /** Resolves a legacy QMD registry path and metadata value for recipe migration. */
    public static DeferredItem<Item> fromLegacy(String legacyItem, int metadata) {
        DeferredItem<Item> item = LEGACY_ITEMS.get(new LegacyMaterial(legacyItem, metadata));
        if (item == null) {
            throw new IllegalArgumentException("Unknown legacy QMD material: " + legacyItem + ":" + metadata);
        }
        return item;
    }

    public static Collection<DeferredItem<Item>> entries() {
        return Collections.unmodifiableCollection(MATERIAL_ITEMS.values());
    }

    public static List<Material> materials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    public record Material(String id, String legacyItem, int legacyMetadata, String variant) {}

    private record LegacyMaterial(String item, int metadata) {}

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(QMDItems::registerCapabilities);
        bus.addListener(QMDItems::modifyDefaultComponents);
        bus.addListener(QMDItems::commonSetup);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onLivingJump);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onLivingFall);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onEffectApplicable);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(EquipmentEvents::onLivingDamage);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
            Capabilities.FluidHandler.ITEM,
            (stack, context) -> new ParticleCellFluidHandler(stack),
            EMPTY_CELL.get());
        PARTICLE_CELLS.values()
            .forEach(
                item -> event.registerItem(
                    Capabilities.FluidHandler.ITEM,
                    (stack, context) -> new ParticleCellFluidHandler(stack),
                    item.get()));

        for (DeferredItem<? extends Item> entry : List
            .of(DRILL_BASIC, DRILL_ADVANCED, HELM_HEV, CHEST_HEV, LEGS_HEV, BOOTS_HEV)) {
            event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> {
                int capacity = ((EnergyEquipment) stack.getItem()).energyCapacity();
                return new ComponentEnergyStorage(
                    stack,
                    DataComponentRegistration.ENERGY_COMPONENT.get(),
                    capacity,
                    capacity,
                    capacity);
            }, entry.get());
        }
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RadSources.RUNNABLES.add(QMDItems::registerNuclearRadiation);
            registerNuclearRadiation();
        });

        event.enqueueWork(() -> {
            RadSources.RUNNABLES.add(QMDItems::registerPaintRadiation);
            registerPaintRadiation();
        });

        event.enqueueWork(() -> RadSources.addToFoodMaps(new ItemStack(FLESH.get()), 0, 3D));
    }

    private static void registerNuclearRadiation() {
        String[] names = { "sodium_22", "beryllium_7", "uranium_234", "protactinium_231", "cobalt_60", "iridium_192" };
        double[] doses = { 0.384, 6.89, 0.00000407, 0.0000305, 0.190, 4.94 };
        for (int i = 0; i < names.length; i++) RadSources.put(
            doses[i],
            NUCLEAR_MATERIALS.get("isotope_" + names[i])
                .get());
        RadSources.put(
            0.384 / 2,
            PARTICLE_SOURCES.get("sodium_22")
                .get());
        RadSources.put(
            0.190 / 2,
            PARTICLE_SOURCES.get("cobalt_60")
                .get());
        RadSources.put(
            4.94 / 2,
            PARTICLE_SOURCES.get("iridium_192")
                .get());
        NUCLEAR_MATERIALS.forEach((name, item) -> {
            if (name.startsWith("copernicium")) RadSources.put(0.000833, item.get());
            else if (name.startsWith("depleted_mix")) RadSources.put(DEPLETED_RADIATION, item.get());
            else if (name.startsWith("mix")) RadSources.put(MIX_RADIATION, item.get());
        });
        RadSources.put(RadSources.STRONTIUM_90 / 4, QMDBlocks.STRONTIUM_RTG.get());
        RadSources.put(RadSources.STRONTIUM_90 * 9, QMDBlocks.STRONTIUM_90_BLOCK.get());
        RadSources.put(
            RadSources.TRITIUM * (125D / 18D) * 0.1,
            PARTICLE_CELLS.get("antitritium")
                .get());
        RadSources.putFluid(RadSources.TRITIUM, "antitritium");
        materials().forEach(material -> {
            if (material.id()
                .startsWith("waste_fission_")) RadSources.put(RadSources.CAESIUM_137, get(material.id()).get());
            else if (material.id()
                .startsWith("waste_spallation_")) RadSources.put(RadSources.CAESIUM_137 / 2, get(material.id()).get());
        });
    }

    public static ItemStack manualStack() {
        return PatchouliAPI.get()
            .getBookStack(GUIDE_ID);
    }

    public static void registerPaintRadiation() {
        double radiation = LuminousPaintItem.RADIATION;
        RadSources.put(
            radiation,
            GREEN_LUMINOUS_PAINT_ITEM.get(),
            BLUE_LUMINOUS_PAINT_ITEM.get(),
            ORANGE_LUMINOUS_PAINT_ITEM.get());
        RadSources.put(
            radiation,
            QMDBlocks.GREEN_LUMINOUS_PAINT_BLOCK.get(),
            QMDBlocks.BLUE_LUMINOUS_PAINT_BLOCK.get(),
            QMDBlocks.ORANGE_LUMINOUS_PAINT_BLOCK.get());
    }

    public static void addEquipmentToCreativeTab(CreativeModeTab.Output output) {
        if (EquipmentConfig.toolsEnabled()) {
            output.accept(SWORD_TUNGSTEN_CARBIDE);
            output.accept(PICKAXE_TUNGSTEN_CARBIDE);
            output.accept(SHOVEL_TUNGSTEN_CARBIDE);
            output.accept(AXE_TUNGSTEN_CARBIDE);
            output.accept(HOE_TUNGSTEN_CARBIDE);
        }
        if (EquipmentConfig.drillsEnabled()) {
            output.accept(DRILL_BASIC);
            output.accept(DRILL_ADVANCED);
        }
        output.accept(HELM_HEV);
        output.accept(CHEST_HEV);
        output.accept(LEGS_HEV);
        output.accept(BOOTS_HEV);
    }

    private static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        for (DeferredItem<HevArmorItem> entry : List.of(HELM_HEV, CHEST_HEV, LEGS_HEV, BOOTS_HEV)) {
            HevArmorItem armor = entry.get();
            event.modify(
                armor,
                builder -> builder
                    .set(
                        DataComponentRegistration.RADIATION_RESISTANCE_ITEM.get(),
                        new RadiationResistanceItem(armor.radiationResistance()))
                    .set(DataComponents.DYED_COLOR, new DyedItemColor(HevArmorItem.DEFAULT_COLOR, true)));
            RadArmor.ARMOR_STACK_SHIELDING_BLACKLIST.add(RecipeHelper.pack(armor));
        }
    }

    private static HevArmorItem hev(ArmorItem.Type type, int chargedDefense, int baseDefense,
        double radiationResistance, double specialProtection) {
        return new HevArmorItem(
            QMDArmorMaterials.HEV,
            type,
            chargedDefense,
            baseDefense,
            radiationResistance,
            specialProtection,
            new Item.Properties());
    }

    private QMDItems() {}
}
