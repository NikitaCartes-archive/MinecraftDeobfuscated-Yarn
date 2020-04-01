package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class BuriedTreasureFeatureConfig implements FeatureConfig {
	public final float probability;

	public BuriedTreasureFeatureConfig(float probability) {
		this.probability = probability;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("probability"), ops.createFloat(this.probability))));
	}

	public static <T> BuriedTreasureFeatureConfig deserialize(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		return new BuriedTreasureFeatureConfig(f);
	}

	public static BuriedTreasureFeatureConfig method_26598(Random random) {
		return new BuriedTreasureFeatureConfig(random.nextFloat() / 20.0F);
	}
}
