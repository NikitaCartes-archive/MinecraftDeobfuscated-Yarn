package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.sortme.Living;

public enum EntityCategory {
	field_6302("monster", Monster.class, 70, false, false),
	field_6294("creature", AnimalEntity.class, 10, true, true),
	field_6303("ambient", AmbientEntity.class, 15, true, false),
	field_6300("water_creature", WaterCreatureEntity.class, 15, true, false);

	private static final Map<String, EntityCategory> BY_NAME = (Map<String, EntityCategory>)Arrays.stream(values())
		.collect(Collectors.toMap(EntityCategory::getName, entityCategory -> entityCategory));
	private final Class<? extends Living> entityClass;
	private final int spawnCap;
	private final boolean peaceful;
	private final boolean animal;
	private final String name;

	private EntityCategory(String string2, Class<? extends Living> class_, int j, boolean bl, boolean bl2) {
		this.name = string2;
		this.entityClass = class_;
		this.spawnCap = j;
		this.peaceful = bl;
		this.animal = bl2;
	}

	public String getName() {
		return this.name;
	}

	public Class<? extends Living> getEntityClass() {
		return this.entityClass;
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
