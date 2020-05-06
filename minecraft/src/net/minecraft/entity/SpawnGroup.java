package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum SpawnGroup {
	MONSTER("monster", 70, false, 128),
	CREATURE("creature", 10, true),
	AMBIENT("ambient", 15, true, 128),
	WATER_CREATURE("water_creature", 5, true, 128),
	WATER_AMBIENT("water_ambient", 20, true, 64),
	MISC("misc", -1, true);

	private static final Map<String, SpawnGroup> BY_NAME = (Map<String, SpawnGroup>)Arrays.stream(values())
		.collect(Collectors.toMap(SpawnGroup::getName, spawnGroup -> spawnGroup));
	private final int capacity;
	private final boolean peaceful;
	private final boolean animal;
	private final String name;
	private final int despawnStartRange = 32;
	private final int immediateDespawnRange;

	private SpawnGroup(String name, int capacity, boolean peaceful) {
		this.name = name;
		this.capacity = capacity;
		this.peaceful = peaceful;
		this.animal = true;
		this.immediateDespawnRange = Integer.MAX_VALUE;
	}

	private SpawnGroup(String name, int spawnCap, boolean peaceful, int immediateDespawnRange) {
		this.name = name;
		this.capacity = spawnCap;
		this.peaceful = peaceful;
		this.animal = false;
		this.immediateDespawnRange = immediateDespawnRange;
	}

	public String getName() {
		return this.name;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public boolean isPeaceful() {
		return this.peaceful;
	}

	public boolean isAnimal() {
		return this.animal;
	}

	public int getImmediateDespawnRange() {
		return this.immediateDespawnRange;
	}

	public int getDespawnStartRange() {
		return 32;
	}
}
