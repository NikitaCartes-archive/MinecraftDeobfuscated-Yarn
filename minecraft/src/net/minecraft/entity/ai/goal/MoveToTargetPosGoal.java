package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class MoveToTargetPosGoal extends Goal {
	private static final int MIN_WAITING_TIME = 1200;
	private static final int MAX_TRYING_TIME = 1200;
	private static final int MIN_INTERVAL = 200;
	protected final PathAwareEntity mob;
	public final double speed;
	protected int cooldown;
	protected int tryingTime;
	private int safeWaitingTime;
	protected BlockPos targetPos = BlockPos.ORIGIN;
	private boolean reached;
	private final int range;
	private final int maxYDifference;
	protected int lowestY;

	public MoveToTargetPosGoal(PathAwareEntity mob, double speed, int range) {
		this(mob, speed, range, 1);
	}

	public MoveToTargetPosGoal(PathAwareEntity mob, double speed, int range, int maxYDifference) {
		this.mob = mob;
		this.speed = speed;
		this.range = range;
		this.lowestY = 0;
		this.maxYDifference = maxYDifference;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP));
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			this.cooldown = this.getInterval(this.mob);
			return this.findTargetPos();
		}
	}

	protected int getInterval(PathAwareEntity mob) {
		return toGoalTicks(200 + mob.getRandom().nextInt(200));
	}

	@Override
	public boolean shouldContinue() {
		return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.mob.getWorld(), this.targetPos);
	}

	@Override
	public void start() {
		this.startMovingToTarget();
		this.tryingTime = 0;
		this.safeWaitingTime = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
	}

	protected void startMovingToTarget() {
		this.mob
			.getNavigation()
			.startMovingTo((double)this.targetPos.getX() + 0.5, (double)(this.targetPos.getY() + 1), (double)this.targetPos.getZ() + 0.5, this.speed);
	}

	public double getDesiredDistanceToTarget() {
		return 1.0;
	}

	protected BlockPos getTargetPos() {
		return this.targetPos.up();
	}

	@Override
	public boolean shouldRunEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		BlockPos blockPos = this.getTargetPos();
		if (!blockPos.isWithinDistance(this.mob.getPos(), this.getDesiredDistanceToTarget())) {
			this.reached = false;
			this.tryingTime++;
			if (this.shouldResetPath()) {
				this.mob.getNavigation().startMovingTo((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, this.speed);
			}
		} else {
			this.reached = true;
			this.tryingTime--;
		}
	}

	public boolean shouldResetPath() {
		return this.tryingTime % 40 == 0;
	}

	protected boolean hasReached() {
		return this.reached;
	}

	protected boolean findTargetPos() {
		int i = this.range;
		int j = this.maxYDifference;
		BlockPos blockPos = this.mob.getBlockPos();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = this.lowestY; k <= j; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < i; l++) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						mutable.set(blockPos, m, k - 1, n);
						if (this.mob.isInWalkTargetRange(mutable) && this.isTargetPos(this.mob.getWorld(), mutable)) {
							this.targetPos = mutable;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean isTargetPos(WorldView world, BlockPos pos);
}
