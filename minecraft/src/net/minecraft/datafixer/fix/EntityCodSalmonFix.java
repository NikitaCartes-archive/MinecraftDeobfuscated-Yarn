package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityCodSalmonFix extends EntityRenameFix {
	public static final Map<String, String> ENTITIES = ImmutableMap.<String, String>builder()
		.put("minecraft:salmon_mob", "minecraft:salmon")
		.put("minecraft:cod_mob", "minecraft:cod")
		.build();
	public static final Map<String, String> SPAWN_EGGS = ImmutableMap.<String, String>builder()
		.put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg")
		.put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg")
		.build();

	public EntityCodSalmonFix(Schema outputSchema, boolean changesType) {
		super("EntityCodSalmonFix", outputSchema, changesType);
	}

	@Override
	protected String rename(String oldName) {
		return (String)ENTITIES.getOrDefault(oldName, oldName);
	}
}
