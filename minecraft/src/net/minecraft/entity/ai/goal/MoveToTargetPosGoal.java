package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public abstract class MoveToTargetPosGoal extends Goal {
	private final MobEntityWithAi owner;
	public final double speed;
	protected int counter;
	protected int tryingTime;
	private int safeWaitingTime;
	protected BlockPos targetPos = BlockPos.ORIGIN;
	private boolean reached;
	private final int field_6510;
	private final int maxYDifference;
	public int field_6515;

	public MoveToTargetPosGoal(MobEntityWithAi mobEntityWithAi, double d, int i) {
		this(mobEntityWithAi, d, i, 1);
	}

	public MoveToTargetPosGoal(MobEntityWithAi mobEntityWithAi, double d, int i, int j) {
		this.owner = mobEntityWithAi;
		this.speed = d;
		this.field_6510 = i;
		this.field_6515 = 0;
		this.maxYDifference = j;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.counter > 0) {
			this.counter--;
			return false;
		} else {
			this.counter = this.getInterval(this.owner);
			return this.findTargetPos();
		}
	}

	protected int getInterval(MobEntityWithAi mobEntityWithAi) {
		return 200 + mobEntityWithAi.getRand().nextInt(200);
	}

	@Override
	public boolean shouldContinue() {
		return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.owner.world, this.targetPos);
	}

	@Override
	public void start() {
		this.startMovingToTarget();
		this.tryingTime = 0;
		this.safeWaitingTime = this.owner.getRand().nextInt(this.owner.getRand().nextInt(1200) + 1200) + 1200;
	}

	protected void startMovingToTarget() {
		this.owner
			.getNavigation()
			.startMovingTo((double)((float)this.targetPos.getX()) + 0.5, (double)(this.targetPos.getY() + 1), (double)((float)this.targetPos.getZ()) + 0.5, this.speed);
	}

	public double getDesiredSquaredDistanceToTarget() {
		return 1.0;
	}

	@Override
	public void tick() {
		if (this.owner.squaredDistanceToCenter(this.targetPos.up()) > this.getDesiredSquaredDistanceToTarget()) {
			this.reached = false;
			this.tryingTime++;
			if (this.shouldResetPath()) {
				this.owner
					.getNavigation()
					.startMovingTo((double)((float)this.targetPos.getX()) + 0.5, (double)(this.targetPos.getY() + 1), (double)((float)this.targetPos.getZ()) + 0.5, this.speed);
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
		int i = this.field_6510;
		int j = this.maxYDifference;
		BlockPos blockPos = new BlockPos(this.owner);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = this.field_6515; k <= j; k = k > 0 ? -k : 1 - k) {
			for (int l = 0; l < i; l++) {
				for (int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
					for (int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
						mutable.set(blockPos).method_10100(m, k - 1, n);
						if (this.owner.isInAiRange(mutable) && this.isTargetPos(this.owner.world, mutable)) {
							this.targetPos = mutable;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos);
}
