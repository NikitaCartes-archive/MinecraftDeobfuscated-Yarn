package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class class_4635 implements FeatureConfig {
	public final class_4651 field_21230;
	public final class_4651 field_21231;
	public final int field_21232;

	public class_4635(class_4651 arg, class_4651 arg2, int i) {
		this.field_21230 = arg;
		this.field_21231 = arg2;
		this.field_21232 = i;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("cap_provider"), this.field_21230.serialize(dynamicOps))
			.put(dynamicOps.createString("stem_provider"), this.field_21231.serialize(dynamicOps))
			.put(dynamicOps.createString("foliage_radius"), dynamicOps.createInt(this.field_21232));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	public static <T> class_4635 method_23407(Dynamic<T> dynamic) {
		class_4652<?> lv = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)dynamic.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		class_4652<?> lv2 = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)dynamic.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		return new class_4635(
			lv.method_23456(dynamic.get("cap_provider").orElseEmptyMap()),
			lv2.method_23456(dynamic.get("stem_provider").orElseEmptyMap()),
			dynamic.get("foliage_radius").asInt(2)
		);
	}
}
