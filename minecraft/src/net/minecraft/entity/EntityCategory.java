package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EntityCategory {
	field_6302("monster", 70, false, false),
	field_6294("creature", 10, true, true),
	field_6303("ambient", 15, true, false),
	field_6300("water_creature", 15, true, false),
	field_17715("misc", 15, true, false);

	private static final Map<String, EntityCategory> BY_NAME = (Map<String, EntityCategory>)Arrays.stream(values())
		.collect(Collectors.toMap(EntityCategory::getName, entityCategory -> entityCategory));
	private final int spawnCap;
	private final boolean peaceful;
	private final boolean animal;
	private final String name;

	private EntityCategory(String string2, int j, boolean bl, boolean bl2) {
		this.name = string2;
		this.spawnCap = j;
		this.peaceful = bl;
		this.animal = bl2;
	}

	public String getName() {
		return this.name;
	}

	public int getSpawnCap() {
		return this.spawnCap;
	}

	public boolean isPeaceful() {
		return this.peaceful;
	}

	public boolean isAnimal() {
		return this.animal;
	}
}
