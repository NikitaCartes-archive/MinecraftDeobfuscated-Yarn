package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class BreakDoorGoal extends DoorInteractGoal {
	private final Predicate<Difficulty> difficultySufficientPredicate;
	protected int breakProgress;
	protected int prevBreakProgress = -1;
	protected int field_16596 = -1;

	public BreakDoorGoal(MobEntity mobEntity, Predicate<Difficulty> predicate) {
		super(mobEntity);
		this.difficultySufficientPredicate = predicate;
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
			return !this.mob.world.getGameRules().getBoolean(GameRules.field_19388)
				? false
				: this.isDifficultySufficient(this.mob.world.getDifficulty()) && !this.method_6256();
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
			&& this.doorPos.isWithinDistance(this.mob.getPos(), 2.0)
			&& this.isDifficultySufficient(this.mob.world.getDifficulty());
	}

	@Override
	public void stop() {
		super.stop();
		this.mob.world.setBlockBreakingProgress(this.mob.getEntityId(), this.doorPos, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.mob.getRand().nextInt(20) == 0) {
			this.mob.world.playLevelEvent(1019, this.doorPos, 0);
			if (!this.mob.isHandSwinging) {
				this.mob.swingHand(this.mob.getActiveHand());
			}
		}

		this.breakProgress++;
		int i = (int)((float)this.breakProgress / (float)this.method_16462() * 10.0F);
		if (i != this.prevBreakProgress) {
			this.mob.world.setBlockBreakingProgress(this.mob.getEntityId(), this.doorPos, i);
			this.prevBreakProgress = i;
		}

		if (this.breakProgress == this.method_16462() && this.isDifficultySufficient(this.mob.world.getDifficulty())) {
			this.mob.world.clearBlockState(this.doorPos, false);
			this.mob.world.playLevelEvent(1021, this.doorPos, 0);
			this.mob.world.playLevelEvent(2001, this.doorPos, Block.getRawIdFromState(this.mob.world.getBlockState(this.doorPos)));
		}
	}

	private boolean isDifficultySufficient(Difficulty difficulty) {
		return this.difficultySufficientPredicate.test(difficulty);
	}
}
