/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AquaticStrollTask
extends StrollTask {
    public AquaticStrollTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return pathAwareEntity.isInsideWaterOrBubbleColumn();
    }

    @Override
    protected Vec3d findWalkTarget(PathAwareEntity entity) {
        Vec3d vec3d = LookTargetUtil.find(entity, this.horizontalRadius, this.verticalRadius);
        if (vec3d != null && entity.world.getFluidState(new BlockPos(vec3d)).isEmpty()) {
            return null;
        }
        return vec3d;
    }
}

