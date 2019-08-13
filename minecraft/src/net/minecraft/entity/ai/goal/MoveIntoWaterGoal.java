package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveIntoWaterGoal extends Goal {
	private final MobEntityWithAi mob;

	public MoveIntoWaterGoal(MobEntityWithAi mobEntityWithAi) {
		this.mob = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.mob.onGround && !this.mob.world.getFluidState(new BlockPos(this.mob)).matches(FluidTags.field_15517);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.iterate(
			MathHelper.floor(this.mob.x - 2.0),
			MathHelper.floor(this.mob.y - 2.0),
			MathHelper.floor(this.mob.z - 2.0),
			MathHelper.floor(this.mob.x + 2.0),
			MathHelper.floor(this.mob.y),
			MathHelper.floor(this.mob.z + 2.0)
		)) {
			if (this.mob.world.getFluidState(blockPos2).matches(FluidTags.field_15517)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.mob.getMoveControl().moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
		}
	}
}
