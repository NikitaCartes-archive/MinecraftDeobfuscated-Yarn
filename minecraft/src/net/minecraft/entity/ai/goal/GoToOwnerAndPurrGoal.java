package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.class_4538;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class GoToOwnerAndPurrGoal extends MoveToTargetPosGoal {
	private final CatEntity cat;

	public GoToOwnerAndPurrGoal(CatEntity catEntity, double d, int i) {
		super(catEntity, d, i, 6);
		this.cat = catEntity;
		this.lowestY = -2;
		this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.isSleepingWithOwner() && super.canStart();
	}

	@Override
	public void start() {
		super.start();
		this.cat.getSitGoal().setEnabledWithOwner(false);
	}

	@Override
	protected int getInterval(MobEntityWithAi mobEntityWithAi) {
		return 40;
	}

	@Override
	public void stop() {
		super.stop();
		this.cat.setSleepingWithOwner(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.cat.getSitGoal().setEnabledWithOwner(false);
		if (!this.hasReached()) {
			this.cat.setSleepingWithOwner(false);
		} else if (!this.cat.isSleepingWithOwner()) {
			this.cat.setSleepingWithOwner(true);
		}
	}

	@Override
	protected boolean isTargetPos(class_4538 arg, BlockPos blockPos) {
		return arg.method_22347(blockPos.up()) && arg.getBlockState(blockPos).getBlock().matches(BlockTags.BEDS);
	}
}
