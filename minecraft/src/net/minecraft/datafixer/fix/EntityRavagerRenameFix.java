package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class EntityRavagerRenameFix extends EntityRenameFix {
	public static final Map<String, String> ITEMS = ImmutableMap.<String, String>builder()
		.put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg")
		.build();

	public EntityRavagerRenameFix(Schema outputSchema, boolean changesType) {
		super("EntityRavagerRenameFix", outputSchema, changesType);
	}

	@Override
	protected String rename(String oldName) {
		return Objects.equals("minecraft:illager_beast", oldName) ? "minecraft:ravager" : oldName;
	}
}
