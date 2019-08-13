package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ViewableWorld;

public class BreatheAirGoal extends Goal {
	private final MobEntityWithAi mob;

	public BreatheAirGoal(MobEntityWithAi mobEntityWithAi) {
		this.mob = mobEntityWithAi;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		return this.mob.getBreath() < 140;
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
		Iterable<BlockPos> iterable = BlockPos.iterate(
			MathHelper.floor(this.mob.x - 1.0),
			MathHelper.floor(this.mob.y),
			MathHelper.floor(this.mob.z - 1.0),
			MathHelper.floor(this.mob.x + 1.0),
			MathHelper.floor(this.mob.y + 8.0),
			MathHelper.floor(this.mob.z + 1.0)
		);
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : iterable) {
			if (this.isAirPos(this.mob.world, blockPos2)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos == null) {
			blockPos = new BlockPos(this.mob.x, this.mob.y + 8.0, this.mob.z);
		}

		this.mob.getNavigation().startMovingTo((double)blockPos.getX(), (double)(blockPos.getY() + 1), (double)blockPos.getZ(), 1.0);
	}

	@Override
	public void tick() {
		this.moveToAir();
		this.mob.updateVelocity(0.02F, new Vec3d((double)this.mob.sidewaysSpeed, (double)this.mob.upwardSpeed, (double)this.mob.forwardSpeed));
		this.mob.move(MovementType.field_6308, this.mob.getVelocity());
	}

	private boolean isAirPos(ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState = viewableWorld.getBlockState(blockPos);
		return (viewableWorld.getFluidState(blockPos).isEmpty() || blockState.getBlock() == Blocks.field_10422)
			&& blockState.canPlaceAtSide(viewableWorld, blockPos, BlockPlacementEnvironment.field_50);
	}
}
