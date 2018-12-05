package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlacementEnvironment;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public class class_1342 extends Goal {
	private final MobEntityWithAi field_6408;

	public class_1342(MobEntityWithAi mobEntityWithAi) {
		this.field_6408 = mobEntityWithAi;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		return this.field_6408.getBreath() < 140;
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
		this.method_6252();
	}

	private void method_6252() {
		Iterable<BlockPos.Mutable> iterable = BlockPos.Mutable.method_10068(
			MathHelper.floor(this.field_6408.x - 1.0),
			MathHelper.floor(this.field_6408.y),
			MathHelper.floor(this.field_6408.z - 1.0),
			MathHelper.floor(this.field_6408.x + 1.0),
			MathHelper.floor(this.field_6408.y + 8.0),
			MathHelper.floor(this.field_6408.z + 1.0)
		);
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : iterable) {
			if (this.method_6253(this.field_6408.world, blockPos2)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos == null) {
			blockPos = new BlockPos(this.field_6408.x, this.field_6408.y + 8.0, this.field_6408.z);
		}

		this.field_6408.getNavigation().method_6337((double)blockPos.getX(), (double)(blockPos.getY() + 1), (double)blockPos.getZ(), 1.0);
	}

	@Override
	public void tick() {
		this.method_6252();
		this.field_6408.method_5724(this.field_6408.field_6212, this.field_6408.field_6227, this.field_6408.field_6250, 0.02F);
		this.field_6408.move(MovementType.SELF, this.field_6408.velocityX, this.field_6408.velocityY, this.field_6408.velocityZ);
	}

	private boolean method_6253(ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState = viewableWorld.getBlockState(blockPos);
		return (viewableWorld.getFluidState(blockPos).isEmpty() || blockState.getBlock() == Blocks.field_10422)
			&& blockState.canPlaceAtSide(viewableWorld, blockPos, PlacementEnvironment.field_50);
	}
}
