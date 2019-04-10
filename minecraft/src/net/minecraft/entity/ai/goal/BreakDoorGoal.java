package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;

public class BreakDoorGoal extends DoorInteractGoal {
	private final Predicate<Difficulty> field_19003;
	protected int breakProgress;
	protected int prevBreakProgress = -1;
	protected int field_16596 = -1;

	public BreakDoorGoal(MobEntity mobEntity, Predicate<Difficulty> predicate) {
		super(mobEntity);
		this.field_19003 = predicate;
	}

	public BreakDoorGoal(MobEntity mobEntity, int i, Predicate<Difficulty> predicate) {
		this(mobEntity, predicate);
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
			return !this.owner.world.getGameRules().getBoolean("mobGriefing") ? false : this.method_19994(this.owner.world.getDifficulty()) && !this.method_6256();
		}
	}

	@Override
	public void start() {
		super.start();
		this.breakProgress = 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.breakProgress <= this.method_16462()
			&& !this.method_6256()
			&& this.doorPos.isWithinDistance(this.owner.getPos(), 2.0)
			&& this.method_19994(this.owner.world.getDifficulty());
	}

	@Override
	public void stop() {
		super.stop();
		this.owner.world.setBlockBreakingProgress(this.owner.getEntityId(), this.doorPos, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.owner.getRand().nextInt(20) == 0) {
			this.owner.world.method_20290(1019, this.doorPos, 0);
			if (!this.owner.isHandSwinging) {
				this.owner.swingHand(this.owner.getActiveHand());
			}
		}

		this.breakProgress++;
		int i = (int)((float)this.breakProgress / (float)this.method_16462() * 10.0F);
		if (i != this.prevBreakProgress) {
			this.owner.world.setBlockBreakingProgress(this.owner.getEntityId(), this.doorPos, i);
			this.prevBreakProgress = i;
		}

		if (this.breakProgress == this.method_16462() && this.method_19994(this.owner.world.getDifficulty())) {
			this.owner.world.clearBlockState(this.doorPos, false);
			this.owner.world.method_20290(1021, this.doorPos, 0);
			this.owner.world.method_20290(2001, this.doorPos, Block.getRawIdFromState(this.owner.world.getBlockState(this.doorPos)));
		}
	}

	private boolean method_19994(Difficulty difficulty) {
		return this.field_19003.test(difficulty);
	}
}
