/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WanderAroundFarGoal
extends WanderAroundGoal {
    protected final float probability;

    public WanderAroundFarGoal(MobEntityWithAi mobEntityWithAi, double d) {
        this(mobEntityWithAi, d, 0.001f);
    }

    public WanderAroundFarGoal(MobEntityWithAi mob, double speed, float probabiliity) {
        super(mob, speed);
        this.probability = probabiliity;
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        if (this.mob.isInsideWaterOrBubbleColumn()) {
            Vec3d vec3d = TargetFinder.findGroundTarget(this.mob, 15, 7);
            return vec3d == null ? super.getWanderTarget() : vec3d;
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return TargetFinder.findGroundTarget(this.mob, 10, 7);
        }
        return super.getWanderTarget();
    }
}

