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
			return !this.owner.field_6002.getGameRules().getBoolean("mobGriefing") ? false : !this.method_6256();
		}
	}

	@Override
	public void start() {
		super.start();
		this.breakProgress = 0;
	}

	@Override
	public boolean shouldContinue() {
		double d = this.owner.method_5831(this.field_6414);
		return this.breakProgress <= this.method_16462() && !this.method_6256() && d < 4.0;
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.owner.field_6002.method_8517(this.owner.getEntityId(), this.field_6414, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.owner.getRand().nextInt(20) == 0) {
			this.owner.field_6002.method_8535(1019, this.field_6414, 0);
			if (!this.owner.field_6252) {
				this.owner.swingHand(this.owner.getActiveHand());
			}
		}

		this.breakProgress++;
		int i = (int)((float)this.breakProgress / (float)this.method_16462() * 10.0F);
		if (i != this.prevBreakProgress) {
			this.owner.field_6002.method_8517(this.owner.getEntityId(), this.field_6414, i);
			this.prevBreakProgress = i;
		}

		if (this.breakProgress == this.method_16462() && this.owner.field_6002.getDifficulty() == Difficulty.HARD) {
			this.owner.field_6002.method_8650(this.field_6414);
			this.owner.field_6002.method_8535(1021, this.field_6414, 0);
			this.owner.field_6002.method_8535(2001, this.field_6414, Block.method_9507(this.owner.field_6002.method_8320(this.field_6414)));
		}
	}
}
