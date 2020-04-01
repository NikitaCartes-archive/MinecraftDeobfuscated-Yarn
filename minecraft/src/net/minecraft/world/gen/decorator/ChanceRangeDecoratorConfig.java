package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class ChanceRangeDecoratorConfig implements DecoratorConfig {
	public final float chance;
	public final int bottomOffset;
	public final int topOffset;
	public final int top;

	public ChanceRangeDecoratorConfig(float chance, int bottomOffset, int topOffset, int top) {
		this.chance = chance;
		this.bottomOffset = bottomOffset;
		this.topOffset = topOffset;
		this.top = top;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("chance"),
					ops.createFloat(this.chance),
					ops.createString("bottom_offset"),
					ops.createInt(this.bottomOffset),
					ops.createString("top_offset"),
					ops.createInt(this.topOffset),
					ops.createString("top"),
					ops.createInt(this.top)
				)
			)
		);
	}

	public static ChanceRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
		float f = dynamic.get("chance").asFloat(0.0F);
		int i = dynamic.get("bottom_offset").asInt(0);
		int j = dynamic.get("top_offset").asInt(0);
		int k = dynamic.get("top").asInt(0);
		return new ChanceRangeDecoratorConfig(f, i, j, k);
	}

	public static ChanceRangeDecoratorConfig method_26599(Random random) {
		int i = random.nextInt(11);
		int j = random.nextInt(11);
		return new ChanceRangeDecoratorConfig(random.nextFloat(), i, j, i + j + random.nextInt(70));
	}
}
