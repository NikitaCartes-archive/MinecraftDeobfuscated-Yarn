/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.BuiltInBiomes;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class WanderingTraderManager
implements Spawner {
    private final Random random = new Random();
    private final ServerWorldProperties properties;
    private int spawnTimer;
    private int spawnDelay;
    private int spawnChance;

    public WanderingTraderManager(ServerWorldProperties properties) {
        this.properties = properties;
        this.spawnTimer = 1200;
        this.spawnDelay = properties.getWanderingTraderSpawnDelay();
        this.spawnChance = properties.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            properties.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            properties.setWanderingTraderSpawnChance(this.spawnChance);
        }
    }

    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            return 0;
        }
        if (--this.spawnTimer > 0) {
            return 0;
        }
        this.spawnTimer = 1200;
        this.spawnDelay -= 1200;
        this.properties.setWanderingTraderSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) {
            return 0;
        }
        this.spawnDelay = 24000;
        if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            return 0;
        }
        int i = this.spawnChance;
        this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
        this.properties.setWanderingTraderSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > i) {
            return 0;
        }
        if (this.method_18018(world)) {
            this.spawnChance = 25;
            return 1;
        }
        return 0;
    }

    private boolean method_18018(ServerWorld serverWorld) {
        ServerPlayerEntity playerEntity = serverWorld.getRandomAlivePlayer();
        if (playerEntity == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        BlockPos blockPos2 = playerEntity.getBlockPos();
        int i = 48;
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        Optional<BlockPos> optional = pointOfInterestStorage.getPosition(PointOfInterestType.MEETING.getCompletionCondition(), blockPos -> true, blockPos2, 48, PointOfInterestStorage.OccupationStatus.ANY);
        BlockPos blockPos22 = optional.orElse(blockPos2);
        BlockPos blockPos3 = this.getNearbySpawnPos(serverWorld, blockPos22, 48);
        if (blockPos3 != null && this.doesNotSuffocateAt(serverWorld, blockPos3)) {
            if (serverWorld.method_31081(blockPos3).equals(Optional.of(BuiltInBiomes.THE_VOID))) {
                return false;
            }
            WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(serverWorld, null, null, null, blockPos3, SpawnReason.EVENT, false, false);
            if (wanderingTraderEntity != null) {
                for (int j = 0; j < 2; ++j) {
                    this.spawnLlama(serverWorld, wanderingTraderEntity, 4);
                }
                this.properties.setWanderingTraderId(wanderingTraderEntity.getUuid());
                wanderingTraderEntity.setDespawnDelay(48000);
                wanderingTraderEntity.setWanderTarget(blockPos22);
                wanderingTraderEntity.setPositionTarget(blockPos22, 16);
                return true;
            }
        }
        return false;
    }

    private void spawnLlama(ServerWorld serverWorld, WanderingTraderEntity wanderingTraderEntity, int i) {
        BlockPos blockPos = this.getNearbySpawnPos(serverWorld, wanderingTraderEntity.getBlockPos(), i);
        if (blockPos == null) {
            return;
        }
        TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(serverWorld, null, null, null, blockPos, SpawnReason.EVENT, false, false);
        if (traderLlamaEntity == null) {
            return;
        }
        traderLlamaEntity.attachLeash(wanderingTraderEntity, true);
    }

    @Nullable
    private BlockPos getNearbySpawnPos(WorldView worldView, BlockPos blockPos, int i) {
        BlockPos blockPos2 = null;
        for (int j = 0; j < 10; ++j) {
            int l;
            int m;
            int k = blockPos.getX() + this.random.nextInt(i * 2) - i;
            BlockPos blockPos3 = new BlockPos(k, m = worldView.getTopY(Heightmap.Type.WORLD_SURFACE, k, l = blockPos.getZ() + this.random.nextInt(i * 2) - i), l);
            if (!SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, worldView, blockPos3, EntityType.WANDERING_TRADER)) continue;
            blockPos2 = blockPos3;
            break;
        }
        return blockPos2;
    }

    private boolean doesNotSuffocateAt(BlockView blockView, BlockPos blockPos) {
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos, blockPos.add(1, 2, 1))) {
            if (blockView.getBlockState(blockPos2).getCollisionShape(blockView, blockPos2).isEmpty()) continue;
            return false;
        }
        return true;
    }
}

