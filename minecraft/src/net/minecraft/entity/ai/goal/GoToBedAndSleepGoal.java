package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

/**
 * Makes a {@link CatEntity} go to a bed and sleep.
 */
public class GoToBedAndSleepGoal extends MoveToTargetPosGoal {
	private final CatEntity cat;

	public GoToBedAndSleepGoal(CatEntity cat, double speed, int range) {
		super(cat, speed, range, 6);
		this.cat = cat;
		this.lowestY = -2;
		this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.isInSleepingPose() && super.canStart();
	}

	@Override
	public void start() {
		super.start();
		this.cat.setInSittingPose(false);
	}

	@Override
	protected int getInterval(PathAwareEntity mob) {
		return 40;
	}

	@Override
	public void stop() {
		super.stop();
		this.cat.setInSleepingPose(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.cat.setInSittingPose(false);
		if (!this.hasReached()) {
			this.cat.setInSleepingPose(false);
		} else if (!this.cat.isInSleepingPose()) {
			this.cat.setInSleepingPose(true);
		}
	}

	@Override
	protected boolean isTargetPos(WorldView world, BlockPos pos) {
		return world.isAir(pos.up()) && world.getBlockState(pos).isIn(BlockTags.BEDS);
	}
}
