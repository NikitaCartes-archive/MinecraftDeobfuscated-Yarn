package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixers.fixes.EntityRenameFix;

public class class_3983 extends EntityRenameFix {
	public static final Map<String, String> field_17712 = ImmutableMap.<String, String>builder()
		.put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg")
		.build();

	public class_3983(Schema schema, boolean bl) {
		super("EntityRavagerRenameFix", schema, bl);
	}

	@Override
	protected String rename(String string) {
		return Objects.equals("minecraft:illager_beast", string) ? "minecraft:ravager" : string;
	}
}
