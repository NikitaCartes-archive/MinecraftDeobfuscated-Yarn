package net.minecraft.datafixers.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class EntityRavagerRenameFix extends EntityRenameFix {
	public static final Map<String, String> ITEMS = ImmutableMap.builder().put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg").build();

	public EntityRavagerRenameFix(Schema schema, boolean bl) {
		super("EntityRavagerRenameFix", schema, bl);
	}

	@Override
	protected String rename(String string) {
		return Objects.equals("minecraft:illager_beast", string) ? "minecraft:ravager" : string;
	}
}
