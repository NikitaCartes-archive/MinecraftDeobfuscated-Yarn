/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.gen.Spawner;

public class PhantomSpawner
implements Spawner {
    private int ticksUntilNextSpawn;

    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        }
        if (!world.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
            return 0;
        }
        Random random = world.random;
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn += (60 + random.nextInt(60)) * 20;
        if (world.getAmbientDarkness() < 5 && world.getDimension().hasSkyLight()) {
            return 0;
        }
        int i = 0;
        for (PlayerEntity playerEntity : world.getPlayers()) {
            FluidState fluidState;
            BlockState blockState;
            BlockPos blockPos2;
            LocalDifficulty localDifficulty;
            if (playerEntity.isSpectator()) continue;
            BlockPos blockPos = playerEntity.getBlockPos();
            if (world.getDimension().hasSkyLight() && (blockPos.getY() < world.getSeaLevel() || !world.isSkyVisible(blockPos)) || !(localDifficulty = world.getLocalDifficulty(blockPos)).isHarderThan(random.nextFloat() * 3.0f)) continue;
            ServerStatHandler serverStatHandler = ((ServerPlayerEntity)playerEntity).getStatHandler();
            int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
            int k = 24000;
            if (random.nextInt(j) < 72000 || !SpawnHelper.isClearForSpawn(world, blockPos2 = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21)), blockState = world.getBlockState(blockPos2), fluidState = world.getFluidState(blockPos2), EntityType.PHANTOM)) continue;
            EntityData entityData = null;
            int l = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);
            for (int m = 0; m < l; ++m) {
                PhantomEntity phantomEntity = EntityType.PHANTOM.create(world);
                phantomEntity.refreshPositionAndAngles(blockPos2, 0.0f, 0.0f);
                entityData = phantomEntity.initialize(world, localDifficulty, SpawnReason.NATURAL, entityData, null);
                world.spawnEntity(phantomEntity);
            }
            i += l;
        }
        return i;
    }
}

