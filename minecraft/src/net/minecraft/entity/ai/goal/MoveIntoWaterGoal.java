package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveIntoWaterGoal extends Goal {
	private final PathAwareEntity mob;

	public MoveIntoWaterGoal(PathAwareEntity mob) {
		this.mob = mob;
	}

	@Override
	public boolean canStart() {
		return this.mob.isOnGround() && !this.mob.getWorld().getFluidState(this.mob.getBlockPos()).isIn(FluidTags.WATER);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.iterate(
			MathHelper.floor(this.mob.getX() - 2.0),
			MathHelper.floor(this.mob.getY() - 2.0),
			MathHelper.floor(this.mob.getZ() - 2.0),
			MathHelper.floor(this.mob.getX() + 2.0),
			this.mob.getBlockY(),
			MathHelper.floor(this.mob.getZ() + 2.0)
		)) {
			if (this.mob.getWorld().getFluidState(blockPos2).isIn(FluidTags.WATER)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.mob.getMoveControl().moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
		}
	}
}
