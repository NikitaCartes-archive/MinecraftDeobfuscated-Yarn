package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.GenerationStep;

public class CarvingMaskDecoratorConfig implements DecoratorConfig {
	protected final GenerationStep.Carver step;
	protected final float probability;

	public CarvingMaskDecoratorConfig(GenerationStep.Carver carver, float f) {
		this.step = carver;
		this.probability = f;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("step"),
					dynamicOps.createString(this.step.toString()),
					dynamicOps.createString("probability"),
					dynamicOps.createFloat(this.probability)
				)
			)
		);
	}

	public static CarvingMaskDecoratorConfig deserialize(Dynamic<?> dynamic) {
		GenerationStep.Carver carver = GenerationStep.Carver.valueOf(dynamic.get("step").asString(""));
		float f = dynamic.get("probability").asFloat(0.0F);
		return new CarvingMaskDecoratorConfig(carver, f);
	}
}
