package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.fluid.FluidState;

public enum FluidRayTraceMode {
	NONE(fluidState -> false),
	field_1345(FluidState::isStill),
	field_1347(fluidState -> !fluidState.isEmpty());

	public final Predicate<FluidState> predicate;

	private FluidRayTraceMode(Predicate<FluidState> predicate) {
		this.predicate = predicate;
	}
}
