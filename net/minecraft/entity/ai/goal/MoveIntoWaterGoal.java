/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class MoveIntoWaterGoal
extends Goal {
    private final PathAwareEntity mob;

    public MoveIntoWaterGoal(PathAwareEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        return this.mob.isOnGround() && !this.mob.world.getFluidState(this.mob.getBlockPos()).isIn(FluidTags.WATER);
    }

    @Override
    public void start() {
        Vec3i blockPos = null;
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 2.0), MathHelper.floor(this.mob.getY() - 2.0), MathHelper.floor(this.mob.getZ() - 2.0), MathHelper.floor(this.mob.getX() + 2.0), this.mob.getBlockY(), MathHelper.floor(this.mob.getZ() + 2.0));
        for (BlockPos blockPos2 : iterable) {
            if (!this.mob.world.getFluidState(blockPos2).isIn(FluidTags.WATER)) continue;
            blockPos = blockPos2;
            break;
        }
        if (blockPos != null) {
            this.mob.getMoveControl().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
        }
    }
}

