package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;

public class SpringFeatureConfig implements FeatureConfig {
	public final FluidState field_13850;

	public SpringFeatureConfig(FluidState fluidState) {
		this.field_13850 = fluidState;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), FluidState.serialize(dynamicOps, this.field_13850).getValue()))
		);
	}

	public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic) {
		FluidState fluidState = (FluidState)dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.field_15906.method_15785());
		return new SpringFeatureConfig(fluidState);
	}
}
