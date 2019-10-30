package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;

public class VillageFeatureConfig implements FeatureConfig {
	public final Identifier startPool;
	public final int size;

	public VillageFeatureConfig(String startPool, int size) {
		this.startPool = new Identifier(startPool);
		this.size = size;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(ops.createString("start_pool"), ops.createString(this.startPool.toString()), ops.createString("size"), ops.createInt(this.size))
			)
		);
	}

	public static <T> VillageFeatureConfig deserialize(Dynamic<T> dynamic) {
		String string = dynamic.get("start_pool").asString("");
		int i = dynamic.get("size").asInt(6);
		return new VillageFeatureConfig(string, i);
	}
}
