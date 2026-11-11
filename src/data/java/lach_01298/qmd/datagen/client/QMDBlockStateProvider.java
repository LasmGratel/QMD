package lach_01298.qmd.datagen.client;

import com.nred.nuclearcraft.multiblock.turbine.TurbineRotorBladeUtil;
import lach_01298.qmd.block.accelerator.AbstractAcceleratorBlock;
import lach_01298.qmd.block.chamber.AbstractChamberBlock;
import lach_01298.qmd.item.player.LuminousPaintBlock;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.multiblock.accelerator.AcceleratorPartType;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDFluids;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Registry-driven block models and explicit state rotations, including native Neohaul properties. */
public final class QMDBlockStateProvider extends BlockStateProvider {
    public QMDBlockStateProvider(PackOutput output, ExistingFileHelper existing) {
        super(output, "qmd", existing);
    }

    @Override
    protected void registerStatesAndModels() {
        for (String color : java.util.List.of("green", "blue", "orange")) luminousPaint(color);
        for (var entry : QMDBlocks.ACCELERATOR_PARTS.values()) accelerator(entry);
        for (var role : ChamberPart.values()) chamber(role);
        QMDFluids.entries().forEach(fluid -> {
            String id = fluid.still.getId().getPath();
            String texture = QMDFluids.EXOTIC.containsKey(id) ? "qmd:block/fluid/antimatter_still" : fluid.client.getStillTexture().toString();
            models().cubeAll("block/fluid/" + id, ResourceLocation.parse(texture));
            single(id, modelFile("fluid/" + id));
        });
        for (String id : new String[]{"liquid_collector", "atmosphere_collector", "creative_particle_source"}) {
            cube(id, "block/" + (id.equals("creative_particle_source") ? "other/" : "machine/") + id);
            single(id, modelFile(id));
        }
        for (String id : new String[]{"ore_leacher", "irradiator"}) {
            String off = id.equals("irradiator") ? "_off" : "";
            String on = id.equals("irradiator") ? "_on" : "_active";
            models().singleTexture(id + off, ResourceLocation.parse("nuclearcraftneohaul:block/processor"),
                    "front", modLoc("block/machine/" + id + "_front_off"));
            models().singleTexture(id + on, ResourceLocation.parse("nuclearcraftneohaul:block/processor"),
                    "front", modLoc("block/machine/" + id + "_front_on"));
            horizontal(id, off, on, id.equals("irradiator") ? "active" : "nuclearcraftneohaul_active");
        }
        liquefier();
        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (id.getNamespace().equals("qmd") && id.getPath().startsWith("discharge_lamp_")) {
                String name = id.getPath();
                cube(name, "block/lamp/" + name.substring("discharge_lamp_".length()));
                cube(name + "_off", "block/lamp/" + name.substring("discharge_lamp_".length()) + "_off");
                toggle(name, "lit", "_off", "");
            }
        }
        cube("strontium_90_block", "block/strontium_90_block");
        single("strontium_90_block", modelFile("strontium_90_block"));
        cube("reflector_tungsten_carbide", "block/fission/reflector_tungsten_carbide");
        single("reflector_tungsten_carbide", modelFile("reflector_tungsten_carbide"));
        for (String state : new String[]{"off", "on"}) cube("neutron_shield_hafnium_" + state, "block/fission/shield_hafnium_" + state);
        toggle("neutron_shield_hafnium", "nuclearcraftneohaul_active", "_off", "_on");
        models().cubeColumn("rtg_strontium", modLoc("block/machine/rtg_strontium_side"), modLoc("block/machine/rtg_strontium_top"));
        single("rtg_strontium", modelFile("rtg_strontium"));
        models().singleTexture("turbine_blade_super_alloy", ResourceLocation.parse("nuclearcraftneohaul:block/turbine_rotor_blade"),
                "texture", modLoc("block/turbine_blade_super_alloy"));
        var bladeBlock = block("turbine_blade_super_alloy");
        getVariantBuilder(bladeBlock).forAllStates(state -> {
            String direction = state.getValue(TurbineRotorBladeUtil.DIR).getSerializedName();
            ModelFile model = direction.equals("invisible")
                    ? models().getExistingFile(ResourceLocation.parse("nuclearcraftneohaul:block/block_invisible"))
                    : modelFile("turbine_blade_super_alloy");
            return new ConfiguredModel[]{configured(model, direction.equals("y") || direction.equals("invisible") ? 0 : 90,
                    direction.equals("x") ? 90 : 0)};
        });
        // Beamline geometry/display transforms remain authored.
        var line = getVariantBuilder(block("beamline"));
        line.partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).addModels(configured(modelFile("beamline"), 0, 90));
        line.partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y).addModels(configured(modelFile("beamline"), 90, 0));
        line.partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).addModels(configured(modelFile("beamline"), 0, 0));
    }

    private void luminousPaint(String color) {
        var model = models().carpet(color + "_luminous_paint", modLoc("block/other/" + color + "_luminous_paint"));
        var variants = getVariantBuilder(block("block_" + color + "_luminous_paint"));
        for (Direction facing : Direction.values()) {
            int x = switch (facing) { case DOWN -> 180; case UP -> 0; case SOUTH -> 270; default -> 90; };
            int y = switch (facing) { case EAST -> 90; case WEST -> 270; default -> 0; };
            variants.partialState().with(LuminousPaintBlock.FACING, facing).addModels(configured(model, x, y));
        }
    }

    private void accelerator(AcceleratorPartType part) {
        String id = part.id;
        if (acceleratorSwitch(part.role)) {
            for (boolean active : new boolean[]{false, true}) for (boolean output : new boolean[]{false, true}) {
                String suffix = (active ? "_on" : "_off") + (output ? "_out" : "_in");
                String front = switch (part.role) {
                    case BEAM_PORT -> "beam_port/" + (output ? "output" : "input");
                    case SOURCE -> part.variant == 0 ? "source_front" : "laser_ion_source_front";
                    case COLLECTOR -> "ion_collector_front";
                    default -> id + "_front_" + (active ? "on" : "off");
                };
                facedCube(id + suffix, "qmd:block/accelerator/casing", "qmd:block/accelerator/" + front);
            }
            var variants = getVariantBuilder(block(id));
            for (boolean active : new boolean[]{false, true}) for (Direction facing : Direction.values()) for (boolean output : new boolean[]{false, true}) {
                String suffix = (active ? "_on" : "_off") + (output ? "_out" : "_in");
                variants.partialState().with(AbstractAcceleratorBlock.ACTIVE, active)
                        .with(AbstractAcceleratorBlock.FACING, facing).with(AbstractAcceleratorBlock.OUTPUT, output)
                        .addModels(configured(modelFile(id + suffix), x(facing), y(facing)));
            }
        } else {
            String texture = switch (part.role) {
                case CAVITY -> "cavity/" + part.material.id;
                case MAGNET -> "magnet/" + part.material.id;
                case COOLER -> "cooler/" + id.substring("accelerator_cooler_".length());
                case COOLANT_INLET -> "inlet";
                case COOLANT_OUTLET -> "outlet";
                case REDSTONE_PORT -> "redstone_in";
                case COMPUTER_PORT -> "computer";
                case SYNCHROTRON_PORT -> "beam_port/synchrotron";
                default -> id.substring("accelerator_".length());
            };
            cube(id, "block/accelerator/" + texture);
            single(id, modelFile(id));
        }
    }

    static boolean acceleratorSwitch(AcceleratorPartType.Role role) {
        return role == AcceleratorPartType.Role.CONTROLLER || role == AcceleratorPartType.Role.SOURCE
                || role == AcceleratorPartType.Role.COLLECTOR || role == AcceleratorPartType.Role.BEAM_PORT;
    }

    private void chamber(ChamberPart role) {
        String id = role.id;
        boolean toggled = role.controller() || role.beamPort() || role.fluidPort() || role == ChamberPart.REDSTONE_PORT;
        if (role.controller()) {
            String folder = role.vacuum() ? "vacuum" : "chamber";
            for (boolean active : new boolean[]{false, true}) models().orientable(id + (active ? "_active" : ""),
                    modLoc("block/" + folder + "/casing"),
                    modLoc("block/" + folder + "/" + id + "_front_" + (active ? "on" : "off")),
                    modLoc("block/" + folder + "/casing"));
        } else if (role == ChamberPart.CHAMBER) {
            var beam = modLoc("block/chamber/beam");
            var chamber = modLoc("block/chamber/chamber");
            models().cube(id, beam, beam, chamber, chamber, chamber, chamber).texture("particle", beam);
        } else if (role == ChamberPart.VACUUM_COIL) {
            models().cubeColumn(id, modLoc("block/vacuum/coil_side"), modLoc("block/vacuum/coil_top"));
        } else {
            for (boolean active : toggled ? new boolean[]{false, true} : new boolean[]{false}) {
                String texture;
                if (role.detector()) texture = "chamber/detector/" + id;
                else if (role.heater()) texture = "vacuum/heater/" + id.substring("vacuum_chamber_heater_".length());
                else texture = (role.vacuum() ? "vacuum/" : "chamber/") + switch (role) {
                    case BEAM_PORT, VACUUM_BEAM_PORT -> "beam_port/" + (active ? "output" : "input");
                    case FLUID_PORT -> active ? "outlet" : "inlet";
                    case VACUUM_FLUID_PORT -> "fluid_port_" + (active ? "output" : "input");
                    case COOLANT_VENT -> "vent_" + (active ? "output" : "input");
                    case HEATER_VENT -> "heater/vent_" + (active ? "output" : "input");
                    case REDSTONE_PORT -> "redstone_" + (active ? "2" : "1");
                    case VACUUM_LASER -> "laser_front";
                    case PLASMA_NOZZLE -> "nozzle";
                    default -> id.substring((role.vacuum() ? "vacuum_chamber_" : "particle_chamber_").length());
                };
                cube(id + (active ? "_active" : ""), "block/" + texture);
            }
        }
        facing(id, "", toggled ? "_active" : "", AbstractChamberBlock.FACING, AbstractChamberBlock.ACTIVE);
    }

    private void liquefier() {
        for (String name : new String[]{"copper", "neodymium", "samarium_cobalt"}) {
            String id = name + "_liquefier_compressor";
            models().cubeBottomTop(id, ResourceLocation.parse("nuclearcraftneohaul:block/heat_exchanger/controller_side"),
                    modLoc("block/liquefier/compressor_bottom"), modLoc("block/liquefier/compressor_" + name));
            single(id, modelFile(id));
        }
        cube("liquefier_nozzle", "block/liquefier/nozzle");
        single("liquefier_nozzle", modelFile("liquefier_nozzle"));
        for (String id : new String[]{"controller", "controller_on", "fluid_port_in", "fluid_port_out", "energy_port"}) {
            String front = id.equals("controller") ? "controller_front_off" : id.equals("controller_on") ? "controller_front_on" : id;
            models().cube("liquefier_" + id, modLoc("block/liquefier/controller_bottom"), modLoc("block/liquefier/controller_top"),
                    modLoc("block/liquefier/" + front), modLoc("block/liquefier/controller_back"),
                    modLoc("block/liquefier/controller_side"), modLoc("block/liquefier/controller_side"))
                    .texture("particle", modLoc("block/liquefier/controller_side"));
        }
        facing("liquefier_controller", "", "_on",
                com.nred.nuclearcraft.registration.BlockRegistration.FACING_ALL, com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE);
        facing("liquefier_fluid_port", "_in", "_out",
                com.nred.nuclearcraft.registration.BlockRegistration.FACING_ALL, com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE);
        facing("liquefier_energy_port", "", "",
                com.nred.nuclearcraft.registration.BlockRegistration.FACING_ALL, com.nred.nuclearcraft.registration.BlockRegistration.ACTIVE);
    }

    private void facedCube(String id, String side, String front) {
        models().cube(id, ResourceLocation.parse(side), ResourceLocation.parse(side), ResourceLocation.parse(front),
                ResourceLocation.parse(side), ResourceLocation.parse(side), ResourceLocation.parse(side)).texture("particle", side);
    }

    private void cube(String id, String texture) { models().cubeAll(id, modLoc(texture)); }
    private Block block(String id) { return BuiltInRegistries.BLOCK.get(modLoc(id)); }
    private ModelFile modelFile(String id) { return models().getExistingFile(modLoc("block/" + id)); }
    private static int x(Direction facing) { return facing == Direction.UP ? 270 : facing == Direction.DOWN ? 90 : 0; }
    private static int y(Direction facing) { return switch (facing) { case EAST -> 90; case SOUTH -> 180; case WEST -> 270; default -> 0; }; }
    private static ConfiguredModel configured(ModelFile model, int x, int y) {
        return new ConfiguredModel(model, x, y, false);
    }
    private void single(String id, ModelFile model) { simpleBlock(block(id), model); }

    private void toggle(String id, String property, String off, String on) {
        var block = block(id);
        var active = (BooleanProperty) block.getStateDefinition().getProperty(property);
        var variants = getVariantBuilder(block);
        variants.partialState().with(active, false).addModels(new ConfiguredModel(modelFile(id + off)));
        variants.partialState().with(active, true).addModels(new ConfiguredModel(modelFile(id + on)));
    }

    private void facing(String id, String off, String on, DirectionProperty facing, BooleanProperty active) {
        var block = block(id);
        getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[]{
                configured(modelFile(id + (state.getValue(active) ? on : off)), x(state.getValue(facing)), y(state.getValue(facing)))});
    }

    private void horizontal(String id, String off, String on, String property) {
        var block = block(id);
        var active = (BooleanProperty) block.getStateDefinition().getProperty(property);
        getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[]{
                configured(modelFile(id + (state.getValue(active) ? on : off)), 0, y(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))});
    }
}
