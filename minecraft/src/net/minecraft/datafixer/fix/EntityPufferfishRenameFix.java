package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class EntityPufferfishRenameFix extends EntityRenameFix {
	public static final Map<String, String> RENAMED_FISH = ImmutableMap.<String, String>builder()
		.put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg")
		.build();

	public EntityPufferfishRenameFix(Schema outputSchema, boolean changesType) {
		super("EntityPufferfishRenameFix", outputSchema, changesType);
	}

	@Override
	protected String rename(String oldName) {
		return Objects.equals("minecraft:puffer_fish", oldName) ? "minecraft:pufferfish" : oldName;
	}
}
