/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.spawner;

import net.minecraft.server.world.ServerWorld;

/**
 * Spawns entities in a world.
 * 
 * <p>A spawner is typically used to spawn entities within a
 * special context, such as cats in a village or wandering traders.
 * This is different from
 * {@link net.minecraft.world.MobSpawnerLogic the mob spawner logic}
 * which is used for {@link net.minecraft.SpawnerBlock the spawner block},
 * or the structure spawn conditions (such as guardians) which is defined in
 * {@link net.minecraft.world.gen.chunk.ChunkGeneratorSettings#getEntitySpawnList}.
 * However, cats in swamp huts are spawned in both {@link CatSpawner} and
 * the normal structure spawning.
 */
public interface Spawner {
    /**
     * Spawns entities into a world.
     * 
     * @return the number of entities spawned
     * 
     * @param spawnAnimals whether animals should be spawned
     * @param spawnMonsters whether monsters should be spawned
     */
    public int spawn(ServerWorld var1, boolean var2, boolean var3);
}

