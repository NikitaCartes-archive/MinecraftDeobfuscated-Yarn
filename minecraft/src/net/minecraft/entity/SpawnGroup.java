package net.minecraft.entity;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;

public enum SpawnGroup implements StringIdentifiable {
	field_6302("monster", 70, false, false, 128),
	field_6294("creature", 10, true, true, 128),
	field_6303("ambient", 15, true, false, 128),
	field_6300("water_creature", 5, true, false, 128),
	field_24460("water_ambient", 20, true, false, 64),
	field_17715("misc", -1, true, true, 128);

	public static final Codec<SpawnGroup> field_24655 = StringIdentifiable.createCodec(SpawnGroup::values, SpawnGroup::method_28307);
	private static final Map<String, SpawnGroup> BY_NAME = (Map<String, SpawnGroup>)Arrays.stream(values())
		.collect(Collectors.toMap(SpawnGroup::getName, spawnGroup -> spawnGroup));
	private final int capacity;
	private final boolean peaceful;
	private final boolean animal;
	private final String name;
	private final int despawnStartRange = 32;
	private final int immediateDespawnRange;

	private SpawnGroup(String name, int spawnCap, boolean peaceful, boolean bl, int j) {
		this.name = name;
		this.capacity = spawnCap;
		this.peaceful = peaceful;
		this.animal = bl;
		this.immediateDespawnRange = j;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public static SpawnGroup method_28307(String string) {
		return (SpawnGroup)BY_NAME.get(string);
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
