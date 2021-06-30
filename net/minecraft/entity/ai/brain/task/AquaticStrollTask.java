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
import org.jetbrains.annotations.Nullable;

public class AquaticStrollTask
extends StrollTask {
    public static final int[][] NORMALIZED_POS_MULTIPLIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

    public AquaticStrollTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return pathAwareEntity.isInsideWaterOrBubbleColumn();
    }

    @Override
    @Nullable
    protected Vec3d findWalkTarget(PathAwareEntity entity) {
        Vec3d vec3d = null;
        Vec3d vec3d2 = null;
        for (int[] is : NORMALIZED_POS_MULTIPLIERS) {
            vec3d2 = vec3d == null ? LookTargetUtil.find(entity, is[0], is[1]) : entity.getPos().add(entity.getPos().relativize(vec3d).normalize().multiply(is[0], is[1], is[0]));
            if (vec3d2 == null || entity.world.getFluidState(new BlockPos(vec3d2)).isEmpty()) {
                return vec3d;
            }
            vec3d = vec3d2;
        }
        return vec3d2;
    }
}

