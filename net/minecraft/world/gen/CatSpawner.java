/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class CatSpawner
implements Spawner {
    private int ticksUntilNextSpawn;

    @Override
    public int spawn(ServerWorld serverWorld, boolean bl, boolean bl2) {
        if (!bl2 || !serverWorld.getGameRules().getBoolean(GameRules.field_19390)) {
            return 0;
        }
        --this.ticksUntilNextSpawn;
        if (this.ticksUntilNextSpawn > 0) {
            return 0;
        }
        this.ticksUntilNextSpawn = 1200;
        ServerPlayerEntity playerEntity = serverWorld.getRandomAlivePlayer();
        if (playerEntity == null) {
            return 0;
        }
        Random random = serverWorld.random;
        int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos blockPos = playerEntity.getBlockPos().add(i, 0, j);
        if (!serverWorld.isRegionLoaded(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10)) {
            return 0;
        }
        if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, serverWorld, blockPos, EntityType.CAT)) {
            if (serverWorld.isNearOccupiedPointOfInterest(blockPos, 2)) {
                return this.spawnInHouse(serverWorld, blockPos);
            }
            if (serverWorld.getStructureAccessor().method_28388(blockPos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
                return this.spawnInSwampHut(serverWorld, blockPos);
            }
        }
        return 0;
    }

    private int spawnInHouse(ServerWorld world, BlockPos pos) {
        List<CatEntity> list;
        int i = 48;
        if (world.getPointOfInterestStorage().count(PointOfInterestType.HOME.getCompletionCondition(), pos, 48, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED) > 4L && (list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos).expand(48.0, 8.0, 48.0))).size() < 5) {
            return this.spawn(pos, world);
        }
        return 0;
    }

    private int spawnInSwampHut(World world, BlockPos pos) {
        int i = 16;
        List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos).expand(16.0, 8.0, 16.0));
        if (list.size() < 1) {
            return this.spawn(pos, world);
        }
        return 0;
    }

    private int spawn(BlockPos pos, World world) {
        CatEntity catEntity = EntityType.CAT.create(world);
        if (catEntity == null) {
            return 0;
        }
        catEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, null, null);
        catEntity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
        world.spawnEntity(catEntity);
        return 1;
    }
}

