package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

public class BreakDoorGoal extends DoorInteractGoal {
	private static final int MIN_MAX_PROGRESS = 240;
	private final Predicate<Difficulty> difficultySufficientPredicate;
	protected int breakProgress;
	protected int prevBreakProgress = -1;
	protected int maxProgress = -1;

	public BreakDoorGoal(MobEntity mob, Predicate<Difficulty> difficultySufficientPredicate) {
		super(mob);
		this.difficultySufficientPredicate = difficultySufficientPredicate;
	}

	public BreakDoorGoal(MobEntity mob, int maxProgress, Predicate<Difficulty> difficultySufficientPredicate) {
		this(mob, difficultySufficientPredicate);
		this.maxProgress = maxProgress;
	}

	protected int getMaxProgress() {
		return Math.max(240, this.maxProgress);
	}

	@Override
	public boolean canStart() {
		if (!super.canStart()) {
			return false;
		} else {
			return !this.mob.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
				? false
				: this.isDifficultySufficient(this.mob.getWorld().getDifficulty()) && !this.isDoorOpen();
		}
	}

	@Override
	public void start() {
		super.start();
		this.breakProgress = 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.breakProgress <= this.getMaxProgress()
			&& !this.isDoorOpen()
			&& this.doorPos.isWithinDistance(this.mob.getPos(), 2.0)
			&& this.isDifficultySufficient(this.mob.getWorld().getDifficulty());
	}

	@Override
	public void stop() {
		super.stop();
		this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.doorPos, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.mob.getRandom().nextInt(20) == 0) {
			this.mob.getWorld().syncWorldEvent(WorldEvents.ZOMBIE_ATTACKS_WOODEN_DOOR, this.doorPos, 0);
			if (!this.mob.handSwinging) {
				this.mob.swingHand(this.mob.getActiveHand());
			}
		}

		this.breakProgress++;
		int i = (int)((float)this.breakProgress / (float)this.getMaxProgress() * 10.0F);
		if (i != this.prevBreakProgress) {
			this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.doorPos, i);
			this.prevBreakProgress = i;
		}

		if (this.breakProgress == this.getMaxProgress() && this.isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
			this.mob.getWorld().removeBlock(this.doorPos, false);
			this.mob.getWorld().syncWorldEvent(WorldEvents.ZOMBIE_BREAKS_WOODEN_DOOR, this.doorPos, 0);
			this.mob.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.doorPos, Block.getRawIdFromState(this.mob.getWorld().getBlockState(this.doorPos)));
		}
	}

	private boolean isDifficultySufficient(Difficulty difficulty) {
		return this.difficultySufficientPredicate.test(difficulty);
	}
}
