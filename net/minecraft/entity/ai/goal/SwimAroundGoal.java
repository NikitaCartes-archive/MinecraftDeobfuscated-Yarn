/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SwimAroundGoal
extends WanderAroundGoal {
    public SwimAroundGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
        super(mobEntityWithAi, d, i);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d vec3d = TargetFinder.findTarget(this.mob, 10, 7);
        int i = 0;
        while (vec3d != null && !this.mob.world.getBlockState(new BlockPos(vec3d)).canPathfindThrough(this.mob.world, new BlockPos(vec3d), NavigationType.WATER) && i++ < 10) {
            vec3d = TargetFinder.findTarget(this.mob, 10, 7);
        }
        return vec3d;
    }
}

