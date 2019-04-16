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
	private final MobEntityWithAi owner;

	public BreatheAirGoal(MobEntityWithAi mobEntityWithAi) {
		this.owner = mobEntityWithAi;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		return this.owner.getBreath() < 140;
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
			MathHelper.floor(this.owner.x - 1.0),
			MathHelper.floor(this.owner.y),
			MathHelper.floor(this.owner.z - 1.0),
			MathHelper.floor(this.owner.x + 1.0),
			MathHelper.floor(this.owner.y + 8.0),
			MathHelper.floor(this.owner.z + 1.0)
		);
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : iterable) {
			if (this.isAirPos(this.owner.world, blockPos2)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos == null) {
			blockPos = new BlockPos(this.owner.x, this.owner.y + 8.0, this.owner.z);
		}

		this.owner.getNavigation().startMovingTo((double)blockPos.getX(), (double)(blockPos.getY() + 1), (double)blockPos.getZ(), 1.0);
	}

	@Override
	public void tick() {
		this.moveToAir();
		this.owner.updateVelocity(0.02F, new Vec3d((double)this.owner.sidewaysSpeed, (double)this.owner.upwardSpeed, (double)this.owner.forwardSpeed));
		this.owner.move(MovementType.field_6308, this.owner.getVelocity());
	}

	private boolean isAirPos(ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState = viewableWorld.getBlockState(blockPos);
		return (viewableWorld.getFluidState(blockPos).isEmpty() || blockState.getBlock() == Blocks.field_10422)
			&& blockState.canPlaceAtSide(viewableWorld, blockPos, BlockPlacementEnvironment.field_50);
	}
}
