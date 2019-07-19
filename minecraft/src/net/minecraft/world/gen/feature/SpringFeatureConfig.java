package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;

public class SpringFeatureConfig implements FeatureConfig {
	public final FluidState state;

	public SpringFeatureConfig(FluidState state) {
		this.state = state;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), FluidState.serialize(ops, this.state).getValue())));
	}

	public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic) {
		FluidState fluidState = (FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState());
		return new SpringFeatureConfig(fluidState);
	}
}
