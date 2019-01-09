package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class class_3596 extends class_1211 {
	public static final Map<String, String> field_15892 = ImmutableMap.<String, String>builder()
		.put("minecraft:salmon_mob", "minecraft:salmon")
		.put("minecraft:cod_mob", "minecraft:cod")
		.build();
	public static final Map<String, String> field_15893 = ImmutableMap.<String, String>builder()
		.put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg")
		.put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg")
		.build();

	public class_3596(Schema schema, boolean bl) {
		super("EntityCodSalmonFix", schema, bl);
	}

	@Override
	protected String method_5163(String string) {
		return (String)field_15892.getOrDefault(string, string);
	}
}
