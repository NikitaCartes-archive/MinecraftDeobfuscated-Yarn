package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveIntoWaterGoal extends Goal {
	private final MobEntityWithAi owner;

	public MoveIntoWaterGoal(MobEntityWithAi mobEntityWithAi) {
		this.owner = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.owner.onGround && !this.owner.field_6002.method_8316(new BlockPos(this.owner)).method_15767(FluidTags.field_15517);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(
			MathHelper.floor(this.owner.x - 2.0),
			MathHelper.floor(this.owner.y - 2.0),
			MathHelper.floor(this.owner.z - 2.0),
			MathHelper.floor(this.owner.x + 2.0),
			MathHelper.floor(this.owner.y),
			MathHelper.floor(this.owner.z + 2.0)
		)) {
			if (this.owner.field_6002.method_8316(blockPos2).method_15767(FluidTags.field_15517)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.owner.method_5962().moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
		}
	}
}
