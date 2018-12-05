package net.minecraft.datafixers.mapping;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class LegacyDyeItemMapping {
	public static final Map<String, String> MAP = ImmutableMap.builder()
		.put("minecraft:cactus_green", "minecraft:green_dye")
		.put("minecraft:rose_red", "minecraft:red_dye")
		.put("minecraft:dandelion_yellow", "minecraft:yellow_dye")
		.build();
}
