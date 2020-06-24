/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetFinder;
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
    public IronGolemWanderAroundGoal(PathAwareEntity pathAwareEntity, double d) {
        super(pathAwareEntity, d, 240, false);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d vec3d;
        float f = this.mob.world.random.nextFloat();
        if (this.mob.world.random.nextFloat() < 0.3f) {
            return this.method_27925();
        }
        if (f < 0.7f) {
            vec3d = this.method_27926();
            if (vec3d == null) {
                vec3d = this.method_27927();
            }
        } else {
            vec3d = this.method_27927();
            if (vec3d == null) {
                vec3d = this.method_27926();
            }
        }
        return vec3d == null ? this.method_27925() : vec3d;
    }

    @Nullable
    private Vec3d method_27925() {
        return TargetFinder.findGroundTarget(this.mob, 10, 7);
    }

    @Nullable
    private Vec3d method_27926() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        List<VillagerEntity> list = serverWorld.getEntities(EntityType.VILLAGER, this.mob.getBoundingBox().expand(32.0), this::method_27922);
        if (list.isEmpty()) {
            return null;
        }
        VillagerEntity villagerEntity = list.get(this.mob.world.random.nextInt(list.size()));
        Vec3d vec3d = villagerEntity.getPos();
        return TargetFinder.method_27929(this.mob, 10, 7, vec3d);
    }

    @Nullable
    private Vec3d method_27927() {
        ChunkSectionPos chunkSectionPos = this.method_27928();
        if (chunkSectionPos == null) {
            return null;
        }
        BlockPos blockPos = this.method_27923(chunkSectionPos);
        if (blockPos == null) {
            return null;
        }
        return TargetFinder.method_27929(this.mob, 10, 7, Vec3d.ofBottomCenter(blockPos));
    }

    @Nullable
    private ChunkSectionPos method_27928() {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        List list = ChunkSectionPos.stream(ChunkSectionPos.from(this.mob), 2).filter(chunkSectionPos -> serverWorld.getOccupiedPointOfInterestDistance((ChunkSectionPos)chunkSectionPos) == 0).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return (ChunkSectionPos)list.get(serverWorld.random.nextInt(list.size()));
    }

    @Nullable
    private BlockPos method_27923(ChunkSectionPos chunkSectionPos) {
        ServerWorld serverWorld = (ServerWorld)this.mob.world;
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        List list = pointOfInterestStorage.getInCircle(pointOfInterestType -> true, chunkSectionPos.getCenterPos(), 8, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).map(PointOfInterest::getPos).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return (BlockPos)list.get(serverWorld.random.nextInt(list.size()));
    }

    private boolean method_27922(VillagerEntity villagerEntity) {
        return villagerEntity.canSummonGolem(this.mob.world.getTime());
    }
}

