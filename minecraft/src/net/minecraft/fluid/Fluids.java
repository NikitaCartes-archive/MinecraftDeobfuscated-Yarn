package net.minecraft.fluid;

import net.minecraft.util.registry.Registry;

public class Fluids {
	public static final Fluid field_15906 = register("empty", new EmptyFluid());
	public static final FlowableFluid FLOWING_WATER = register("flowing_water", new WaterFluid.Flowing());
	public static final FlowableFluid WATER = register("water", new WaterFluid.Still());
	public static final FlowableFluid FLOWING_LAVA = register("flowing_lava", new LavaFluid.Flowing());
	public static final FlowableFluid LAVA = register("lava", new LavaFluid.Still());

	private static <T extends Fluid> T register(String id, T value) {
		return Registry.register(Registry.FLUID, id, value);
	}

	static {
		for (Fluid fluid : Registry.FLUID) {
			for (FluidState fluidState : fluid.getStateManager().getStates()) {
				Fluid.STATE_IDS.add(fluidState);
			}
		}
	}
}
