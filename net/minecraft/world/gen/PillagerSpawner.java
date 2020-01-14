/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class PillagerSpawner {
    private int ticksUntilNextSpawn;

    public int spawn(ServerWorld serverWorld, boolean spawnMonsters, boolean spawnAnimals) {
        int k;
        if (!spawnMonsters) {
            return 0;
        }
        if (!serverWorld.getGameRules().getBoolean(GameRules.DO_PATROL_SPAWNING)) {
            return 0;
        }
        Random random = serverWorld.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += 12000 + random.nextInt(1200);
        long l = serverWorld.getTimeOfDay() / 24000L;
        if (l < 5L || !serverWorld.isDay()) {
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
        BlockPos.Mutable mutable = new BlockPos.Mutable(playerEntity).setOffset(j, 0, k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1));
        if (!serverWorld.isRegionLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)) {
            return 0;
        }
        Biome biome = serverWorld.getBiome(mutable);
        Biome.Category category = biome.getCategory();
        if (category == Biome.Category.MUSHROOM) {
            return 0;
        }
        int m = 0;
        int n = (int)Math.ceil(serverWorld.getLocalDifficulty(mutable).getLocalDifficulty()) + 1;
        for (int o = 0; o < n; ++o) {
            ++m;
            mutable.setY(serverWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
            if (o == 0) {
                if (!this.spawnOneEntity(serverWorld, mutable, random, true)) {
                    break;
                }
            } else {
                this.spawnOneEntity(serverWorld, mutable, random, false);
            }
            mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
            mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
        }
        return m;
    }

    private boolean spawnOneEntity(World world, BlockPos blockPos, Random random, boolean bl) {
        BlockState blockState = world.getBlockState(blockPos);
        if (!SpawnHelper.isClearForSpawn(world, blockPos, blockState, blockState.getFluidState())) {
            return false;
        }
        if (!PatrolEntity.canSpawn(EntityType.PILLAGER, world, SpawnType.PATROL, blockPos, random)) {
            return false;
        }
        PatrolEntity patrolEntity = EntityType.PILLAGER.create(world);
        if (patrolEntity != null) {
            if (bl) {
                patrolEntity.setPatrolLeader(true);
                patrolEntity.setRandomPatrolTarget();
            }
            patrolEntity.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            patrolEntity.initialize(world, world.getLocalDifficulty(blockPos), SpawnType.PATROL, null, null);
            world.spawnEntity(patrolEntity);
            return true;
        }
        return false;
    }
}

