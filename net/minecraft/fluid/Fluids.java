/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.fluid;

import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.registry.Registry;

public class Fluids {
    public static final Fluid EMPTY = Fluids.register("empty", new EmptyFluid());
    public static final BaseFluid FLOWING_WATER = Fluids.register("flowing_water", new WaterFluid.Flowing());
    public static final BaseFluid WATER = Fluids.register("water", new WaterFluid.Still());
    public static final BaseFluid FLOWING_LAVA = Fluids.register("flowing_lava", new LavaFluid.Flowing());
    public static final BaseFluid LAVA = Fluids.register("lava", new LavaFluid.Still());

    private static <T extends Fluid> T register(String string, T fluid) {
        return (T)Registry.register(Registry.FLUID, string, fluid);
    }

    static {
        for (Fluid fluid : Registry.FLUID) {
            for (FluidState fluidState : fluid.getStateFactory().getStates()) {
                Fluid.STATE_IDS.add(fluidState);
            }
        }
    }
}

