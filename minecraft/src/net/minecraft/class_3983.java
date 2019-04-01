package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class class_3983 extends class_1211 {
	public static final Map<String, String> field_17712 = ImmutableMap.<String, String>builder()
		.put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg")
		.build();

	public class_3983(Schema schema, boolean bl) {
		super("EntityRavagerRenameFix", schema, bl);
	}

	@Override
	protected String method_5163(String string) {
		return Objects.equals("minecraft:illager_beast", string) ? "minecraft:ravager" : string;
	}
}
