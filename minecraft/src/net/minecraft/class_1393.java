package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class class_1393 extends Goal {
	private final MobEntityWithAi field_6625;

	public class_1393(MobEntityWithAi mobEntityWithAi) {
		this.field_6625 = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		return this.field_6625.onGround && !this.field_6625.world.getFluidState(new BlockPos(this.field_6625)).matches(FluidTags.field_15517);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.Mutable.method_10068(
			MathHelper.floor(this.field_6625.x - 2.0),
			MathHelper.floor(this.field_6625.y - 2.0),
			MathHelper.floor(this.field_6625.z - 2.0),
			MathHelper.floor(this.field_6625.x + 2.0),
			MathHelper.floor(this.field_6625.y),
			MathHelper.floor(this.field_6625.z + 2.0)
		)) {
			if (this.field_6625.world.getFluidState(blockPos2).matches(FluidTags.field_15517)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.field_6625.getMoveControl().method_6239((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
		}
	}
}
