/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class PillagerSpawner {
    private static final List<SpawnEntry> SPAWN_ENTRIES = Arrays.asList(new SpawnEntry(EntityType.PILLAGER, 80), new SpawnEntry(EntityType.VINDICATOR, 20));
    private int ticksUntilNextSpawn;

    public int spawn(ServerWorld serverWorld, boolean bl, boolean bl2) {
        int k;
        if (!bl) {
            return 0;
        }
        Random random = serverWorld.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += 6000 + random.nextInt(1200);
        long l = serverWorld.getTimeOfDay() / 24000L;
        if (l < 5L || !serverWorld.isDaylight()) {
            return 0;
        }
        if (random.nextInt(5) != 0) {
            return 0;
        }
        int i = serverWorld.getPlayers().size();
        if (i < 1) {
            return 0;
        }
        PlayerEntity playerEntity = serverWorld.getPlayers().get(random.nextInt(i));
        if (playerEntity.isSpectator()) {
            return 0;
        }
        if (serverWorld.isNearOccupiedPointOfInterest(playerEntity.getBlockPos())) {
            return 0;
        }
        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos blockPos = new BlockPos(playerEntity).add(j, 0, k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1));
        if (!serverWorld.isAreaLoaded(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10)) {
            return 0;
        }
        Biome biome = serverWorld.getBiome(blockPos);
        Biome.Category category = biome.getCategory();
        if (category != Biome.Category.PLAINS && category != Biome.Category.TAIGA && category != Biome.Category.DESERT && category != Biome.Category.SAVANNA) {
            return 0;
        }
        int m = 1;
        this.spawnOneEntity(serverWorld, blockPos, random, true);
        int n = (int)Math.ceil(serverWorld.getLocalDifficulty(blockPos).getLocalDifficulty());
        for (int o = 0; o < n; ++o) {
            ++m;
            this.spawnOneEntity(serverWorld, blockPos, random, false);
        }
        return m;
    }

    private void spawnOneEntity(World world, BlockPos blockPos, Random random, boolean bl) {
        SpawnEntry spawnEntry = WeightedPicker.getRandom(random, SPAWN_ENTRIES);
        PatrolEntity patrolEntity = spawnEntry.entityType.create(world);
        if (patrolEntity != null) {
            double d = blockPos.getX() + random.nextInt(5) - random.nextInt(5);
            double e = blockPos.getZ() + random.nextInt(5) - random.nextInt(5);
            BlockPos blockPos2 = patrolEntity.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(d, (double)blockPos.getY(), e));
            if (bl) {
                patrolEntity.setPatrolLeader(true);
                patrolEntity.setRandomPatrolTarget();
            }
            patrolEntity.setPosition(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            patrolEntity.initialize(world, world.getLocalDifficulty(blockPos2), SpawnType.PATROL, null, null);
            world.spawnEntity(patrolEntity);
        }
    }

    public static class SpawnEntry
    extends WeightedPicker.Entry {
        public final EntityType<? extends PatrolEntity> entityType;

        public SpawnEntry(EntityType<? extends PatrolEntity> entityType, int i) {
            super(i);
            this.entityType = entityType;
        }
    }
}

