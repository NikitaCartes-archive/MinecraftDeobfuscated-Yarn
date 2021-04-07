/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

public class IronGolemWanderAroundGoal
extends WanderAroundGoal {
    private static final int CHUNK_RANGE = 2;
    private static final int ENTITY_COLLISION_RANGE = 32;
    private static final int HORIZONTAL_RANGE = 10;
    private static final int VERTICAL_RANGE = 7;

    public IronGolemWanderAroundGoal(PathAwareEntity pathAwareEntity, double d) {
        super(pathAwareEntity, d, 240, false);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d vec3d;
        float f = this.mob.world.random.nextFloat();
        if (this.mob.world.random.nextFloat() < 0.3f) {
            return this.findRandomInRange();
        }
        if (f < 0.7f) {
            vec3d = this.findVillagerPos();
            if (vec3d == null) {
                vec3d = this.findRandomBlockPos();
            }
        } else {
            vec3d = this.findRandomBlockPos();
            if (vec3d == null) {
                vec3d = this.findVillagerPos();
            }
        }
        return vec3d == null ? this.findRandomInRange() : vec3d;
    }

    @Nullable
    private Vec3d findRandomInRange() {
        return FuzzyTargeting.find(this.mob, 10, 7);
    }

    @Nullable
    private Vec3d findVillagerPos() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        List<VillagerEntity> list = serverWorld.getEntitiesByType(EntityType.VILLAGER, this.mob.getBoundingBox().expand(32.0), this::canVillagerSummonGolem);
        if (list.isEmpty()) {
            return null;
        }
        VillagerEntity villagerEntity = list.get(this.mob.world.random.nextInt(list.size()));
        Vec3d vec3d = villagerEntity.getPos();
        return FuzzyTargeting.findTo(this.mob, 10, 7, vec3d);
    }

    @Nullable
    private Vec3d findRandomBlockPos() {
        ChunkSectionPos chunkSectionPos = this.findRandomChunkPos();
        if (chunkSectionPos == null) {
            return null;
        }
        BlockPos blockPos = this.findRandomPosInChunk(chunkSectionPos);
        if (blockPos == null) {
            return null;
        }
        return FuzzyTargeting.findTo(this.mob, 10, 7, Vec3d.ofBottomCenter(blockPos));
    }

    @Nullable
    private ChunkSectionPos findRandomChunkPos() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        List list = ChunkSectionPos.stream(ChunkSectionPos.from(this.mob), 2).filter(chunkSectionPos -> serverWorld.getOccupiedPointOfInterestDistance((ChunkSectionPos)chunkSectionPos) == 0).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return (ChunkSectionPos)list.get(serverWorld.random.nextInt(list.size()));
    }

    @Nullable
    private BlockPos findRandomPosInChunk(ChunkSectionPos pos) {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        List list = pointOfInterestStorage.getInCircle(pointOfInterestType -> true, pos.getCenterPos(), 8, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).map(PointOfInterest::getPos).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return (BlockPos)list.get(serverWorld.random.nextInt(list.size()));
    }

    private boolean canVillagerSummonGolem(VillagerEntity villager) {
        return villager.canSummonGolem(this.mob.world.getTime());
    }
}

