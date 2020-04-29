package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import net.minecraft.state.AbstractState;
import net.minecraft.state.property.Property;

public class FluidStateImpl extends AbstractState<Fluid, FluidState> implements FluidState {
	public FluidStateImpl(Fluid fluid, ImmutableMap<Property<?>, Comparable<?>> properties) {
		super(fluid, properties);
	}

	@Override
	public Fluid getFluid() {
		return this.owner;
	}
}
