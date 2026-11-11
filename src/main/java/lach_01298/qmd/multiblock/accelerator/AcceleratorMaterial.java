package lach_01298.qmd.multiblock.accelerator;

import lach_01298.qmd.config.AcceleratorConfig;

/** Legacy RF/magnet operating points, shared by validation, runtime and JEI tooltips. */
public enum AcceleratorMaterial {

    COPPER("copper", 200, .2, .7, 300, 500, 1000, 350),
    MAGNESIUM_DIBORIDE("magnesium_diboride", 500, .5, .9, 540, 1000, 2000, 39),
    NIOBIUM_TIN("niobium_tin", 1000, 1, 1.1, 1020, 2000, 4000, 18),
    NIOBIUM_TITANIUM("niobium_titanium", 2000, 2, 1.3, 1980, 4000, 8000, 10),
    BSCCO("bscco", 4000, 4, 1.5, 3900, 8000, 16000, 110),
    ALUMINIUM("aluminium", 100, .1, .5, 180, 250, 500, 350),
    SSFAF("ssfaf", 1500, 1.5, 1.2, 1500, 3000, 6000, 56),
    YBCO("ybco", 3000, 3, 1.4, 2940, 6000, 12000, 95),
    HBCCO("hbcco", 750, .75, 1, 780, 1500, 3000, 133);

    public final String id;
    public final int voltage, heat, cavityPower, magnetPower, maxTemperature;
    public final double strength, efficiency;

    public AcceleratorConfig.MaterialSettings settings() {
        return AcceleratorConfig.MATERIALS.get(this);
    }

    AcceleratorMaterial(String id, int voltage, double strength, double efficiency, int heat, int cavityPower,
        int magnetPower, int maxTemperature) {
        this.id = id;
        this.voltage = voltage;
        this.strength = strength;
        this.efficiency = efficiency;
        this.heat = heat;
        this.cavityPower = cavityPower;
        this.magnetPower = magnetPower;
        this.maxTemperature = maxTemperature;
    }
}
