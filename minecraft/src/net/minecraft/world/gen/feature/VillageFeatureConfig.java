package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.sortme.structures.VillageGenerator;

public class VillageFeatureConfig implements FeatureConfig {
	public final int sizeModifier;
	public final VillageGenerator.Type type;

	public VillageFeatureConfig(int i, VillageGenerator.Type type) {
		this.sizeModifier = i;
		this.type = type;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("size_modifier"),
					dynamicOps.createInt(this.sizeModifier),
					dynamicOps.createString("type"),
					dynamicOps.createString(this.type.getName())
				)
			)
		);
	}

	public static <T> VillageFeatureConfig deserialize(Dynamic<T> dynamic) {
		int i = dynamic.getInt("size_modifier", 0);
		VillageGenerator.Type type = VillageGenerator.Type.byName(dynamic.getString("type", ""));
		return new VillageFeatureConfig(i, type);
	}
}
