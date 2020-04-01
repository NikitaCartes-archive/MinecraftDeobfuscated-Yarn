package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Identifier;

public class VillageFeatureConfig implements FeatureConfig {
	private static final List<String> field_23590 = ImmutableList.of(
		"village/plains/town_centers", "village/desert/town_centers", "village/savanna/town_centers", "village/snowy/town_centers", "village/taiga/town_centers"
	);
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

	public static VillageFeatureConfig method_26651(Random random) {
		return new VillageFeatureConfig((String)field_23590.get(random.nextInt(field_23590.size())), random.nextInt(10));
	}
}
