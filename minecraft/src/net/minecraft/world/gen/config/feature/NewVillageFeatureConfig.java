package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;

public class NewVillageFeatureConfig implements FeatureConfig {
	public final Identifier startPool;
	public final int size;

	public NewVillageFeatureConfig(String string, int i) {
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

	public static <T> NewVillageFeatureConfig method_16752(Dynamic<T> dynamic) {
		String string = dynamic.getString("start_pool");
		int i = dynamic.getInt("size", 6);
		return new NewVillageFeatureConfig(string, i);
	}
}
