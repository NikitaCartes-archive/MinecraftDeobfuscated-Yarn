package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;

public class BreakDoorGoal extends DoorInteractGoal {
	protected int breakProgress;
	protected int prevBreakProgress = -1;
	protected int field_16596 = -1;

	public BreakDoorGoal(MobEntity mobEntity) {
		super(mobEntity);
	}

	public BreakDoorGoal(MobEntity mobEntity, int i) {
		this(mobEntity);
		this.field_16596 = i;
	}

	protected int method_16462() {
		return Math.max(240, this.field_16596);
	}

	@Override
	public boolean canStart() {
		if (!super.canStart()) {
			return false;
		} else {
			return !this.owner.world.getGameRules().getBoolean("mobGriefing") ? false : !this.method_6256();
		}
	}

	@Override
	public void start() {
		super.start();
		this.breakProgress = 0;
	}

	@Override
	public boolean shouldContinue() {
		double d = this.owner.squaredDistanceTo(this.doorPos);
		return this.breakProgress <= this.method_16462() && !this.method_6256() && d < 4.0;
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.owner.world.setBlockBreakingProgress(this.owner.getEntityId(), this.doorPos, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.owner.getRand().nextInt(20) == 0) {
			this.owner.world.fireWorldEvent(1019, this.doorPos, 0);
			if (!this.owner.field_6252) {
				this.owner.swingHand(this.owner.getActiveHand());
			}
		}

		this.breakProgress++;
		int i = (int)((float)this.breakProgress / (float)this.method_16462() * 10.0F);
		if (i != this.prevBreakProgress) {
			this.owner.world.setBlockBreakingProgress(this.owner.getEntityId(), this.doorPos, i);
			this.prevBreakProgress = i;
		}

		if (this.breakProgress == this.method_16462() && this.owner.world.getDifficulty() == Difficulty.HARD) {
			this.owner.world.clearBlockState(this.doorPos);
			this.owner.world.fireWorldEvent(1021, this.doorPos, 0);
			this.owner.world.fireWorldEvent(2001, this.doorPos, Block.getRawIdFromState(this.owner.world.getBlockState(this.doorPos)));
		}
	}
}
