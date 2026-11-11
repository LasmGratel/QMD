package lach_01298.qmd.multiblock.liquefier;

import com.nred.nuclearcraft.block_entity.hx.*;
import com.nred.nuclearcraft.block_entity.internal.energy.EnergyStorage;
import com.nred.nuclearcraft.block_entity.internal.fluid.Tank;
import com.nred.nuclearcraft.multiblock.hx.*;
import com.nred.nuclearcraft.util.LambdaHelper;
import com.nred.nuclearcraft.util.ValueTracker;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import lach_01298.qmd.block.entity.liquefier.*;
import lach_01298.qmd.config.LiquefierConfig;
import lach_01298.qmd.recipe.LiquefierRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static com.nred.nuclearcraft.NuclearcraftNeohaul.MODID;

/** Joule-Thomson liquefaction using the shared Neohaul HX cuboid, shell and tube-flow lifecycle. */
public final class LiquefierLogic extends HeatExchangerLogic {

    public final EnergyStorage energyStorage = new EnergyStorage(1000);
    public final List<Tank> gasTanks = List.of(new Tank(640, null), new Tank(10, null));
    public double pressure, energyEfficiency, heatEfficiency, pressureEfficiency;
    public double powerUse, liquidOut, coolantOut, powerUseFP, liquidOutFP, coolantOutFP;
    private final ValueTracker powerTracker = new ValueTracker(), liquidTracker = new ValueTracker(),
        coolantTracker = new ValueTracker();
    private double gasRemainder, coolantRemainder, energyCredit;
    private ResourceLocation lastGasRecipe, lastCoolantRecipe;
    private LiquefierRecipe gasRecipe, coolantRecipe;

    public LiquefierLogic(HeatExchanger multiblock) {
        super(multiblock);
    }

    public LiquefierLogic(HeatExchangerLogic previous) {
        super(previous);
    }

    @Override
    public String getID() {
        return "liquefier";
    }

    public LiquefierControllerBlockEntity controller() {
        return getParts(LiquefierControllerBlockEntity.class).stream()
            .findFirst()
            .orElse(null);
    }

    @Override
    protected void setupExchanger() {
        int volume = multiblock.getExteriorVolume();
        gasTanks.getFirst()
            .setCapacity(capacity(LiquefierConfig.GAS_DENSITY.get(), volume));
        gasTanks.get(1)
            .setCapacity(capacity(LiquefierConfig.LIQUID_DENSITY.get(), volume));
        multiblock.shellTanks.getFirst()
            .setCapacity(capacity(HeatExchanger.BASE_MAX_INPUT, volume));
        multiblock.shellTanks.get(1)
            .setCapacity(capacity(HeatExchanger.BASE_MAX_OUTPUT, volume));
        energyStorage.setStorageCapacity((long) LiquefierConfig.ENERGY_DENSITY.get() * volume);
        energyStorage.setMaxTransfer(energyStorage.getMaxEnergyStoredLong());
        refreshAllowedFluids();
        Map<Long, HeatExchangerTubeEntity> tubeMap = getPartMap(HeatExchangerTubeEntity.class);
        for (HeatExchangerTubeNetwork network : multiblock.networks) network.setFlowStats(tubeMap);
        multiblock.totalNetworkCount = multiblock.networks.size();
        int nozzles = getPartMap(LiquefierNozzleBlockEntity.class).size();
        energyEfficiency = heatEfficiency = 1;
        for (LiquefierCompressorBlockEntity compressor : getParts(LiquefierCompressorBlockEntity.class)) {
            energyEfficiency *= Math.pow(compressor.energyEfficiency(), 1D / nozzles);
            heatEfficiency *= Math.pow(compressor.heatEfficiency(), 1D / nozzles);
        }
        pressure = 10D * Math.pow(getPartMap(LiquefierCompressorBlockEntity.class).size() / (double) nozzles, 2);
    }

    private static int capacity(int density, int volume) {
        return (int) Math.min(Integer.MAX_VALUE, (long) density * volume);
    }

    private Set<ResourceLocation> validFluids(RecipeType<LiquefierRecipe> type) {
        return getWorld().getRecipeManager()
            .getAllRecipesFor(type)
            .stream()
            .flatMap(
                holder -> Arrays.stream(
                    holder.value()
                        .ingredient()
                        .getStacks()))
            .map(stack -> BuiltInRegistries.FLUID.getKey(stack.getFluid()))
            .collect(Collectors.toSet());
    }

    private void refreshAllowedFluids() {
        gasTanks.getFirst()
            .setAllowedFluids(validFluids(QMDRecipes.LIQUEFIER_GAS_TYPE.get()));
        multiblock.shellTanks.getFirst()
            .setAllowedFluids(validFluids(QMDRecipes.LIQUEFIER_COOLANT_TYPE.get()));
    }

    // HX inlet processors must not pre-consume coolant. This variant processes both circuits atomically below.
    @Override
    public void refreshAll() {}

    @Override
    public void refreshRecipe() {}

    @Override
    public void refreshActivity() {}

    @Override
    public void onExchangerBroken() {
        super.onExchangerBroken();
        energyEfficiency = heatEfficiency = pressure = pressureEfficiency = 0;
        resetStats();
    }

    private boolean invalid(BlockPos pos, String key) {
        if (pos == null) multiblock.setLastError("qmd.multiblock_validation.liquefier." + key);
        else multiblock.setLastError(pos, "qmd.multiblock_validation.liquefier." + key);
        return false;
    }

    private boolean isLiquefierShellInterior(BlockPos pos) {
        return multiblock.isInInterior(pos) && pos.getY() > multiblock.getMinInteriorY();
    }

    @Override
    public boolean isMachineWhole() {
        if (containsBlacklistedPart()) {
            return false;
        }

        multiblock.masterShellInlet = null;
        multiblock.networks.clear();

        multiblock.shellRecipe = Fluids.EMPTY;

        multiblock.totalNetworkCount = 0;

        Map<Long, HeatExchangerTubeEntity> tubeMap = getPartMap(HeatExchangerTubeEntity.class);
        Map<Long, HeatExchangerBaffleEntity> baffleMap = getPartMap(HeatExchangerBaffleEntity.class);

        for (HeatExchangerTubeEntity tube : tubeMap.values()) {
            tube.tubeFlow = null;
            tube.shellFlow = null;
        }

        Map<Long, HeatExchangerInletEntity> inletMap = getPartMap(HeatExchangerInletEntity.class);
        Map<Long, HeatExchangerOutletEntity> outletMap = getPartMap(HeatExchangerOutletEntity.class);

        for (HeatExchangerInletEntity inlet : inletMap.values()) {
            inlet.isMasterInlet = false;
            inlet.network = null;
        }

        for (HeatExchangerOutletEntity outlet : outletMap.values()) {
            outlet.network = null;
        }

        Map<Long, LiquefierCompressorBlockEntity> compressorMap = getPartMap(LiquefierCompressorBlockEntity.class);
        Map<Long, LiquefierNozzleBlockEntity> nozzleMap = getPartMap(LiquefierNozzleBlockEntity.class);
        for (LiquefierNozzleBlockEntity nozzle : nozzleMap.values()) nozzle.network = null;
        if (nozzleMap.isEmpty() || compressorMap.size() < nozzleMap.size())
            return invalid(null, "invalid_compressor_amount");
        for (BlockPos pos : multiblock.getInteriorPlaneMinY(0, 0, 0, 0, 0)) {
            if (!getWorld().getBlockState(pos)
                .isAir()) return invalid(pos, "must_be_empty_layer");
        }
        for (BlockPos pos : multiblock.getInteriorPlaneMinY(1, 0, 0, 0, 0)) {
            if (!baffleMap.containsKey(pos.asLong()) && !nozzleMap.containsKey(pos.asLong()))
                return invalid(pos, "must_be_baffle_or_nozzle");
        }
        for (LiquefierNozzleBlockEntity nozzle : nozzleMap.values()) {
            if (nozzle.getBlockPos()
                .getY() != multiblock.getMinInteriorY() + 1)
                return invalid(nozzle.getBlockPos(), "must_be_baffle_or_nozzle");
        }
        for (LiquefierCompressorBlockEntity compressor : compressorMap.values()) {
            if (compressor.getBlockPos()
                .getY() != multiblock.getMaxY()) return invalid(compressor.getBlockPos(), "compressor_must_be_in_roof");
        }
        boolean gasInput = false, gasOutput = false;
        for (LiquefierFluidPortBlockEntity port : getParts(LiquefierFluidPortBlockEntity.class)) {
            gasOutput |= port.isOutput();
            gasInput |= !port.isOutput();
        }
        if (!gasInput) return invalid(null, "must_have_fluid_input");
        if (!gasOutput) return invalid(null, "must_have_fluid_output");
        if (getPartMap(LiquefierEnergyPortBlockEntity.class).isEmpty()) return invalid(null, "must_have_energy_port");
        for (HeatExchangerInletEntity inlet : inletMap.values()) {
            if (inlet.getBlockPos()
                .getY() < multiblock.getMinInteriorY() + 2)
                return invalid(inlet.getBlockPos(), "inlet_outlet_must_be_in_top_section");
        }
        for (HeatExchangerOutletEntity outlet : outletMap.values()) {
            if (outlet.getBlockPos()
                .getY() < multiblock.getMinInteriorY() + 2)
                return invalid(outlet.getBlockPos(), "inlet_outlet_must_be_in_top_section");
        }

        LongSet tubeInletPosLongSet = new LongOpenHashSet();
        LongSet tubeOutletPosLongSet = new LongOpenHashSet();

        LongSet visitedTubePosLongSet = new LongOpenHashSet();

        for (long tubePosLong : tubeMap.keySet()) {
            if (!visitedTubePosLongSet.contains(tubePosLong)) {
                HeatExchangerTubeNetwork network = new HeatExchangerTubeNetwork(this);

                LongList tubePosLongStack = new LongArrayList();
                LongSupplier popTubePosLong = () -> tubePosLongStack.removeLong(tubePosLongStack.size() - 1);

                visitedTubePosLongSet.add(tubePosLong);
                tubePosLongStack.add(tubePosLong);

                while (!tubePosLongStack.isEmpty()) {
                    long nextPosLong = popTubePosLong.getAsLong();
                    BlockPos nextPos = BlockPos.of(nextPosLong);

                    HeatExchangerTubeEntity tube = tubeMap.get(nextPosLong);
                    HeatExchangerTubeSetting[] tubeSettings = tube.settings;

                    for (int i = 0; i < 6; ++i) {
                        if (tubeSettings[i].isOpen()) {
                            Direction dir = Direction.values()[i];
                            long offsetPosLong = nextPos.relative(dir)
                                .asLong();

                            LiquefierCompressorBlockEntity inlet = compressorMap.get(offsetPosLong);
                            if (inlet != null) {
                                tubeInletPosLongSet.add(offsetPosLong);
                                network.inletPosLongSet.add(offsetPosLong);
                                continue;
                            }

                            LiquefierNozzleBlockEntity outlet = nozzleMap.get(offsetPosLong);
                            if (outlet != null) {
                                tubeOutletPosLongSet.add(offsetPosLong);
                                network.outletPosLongSet.add(offsetPosLong);
                                outlet.network = network;
                                continue;
                            }

                            HeatExchangerTubeEntity other = tubeMap.get(offsetPosLong);
                            if (other != null && other.getTubeSetting(dir.getOpposite())
                                .isOpen()) {
                                if (!visitedTubePosLongSet.contains(offsetPosLong)) {
                                    visitedTubePosLongSet.add(offsetPosLong);
                                    tubePosLongStack.add(offsetPosLong);
                                }
                                continue;
                            }

                            multiblock
                                .setLastError(nextPos, MODID + ".multiblock_validation.heat_exchanger.dangling_tube");
                            return false;
                        }
                    }

                    network.tubePosLongSet.add(nextPosLong);
                }

                multiblock.networks.add(network);
            }
        }

        if (multiblock.networks.size() != nozzleMap.size() || tubeOutletPosLongSet.size() != nozzleMap.size()
            || tubeInletPosLongSet.size() != compressorMap.size()) return invalid(null, "invalid_nozzle_network");
        for (HeatExchangerTubeNetwork network : multiblock.networks) {
            if (network.inletPosLongSet.isEmpty() || network.outletPosLongSet.size() != 1) {
                for (long posLong : network.tubePosLongSet) {
                    multiblock.setLastError(
                        BlockPos.of(posLong),
                        MODID + ".multiblock_validation.heat_exchanger.invalid_network");
                    return false;
                }
            }
        }

        LongSet shellInletPosLongSet = new LongRBTreeSet(), shellOutletPosLongSet = new LongOpenHashSet();

        for (long inletPosLong : inletMap.keySet()) {
            if (!tubeInletPosLongSet.contains(inletPosLong)) {
                shellInletPosLongSet.add(inletPosLong);
            }
        }

        for (long outletPosLong : outletMap.keySet()) {
            if (!tubeOutletPosLongSet.contains(outletPosLong)) {
                shellOutletPosLongSet.add(outletPosLong);
            }
        }

        for (HeatExchangerTubeNetwork network : multiblock.networks) {
            network.setTubeFlows(tubeMap);
        }

        if (shellInletPosLongSet.isEmpty() || shellOutletPosLongSet.isEmpty()) {
            multiblock.setLastError(MODID + ".multiblock_validation.heat_exchanger.invalid_shell");
            return false;
        }

        LongSet shellPosLongSet = new LongOpenHashSet();
        LongList shellPosLongStack = new LongArrayList();
        LongSupplier popShellPosLong = () -> shellPosLongStack.removeLong(shellPosLongStack.size() - 1);

        for (long inletPosLong : shellInletPosLongSet) {
            BlockPos inletPos = BlockPos.of(inletPosLong);
            long clampedPosLong = multiblock.getClampedInteriorCoord(inletPos)
                .asLong();
            if (baffleMap.containsKey(clampedPosLong)) {
                multiblock.setLastError(inletPos, MODID + ".multiblock_validation.heat_exchanger.blocked_inlet");
                return false;
            }

            shellPosLongSet.add(clampedPosLong);
            shellPosLongStack.add(clampedPosLong);

            if (multiblock.masterShellInlet == null) {
                multiblock.masterShellInlet = inletMap.get(inletPosLong);
                multiblock.masterShellInlet.isMasterInlet = true;
            }
        }

        for (long outletPosLong : shellOutletPosLongSet) {
            BlockPos outletPos = BlockPos.of(outletPosLong);
            if (baffleMap.containsKey(
                multiblock.getClampedInteriorCoord(outletPos)
                    .asLong())) {
                multiblock.setLastError(outletPos, MODID + ".multiblock_validation.heat_exchanger.blocked_outlet");
                return false;
            }
        }

        while (!shellPosLongStack.isEmpty()) {
            long nextPosLong = popShellPosLong.getAsLong();
            BlockPos nextPos = BlockPos.of(nextPosLong);

            if (!tubeMap.containsKey(nextPosLong) && !getWorld().getBlockState(nextPos)
                .isAir()) {
                multiblock.setLastError(
                    "zerocore.api.nc.multiblock.validation.invalid_part_for_interior",
                    nextPos,
                    nextPos.getX(),
                    nextPos.getY(),
                    nextPos.getZ());
                return false;
            }

            HeatExchangerTubeSetting[] tubeSettings = tubeMap.containsKey(nextPosLong)
                ? tubeMap.get(nextPosLong).settings
                : null;

            for (int i = 0; i < 6; ++i) {
                if (tubeSettings == null || !tubeSettings[i].isBaffle()) {
                    Direction dir = Direction.values()[i];
                    BlockPos offsetPos = nextPos.relative(dir);
                    long offsetPosLong = offsetPos.asLong();

                    if (isLiquefierShellInterior(offsetPos) && !nozzleMap.containsKey(offsetPosLong)
                        && !shellPosLongSet.contains(offsetPosLong)
                        && (!baffleMap.containsKey(offsetPosLong)
                            || (tubeMap.containsKey(offsetPosLong) && !tubeMap.get(offsetPosLong)
                                .getTubeSetting(dir.getOpposite())
                                .isBaffle()))) {
                        shellPosLongSet.add(offsetPosLong);
                        shellPosLongStack.add(offsetPosLong);
                    }
                }
            }
        }

        if (shellPosLongSet.size() + baffleMap.size() + nozzleMap.size()
            != multiblock.getInteriorLengthX() * (multiblock.getInteriorLengthY() - 1)
                * multiblock.getInteriorLengthZ()) {
            multiblock.setLastError(MODID + ".multiblock_validation.heat_exchanger.blocked_shell");
            return false;
        }

        Long2ObjectMap<ObjectSet<Vec3>> flowMap = HeatExchangerFlowHelper.getFlowMap(
            shellInletPosLongSet,
            shellOutletPosLongSet,
            x -> LambdaHelper.let(x.asLong(), y -> tubeMap.containsKey(y) ? tubeMap.get(y).settings : null),
            x -> !x.isBaffle(),
            (x, y) -> {
                long posLong = x.asLong();
                return shellPosLongSet.contains(posLong) && (!tubeMap.containsKey(posLong) || !tubeMap.get(posLong)
                    .getTubeSetting(y.getOpposite())
                    .isBaffle());
            },
            x -> shellOutletPosLongSet.contains(x.asLong()));

        for (Long2ObjectMap.Entry<ObjectSet<Vec3>> entry : flowMap.long2ObjectEntrySet()) {
            long posLong = entry.getLongKey();
            if (tubeMap.containsKey(posLong)) {
                HeatExchangerTubeEntity tube = tubeMap.get(posLong);
                if (tube.tubeFlow != null) {
                    tube.shellFlow = entry.getValue()
                        .stream()
                        .reduce(Vec3.ZERO, Vec3::add)
                        .normalize();
                }
            }
        }

        for (IHeatExchangerController<?> controller : getParts(IHeatExchangerController.class)) {
            controller.setIsRenderer(false);
        }
        for (IHeatExchangerController<?> controller : getParts(IHeatExchangerController.class)) {
            if (multiblock.shouldSpecialRender) {
                controller.setIsRenderer(true);
            }
            break;
        }

        return true;
    }

    private void resetStats() {
        multiblock.tubeInputRate = multiblock.shellInputRate = multiblock.heatTransferRate = multiblock.totalTempDiff = 0;
        multiblock.activeNetworkCount = multiblock.activeTubeCount = multiblock.activeContactCount = 0;
        powerUse = liquidOut = coolantOut = 0;
    }

    @Override
    public boolean onUpdateServer() {
        resetStats();
        refreshAllowedFluids();
        setIsExchangerOn();
        boolean changed = false;
        if (multiblock.isExchangerOn) changed = processAvailable();
        multiblock.tubeInputRateFP = multiblock.tubeInputRateTracker.update(multiblock.tubeInputRate);
        multiblock.shellInputRateFP = multiblock.shellInputRateTracker.update(multiblock.shellInputRate);
        multiblock.heatTransferRateFP = multiblock.heatTransferRateTracker.update(multiblock.heatTransferRate);
        powerUseFP = powerTracker.update(powerUse);
        liquidOutFP = liquidTracker.update(liquidOut);
        coolantOutFP = coolantTracker.update(coolantOut);
        if (controller() != null) {
            multiblock.sendMultiblockUpdatePacketToListeners();
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk(
                (net.minecraft.server.level.ServerLevel) getWorld(),
                new net.minecraft.world.level.ChunkPos(controller().getBlockPos()),
                lach_01298.qmd.network.LiquefierRenderPayload.from(this));
        }
        return changed;
    }

    private RecipeHolder<LiquefierRecipe> findRecipe(RecipeType<LiquefierRecipe> type, Tank input) {
        return getWorld().getRecipeManager()
            .getAllRecipesFor(type)
            .stream()
            .filter(
                holder -> holder.value()
                    .accepts(input.getFluid()))
            .sorted(
                Comparator.comparing(
                    holder -> holder.id()
                        .toString()))
            .findFirst()
            .orElse(null);
    }

    /** Requeries the live recipe manager so datapack replacement and recipe removal take effect immediately. */
    public boolean processAvailable() {
        if (!multiblock.isAssembled()) return false;
        RecipeHolder<LiquefierRecipe> gas = findRecipe(QMDRecipes.LIQUEFIER_GAS_TYPE.get(), gasTanks.getFirst());
        RecipeHolder<LiquefierRecipe> coolant = findRecipe(
            QMDRecipes.LIQUEFIER_COOLANT_TYPE.get(),
            multiblock.shellTanks.getFirst());
        if (gas == null || coolant == null) return false;
        if (!gas.id()
            .equals(lastGasRecipe) || gasRecipe != null && gas.value() != gasRecipe) {
            gasRemainder = 0;
            lastGasRecipe = gas.id();
        }
        if (!coolant.id()
            .equals(lastCoolantRecipe) || coolantRecipe != null && coolant.value() != coolantRecipe) {
            coolantRemainder = 0;
            lastCoolantRecipe = coolant.id();
        }
        gasRecipe = gas.value();
        coolantRecipe = coolant.value();
        pressureEfficiency = 1D - 1D / (gasRecipe.pressureCoefficient() * pressure + 1D);
        multiblock.totalTempDiff = (gasRecipe.outputTemperature() - coolantRecipe.outputTemperature()) / 2D;
        if (coolantRecipe.inputTemperature() > gasRecipe.inputTemperature()
            || coolantRecipe.outputTemperature() > gasRecipe.outputTemperature()
            || pressureEfficiency <= 0
            || energyEfficiency <= 0
            || heatEfficiency <= 0) return false;

        double gasHeat = gasRecipe.heat() / (heatEfficiency * pressureEfficiency);
        double gasEnergy = gasRecipe.energy() / (energyEfficiency * pressureEfficiency);
        if (!Double.isFinite(gasHeat) || gasHeat <= 0 || !Double.isFinite(gasEnergy) || gasEnergy < 0) return false;
        double maxNozzleRecipes = LiquefierConfig.NOZZLE_SPEED.get() * pressure;
        double heat = 0;
        for (LiquefierNozzleBlockEntity nozzle : getParts(LiquefierNozzleBlockEntity.class)) {
            if (nozzle.network != null) {
                double proposal = Math
                    .min(gasHeat * maxNozzleRecipes, multiblock.totalTempDiff * nozzle.network.baseCoolingMultiplier);
                heat += Math.max(0, proposal);
                if (proposal > 0) {
                    multiblock.activeNetworkCount++;
                    multiblock.activeTubeCount += nozzle.network.usefulTubeCount;
                }
            }
        }
        double gasLimit = Math.max(0, maxRecipes(gasRecipe, gasTanks) - gasRemainder);
        double coolantLimit = Math.max(0, maxRecipes(coolantRecipe, multiblock.shellTanks) - coolantRemainder);
        heat = Math.min(heat, Math.min(gasHeat * gasLimit, coolantRecipe.heat() * coolantLimit));
        if (gasEnergy > 0)
            heat = Math.min(heat, gasHeat * (energyStorage.getEnergyStoredLong() + energyCredit) / gasEnergy);
        if (!Double.isFinite(heat) || heat <= 0) return false;

        double gasRate = heat / gasHeat, coolantRate = heat / coolantRecipe.heat();
        double gasProgress = gasRemainder + gasRate, coolantProgress = coolantRemainder + coolantRate;
        int gasCount = Math.min(maxRecipes(gasRecipe, gasTanks), (int) Math.floor(gasProgress + 1E-10D));
        int coolantCount = Math
            .min(maxRecipes(coolantRecipe, multiblock.shellTanks), (int) Math.floor(coolantProgress + 1E-10D));
        double cost = Math.min(gasRate * gasEnergy, energyStorage.getEnergyStoredLong() + energyCredit);
        long energyCharge = cost <= energyCredit ? 0 : (long) Math.ceil(cost - energyCredit);
        if (energyCharge < 0 || energyCharge > energyStorage.getEnergyStoredLong()) return false;
        // A fractional FE is prepaid, so arbitrarily small recipes cannot create output for free.
        // Keep the credit across ticks rather than charging/truncating each fractional tick separately.
        energyStorage.changeEnergyStored(-energyCharge);
        energyCredit = fraction(energyCredit + energyCharge - cost);
        gasRemainder = Math.max(0, gasProgress - gasCount);
        coolantRemainder = Math.max(0, coolantProgress - coolantCount);
        convert(gasRecipe, gasTanks, gasCount);
        convert(coolantRecipe, multiblock.shellTanks, coolantCount);
        powerUse = energyCharge;
        multiblock.tubeInputRate = (double) gasCount * gasRecipe.amount();
        liquidOut = (double) gasCount * gasRecipe.output()
            .getAmount();
        multiblock.shellInputRate = (double) coolantCount * coolantRecipe.amount();
        coolantOut = (double) coolantCount * coolantRecipe.output()
            .getAmount();
        multiblock.heatTransferRate = heat;
        return true;
    }

    private static int maxRecipes(LiquefierRecipe recipe, List<Tank> tanks) {
        Tank in = tanks.getFirst(), out = tanks.get(1);
        FluidStack result = recipe.output();
        if (!out.isEmpty() && !FluidStack.isSameFluidSameComponents(out.getFluid(), result)) return 0;
        return Math.min(
            in.getFluidAmount() / recipe.amount(),
            (out.getCapacity() - out.getFluidAmount()) / result.getAmount());
    }

    private static void convert(LiquefierRecipe recipe, List<Tank> tanks, int count) {
        if (count <= 0) return;
        tanks.getFirst()
            .changeFluidAmount(-count * recipe.amount());
        FluidStack output = recipe.output();
        output.setAmount(
            output.getAmount() * count + tanks.get(1)
                .getFluidAmount());
        tanks.get(1)
            .setFluidStored(output);
    }

    @Override
    public void clearAllMaterial() {
        gasTanks.forEach(tank -> tank.setFluidStored(FluidStack.EMPTY));
        multiblock.shellTanks.forEach(tank -> tank.setFluidStored(FluidStack.EMPTY));
        energyStorage.setEnergyStored(0);
        gasRemainder = coolantRemainder = energyCredit = 0;
        resetStats();
    }

    @Override
    public void writeToLogicTag(CompoundTag tag, HolderLookup.Provider registries, ISyncableEntity.SyncReason reason) {
        energyStorage.writeToNBT(tag, registries, "energyStorage");
        for (int i = 0; i < 2; i++) gasTanks.get(i)
            .writeToNBT(tag, registries, "gasTank" + i);
        tag.putDouble("pressure", pressure);
        tag.putDouble("pressureEfficiency", pressureEfficiency);
        tag.putDouble("energyEfficiency", energyEfficiency);
        tag.putDouble("heatEfficiency", heatEfficiency);
        tag.putDouble("gasRemainder", gasRemainder);
        tag.putDouble("coolantRemainder", coolantRemainder);
        tag.putDouble("energyCredit", energyCredit);
        if (lastGasRecipe != null) tag.putString("gasRecipe", lastGasRecipe.toString());
        if (lastCoolantRecipe != null) tag.putString("coolantRecipe", lastCoolantRecipe.toString());
    }

    @Override
    public void readFromLogicTag(CompoundTag tag, HolderLookup.Provider registries, ISyncableEntity.SyncReason reason) {
        energyStorage.readFromNBT(tag, registries, "energyStorage");
        for (int i = 0; i < 2; i++) gasTanks.get(i)
            .readFromNBT(tag, registries, "gasTank" + i);
        pressure = tag.getDouble("pressure");
        pressureEfficiency = tag.getDouble("pressureEfficiency");
        energyEfficiency = tag.getDouble("energyEfficiency");
        heatEfficiency = tag.getDouble("heatEfficiency");
        gasRemainder = fraction(tag.getDouble("gasRemainder"));
        coolantRemainder = fraction(tag.getDouble("coolantRemainder"));
        energyCredit = fraction(tag.getDouble("energyCredit"));
        lastGasRecipe = ResourceLocation.tryParse(tag.getString("gasRecipe"));
        lastCoolantRecipe = ResourceLocation.tryParse(tag.getString("coolantRecipe"));
    }

    private static double fraction(double value) {
        return Double.isFinite(value) ? Math.clamp(value, 0D, Math.nextDown(1D)) : 0D;
    }
}
