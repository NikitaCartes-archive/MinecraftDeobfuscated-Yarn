package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;

public class VillageFeatureConfig implements FeatureConfig {
	public final Identifier startPool;
	public final int size;

	public VillageFeatureConfig(String string, int i) {
		this.startPool = new Identifier(string);
		this.size = i;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("start_pool"),
					dynamicOps.createString(this.startPool.toString()),
					dynamicOps.createString("size"),
					dynamicOps.createInt(this.size)
				)
			)
		);
	}

	public static <T> VillageFeatureConfig deserialize(Dynamic<T> dynamic) {
		String string = dynamic.get("start_pool").asString("");
		int i = dynamic.get("size").asInt(6);
		return new VillageFeatureConfig(string, i);
	}
}
