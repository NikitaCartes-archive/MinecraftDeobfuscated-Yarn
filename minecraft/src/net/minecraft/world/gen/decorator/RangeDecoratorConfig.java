package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class RangeDecoratorConfig implements DecoratorConfig {
	public final int count;
	public final int bottomOffset;
	public final int topOffset;
	public final int maximum;

	public RangeDecoratorConfig(int count, int bottomOffset, int topOffset, int maximum) {
		this.count = count;
		this.bottomOffset = bottomOffset;
		this.topOffset = topOffset;
		this.maximum = maximum;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("count"),
					ops.createInt(this.count),
					ops.createString("bottom_offset"),
					ops.createInt(this.bottomOffset),
					ops.createString("top_offset"),
					ops.createInt(this.topOffset),
					ops.createString("maximum"),
					ops.createInt(this.maximum)
				)
			)
		);
	}

	public static RangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		int j = dynamic.get("bottom_offset").asInt(0);
		int k = dynamic.get("top_offset").asInt(0);
		int l = dynamic.get("maximum").asInt(0);
		return new RangeDecoratorConfig(i, j, k, l);
	}

	public static RangeDecoratorConfig method_26607(Random random) {
		int i = random.nextInt(11) + 1;
		int j = random.nextInt(11) + 1;
		return new RangeDecoratorConfig(random.nextInt(16) + 1, i, j, i + j + random.nextInt(70) + 1);
	}
}
