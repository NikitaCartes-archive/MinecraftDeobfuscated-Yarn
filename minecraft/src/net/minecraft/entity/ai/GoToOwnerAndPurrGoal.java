package net.minecraft.entity.ai;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class GoToOwnerAndPurrGoal extends MoveToTargetPosGoal {
	private final CatEntity cat;

	public GoToOwnerAndPurrGoal(CatEntity catEntity, double d, int i) {
		super(catEntity, d, i, 6);
		this.cat = catEntity;
		this.field_6515 = -2;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18407, Goal.class_4134.field_18405));
	}

	@Override
	public boolean canStart() {
		return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.shouldPurr() && super.canStart();
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
	public void onRemove() {
		super.onRemove();
		this.cat.setShouldPurr(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.cat.getSitGoal().setEnabledWithOwner(false);
		if (!this.hasReached()) {
			this.cat.setShouldPurr(false);
		} else if (!this.cat.shouldPurr()) {
			this.cat.setShouldPurr(true);
		}
	}

	@Override
	protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.isAir(blockPos.up()) && viewableWorld.getBlockState(blockPos).getBlock().matches(BlockTags.field_16443);
	}
}
