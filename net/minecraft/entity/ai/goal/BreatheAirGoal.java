/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;

public class BreatheAirGoal
extends Goal {
    private final PathAwareEntity mob;

    public BreatheAirGoal(PathAwareEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.mob.getAir() < 140;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        this.moveToAir();
    }

    private void moveToAir() {
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 1.0), this.mob.getBlockY(), MathHelper.floor(this.mob.getZ() - 1.0), MathHelper.floor(this.mob.getX() + 1.0), MathHelper.floor(this.mob.getY() + 8.0), MathHelper.floor(this.mob.getZ() + 1.0));
        Vec3i blockPos = null;
        for (BlockPos blockPos2 : iterable) {
            if (!this.isAirPos(this.mob.world, blockPos2)) continue;
            blockPos = blockPos2;
            break;
        }
        if (blockPos == null) {
            blockPos = new BlockPos(this.mob.getX(), this.mob.getY() + 8.0, this.mob.getZ());
        }
        this.mob.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), 1.0);
    }

    @Override
    public void tick() {
        this.moveToAir();
        this.mob.updateVelocity(0.02f, new Vec3d(this.mob.sidewaysSpeed, this.mob.upwardSpeed, this.mob.forwardSpeed));
        this.mob.move(MovementType.SELF, this.mob.getVelocity());
    }

    private boolean isAirPos(WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return (world.getFluidState(pos).isEmpty() || blockState.isOf(Blocks.BUBBLE_COLUMN)) && blockState.canPathfindThrough(world, pos, NavigationType.LAND);
    }
}

