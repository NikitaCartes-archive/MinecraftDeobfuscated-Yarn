package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class class_3608 extends class_1211 {
	public static final Map<String, String> field_15899 = ImmutableMap.<String, String>builder()
		.put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg")
		.build();

	public class_3608(Schema schema, boolean bl) {
		super("EntityPufferfishRenameFix", schema, bl);
	}

	@Override
	protected String method_5163(String string) {
		return Objects.equals("minecraft:puffer_fish", string) ? "minecraft:pufferfish" : string;
	}
}
