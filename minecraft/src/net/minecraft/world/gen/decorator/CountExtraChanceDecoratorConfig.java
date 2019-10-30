package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountExtraChanceDecoratorConfig implements DecoratorConfig {
	public final int count;
	public final float extraChance;
	public final int extraCount;

	public CountExtraChanceDecoratorConfig(int count, float extraChance, int extraCount) {
		this.count = count;
		this.extraChance = extraChance;
		this.extraCount = extraCount;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("count"),
					ops.createInt(this.count),
					ops.createString("extra_chance"),
					ops.createFloat(this.extraChance),
					ops.createString("extra_count"),
					ops.createInt(this.extraCount)
				)
			)
		);
	}

	public static CountExtraChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		float f = dynamic.get("extra_chance").asFloat(0.0F);
		int j = dynamic.get("extra_count").asInt(0);
		return new CountExtraChanceDecoratorConfig(i, f, j);
	}
}
