package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveIntoWaterGoal extends Goal {
	private final MobEntityWithAi mob;

	public MoveIntoWaterGoal(MobEntityWithAi mob) {
		this.mob = mob;
	}

	@Override
	public boolean canStart() {
		return this.mob.method_24828() && !this.mob.world.getFluidState(this.mob.getSenseCenterPos()).matches(FluidTags.WATER);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.iterate(
			MathHelper.floor(this.mob.getX() - 2.0),
			MathHelper.floor(this.mob.getY() - 2.0),
			MathHelper.floor(this.mob.getZ() - 2.0),
			MathHelper.floor(this.mob.getX() + 2.0),
			MathHelper.floor(this.mob.getY()),
			MathHelper.floor(this.mob.getZ() + 2.0)
		)) {
			if (this.mob.world.getFluidState(blockPos2).matches(FluidTags.WATER)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.mob.getMoveControl().moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
		}
	}
}
