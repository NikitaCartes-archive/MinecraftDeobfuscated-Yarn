package net.minecraft.fluid;

import net.minecraft.util.registry.Registry;

public class Fluids {
	public static final Fluid EMPTY = register("empty", new EmptyFluid());
	public static final BaseFluid FLOWING_WATER = register("flowing_water", new WaterFluid.Flowing());
	public static final BaseFluid WATER = register("water", new WaterFluid.Still());
	public static final BaseFluid FLOWING_LAVA = register("flowing_lava", new LavaFluid.Flowing());
	public static final BaseFluid LAVA = register("lava", new LavaFluid.Still());

	private static <T extends Fluid> T register(String string, T fluid) {
		return Registry.register(Registry.FLUID, string, fluid);
	}

	static {
		for (Fluid fluid : Registry.FLUID) {
			for (FluidState fluidState : fluid.getStateFactory().getStates()) {
				Fluid.STATE_IDS.add(fluidState);
			}
		}
	}
}
