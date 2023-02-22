/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;

public class GoToPointOfInterestTask {
    public static Task<VillagerEntity> create(float speed, int completionRange) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, walkTarget -> (world, entity, time) -> {
            if (world.isNearOccupiedPointOfInterest(entity.getBlockPos())) {
                return false;
            }
            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            int j = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(entity.getBlockPos()));
            Vec3d vec3d = null;
            for (int k = 0; k < 5; ++k) {
                Vec3d vec3d2 = FuzzyTargeting.find(entity, 15, 7, pos -> -pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(pos)));
                if (vec3d2 == null) continue;
                int l = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(BlockPos.ofFloored(vec3d2)));
                if (l < j) {
                    vec3d = vec3d2;
                    break;
                }
                if (l != j) continue;
                vec3d = vec3d2;
            }
            if (vec3d != null) {
                walkTarget.remember(new WalkTarget(vec3d, speed, completionRange));
            }
            return true;
        }));
    }
}

