package net.minecraft.entity;

import net.minecraft.util.StringIdentifiable;

/**
 * A spawn group represents the category of an entity's natural spawning.
 * 
 * <p>Entities that don't support natural spawning belong to the
 * {@link #MISC} group.
 * 
 * @see EntityType#getSpawnGroup()
 * @see net.minecraft.world.SpawnHelper
 */
public enum SpawnGroup implements StringIdentifiable {
	MONSTER("monster", 70, false, false, 128),
	CREATURE("creature", 10, true, true, 128),
	AMBIENT("ambient", 15, true, false, 128),
	AXOLOTLS("axolotls", 5, true, false, 128),
	UNDERGROUND_WATER_CREATURE("underground_water_creature", 5, true, false, 128),
	WATER_CREATURE("water_creature", 5, true, false, 128),
	WATER_AMBIENT("water_ambient", 20, true, false, 64),
	MISC("misc", -1, true, true, 128);

	/**
	 * A codec that encodes and decodes a spawn group from and to its
	 * {@linkplain #getName() name} string.
	 */
	public static final com.mojang.serialization.Codec<SpawnGroup> CODEC = StringIdentifiable.createCodec(SpawnGroup::values);
	private final int capacity;
	private final boolean peaceful;
	private final boolean rare;
	private final String name;
	private final int despawnStartRange = 32;
	private final int immediateDespawnRange;

	private SpawnGroup(String name, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
		this.name = name;
		this.capacity = spawnCap;
		this.peaceful = peaceful;
		this.rare = rare;
		this.immediateDespawnRange = immediateDespawnRange;
	}

	/**
	 * Returns the name of this spawn group.
	 * 
	 * <p>The names are unique and are in {@code lower_snake_case}.
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	/**
	 * Returns the maximum number of mobs in this group that can be spawned per
	 * chunk.
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Returns {@code true} if this group is spawned as animals, or {@code false}
	 * if this group is spawned as monsters.
	 * 
	 * @see net.minecraft.world.World#setMobSpawnOptions(boolean, boolean)
	 */
	public boolean isPeaceful() {
		return this.peaceful;
	}

	/**
	 * Returns if this spawn group is spawned only rarely.
	 * 
	 * <p>A rare spawn only happens when the {@linkplain
	 * net.minecraft.world.WorldProperties#getTime() world time} is a multiple
	 * of {@code 400} in {@link
	 * net.minecraft.server.world.ServerChunkManager#tickChunks()}.
	 */
	public boolean isRare() {
		return this.rare;
	}

	/**
	 * Returns the distance, of a mob of this group from a player, at which
	 * that mob will despawn immediately.
	 * 
	 * <p>This is ignored if a mob {@linkplain
	 * net.minecraft.entity.mob.MobEntity#canImmediatelyDespawn(double) cannot
	 * immediately despawn}.
	 * 
	 * @see net.minecraft.entity.mob.MobEntity#checkDespawn()
	 */
	public int getImmediateDespawnRange() {
		return this.immediateDespawnRange;
	}

	/**
	 * Returns the distance, of a mob of this group from a player, at which
	 * that mob can despawn at chance.
	 * 
	 * <p>This is ignored if a mob {@linkplain
	 * net.minecraft.entity.mob.MobEntity#canImmediatelyDespawn(double) cannot
	 * immediately despawn}.
	 * 
	 * @see net.minecraft.entity.mob.MobEntity#checkDespawn()
	 */
	public int getDespawnStartRange() {
		return 32;
	}
}
