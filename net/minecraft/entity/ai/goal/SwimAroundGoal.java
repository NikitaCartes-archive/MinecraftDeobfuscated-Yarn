/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SwimAroundGoal
extends WanderAroundGoal {
    public SwimAroundGoal(PathAwareEntity pathAwareEntity, double d, int i) {
        super(pathAwareEntity, d, i);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        return LookTargetUtil.find(this.mob, 10, 7);
    }
}

