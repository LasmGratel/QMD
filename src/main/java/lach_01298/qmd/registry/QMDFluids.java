package lach_01298.qmd.registry;

import com.nred.nuclearcraft.block.fluid.LiquidFluidBlock;
import com.nred.nuclearcraft.info.NCFluid;
import com.nred.nuclearcraft.info.NCFluidMaker;
import lach_01298.qmd.QuantumMinecraftDynamics;
import lach_01298.qmd.block.ExoticFluidBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Fluids used by the migrated collectors and ore-leaching production chains. */
public final class QMDFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister
        .create(NeoForgeRegistries.FLUID_TYPES, QuantumMinecraftDynamics.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister
        .create(BuiltInRegistries.FLUID, QuantumMinecraftDynamics.MODID);

    private static final List<NCFluid> ENTRIES = new ArrayList<>();
    private static final NCFluidMaker MAKER = new NCFluidMaker(FLUID_TYPES, FLUIDS, QMDBlocks.BLOCKS, QMDItems.ITEMS);

    public static final NCFluid HYDROCHLORIC_ACID = registerFluid("hydrochloric_acid", 0xFF99FFEE, NCFluid.ACID_TYPE);
    public static final NCFluid NITRIC_ACID = registerFluid("nitric_acid", 0xFF4F9EFF, NCFluid.ACID_TYPE);
    public static final NCFluid SODIUM_CHLORIDE_SOLUTION = registerFluid(
        "sodium_chloride_solution",
        FastColor.ARGB32.lerp(0.5F, 0xFF2F43F4, 0xFF0057FA),
        NCFluid.SALT_SOLUTION_TYPE);
    public static final NCFluid SALT_WATER = registerFluid(
        "salt_water",
        FastColor.ARGB32.lerp(0.5F, 0xFF2F43F4, 0xFF2974FF),
        NCFluid.SALT_SOLUTION_TYPE);
    public static final NCFluid CHLORINE = registerFluid("chlorine", 0xFFFFFF8F, NCFluid.GAS_TYPE);
    public static final NCFluid NITRIC_OXIDE = registerFluid("nitric_oxide", 0xFFC9EEFF, NCFluid.GAS_TYPE);
    public static final NCFluid NITROGEN_DIOXIDE = registerFluid("nitrogen_dioxide", 0xFF782A10, NCFluid.GAS_TYPE);
    public static final NCFluid COMPRESSED_AIR = registerFluid("compressed_air", 0xFFBDF0FF, NCFluid.GAS_TYPE);
    public static final NCFluid LIQUID_AIR = registerFluid(
        "liquid_air",
        0xFF6CDEFF,
        new NCFluid.TypeInfo(
            8,
            false,
            "liquid_still",
            "liquid_flow",
            FluidType.Properties.create()
                .density(870)
                .temperature(79)
                .viscosity(170),
            LiquidFluidBlock::new));
    public static final NCFluid ARGON = registerFluid("argon", 0xFFFF75DD, NCFluid.GAS_TYPE);
    public static final NCFluid NEON = registerFluid("neon", 0xFFFF9F7A, NCFluid.GAS_TYPE);

    public static final NCFluid LIQUID_ARGON = liquid("liquid_argon", 0xFFFF75DD, 1395, 87, 170);
    public static final NCFluid LIQUID_NEON = liquid("liquid_neon", 0xFFFF9F7A, 1207, 27, 170);
    public static final NCFluid LIQUID_HYDROGEN = liquid("liquid_hydrogen", 0xFFB37AC4, 71, 20, 170);
    public static final NCFluid LIQUID_OXYGEN = liquid("liquid_oxygen", 0xFF7E8CC8, 1141, 90, 170);
    public static final Map<String, NCFluid> EXOTIC;
    static {
        Map<String, NCFluid> exotic = new LinkedHashMap<>();
        String[] names = { "antihydrogen", "antideuterium", "antitritium", "antihelium3", "antihelium", "positronium",
            "muonium", "tauonium", "glueballs" };
        int[] colors = { 0xB37AC4, 0x9E6FEF, 0x5DBBD6, 0xCBBB67, 0xC57B81, 0xC9C9C9, 0x9B93FF, 0xC86300, 0xA6F1F2 };
        for (int i = 0; i < names.length; i++) exotic.put(
            names[i],
            registerFluid(
                names[i],
                0xFF000000 | colors[i],
                new NCFluid.TypeInfo(
                    8,
                    false,
                    "liquid_still",
                    "liquid_flow",
                    FluidType.Properties.create()
                        .density(1000)
                        .viscosity(1000),
                    ExoticFluidBlock::new)));
        EXOTIC = Collections.unmodifiableMap(exotic);
        String[] solutions = { "sodium_nitrate", "lead_nitrate", "sodium_tungstate", "lead_tungstate" };
        int[] soluteColors = { 0xFFFFFF, 0xD5D5D5, 0xFFFEA3, 0xD58715 };
        for (int i = 0; i < solutions.length; i++) registerFluid(
            solutions[i] + "_solution",
            FastColor.ARGB32.lerp(0.5F, 0xFF2F43F4, 0xFF000000 | soluteColors[i]),
            NCFluid.SALT_SOLUTION_TYPE);
        liquid("mercury", 0xFFC6C6C6, 13540, 300, 1000);
        liquid("hot_mercury", 0xFFD0D0D0, 13540, 630, 1000);
        liquid("iodine", 0xFF7400B3, 3960, 387, 1000);
        String[] metals = { "silicon", "yag", "nd_yag", "tungsten", "niobium", "chromium", "titanium", "cobalt",
            "nickel", "hafnium", "zinc", "osmium", "iridium", "platinum", "calcium", "strontium", "yttrium",
            "neodymium", "samarium", "erbium", "ytterbium", "bismuth", "polonium", "radium", "sodium_chloride" };
        int[] metalColors = { 0x676767, 0xFFFDDB, 0xE4BCF5, 0x4E564F, 0xCCCCC0, 0xC9C9C9, 0x8E7E8D, 0x364F70, 0xA3A998,
            0x948484, 0xE0E0E0, 0x6F89A9, 0xDDD5DD, 0x71A4A9, 0xF4F3EA, 0xC4CB97, 0xD1C375, 0x818A97, 0xA4D95F,
            0x5A7A45, 0x7A4552, 0x827E73, 0x5C7C78, 0x6E607B, 0xD4CCCC };
        for (int i = 0; i < metals.length; i++)
            registerFluid(metals[i], 0xFF000000 | metalColors[i], NCFluid.MOLTEN_TYPE);
        registerFluid("carbon", 0xFF343434, NCFluid.HOT_GAS_TYPE);
        registerFluid("high_pressure_mercury", 0xFFAAAAAA, NCFluid.HOT_GAS_TYPE);
        registerFluid("exhaust_mercury", 0xFF888888, NCFluid.HOT_GAS_TYPE);
    }

    private static NCFluid registerFluid(String name, int tint, NCFluid.TypeInfo type) {
        NCFluid fluid = MAKER.registerFluid(name, tint, type);
        ENTRIES.add(fluid);
        return fluid;
    }

    private static NCFluid liquid(String name, int tint, int density, int temperature, int viscosity) {
        return registerFluid(
            name,
            tint,
            new NCFluid.TypeInfo(
                8,
                false,
                "liquid_still",
                "liquid_flow",
                FluidType.Properties.create()
                    .density(density)
                    .temperature(temperature)
                    .viscosity(viscosity),
                LiquidFluidBlock::new));
    }

    private QMDFluids() {}

    public static List<NCFluid> entries() {
        return Collections.unmodifiableList(ENTRIES);
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

}
