package net.minecraft;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class class_3697 extends MoveToTargetPosGoal {
	private final CatEntity field_16282;

	public class_3697(CatEntity catEntity, double d, int i) {
		super(catEntity, d, i, 6);
		this.field_16282 = catEntity;
		this.field_6515 = -2;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		return this.field_16282.isTamed() && !this.field_16282.isSitting() && !this.field_16282.method_16086() && super.canStart();
	}

	@Override
	public void start() {
		super.start();
		this.field_16282.method_6176().method_6311(false);
	}

	@Override
	protected int getInterval(MobEntityWithAi mobEntityWithAi) {
		return 40;
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.field_16282.method_16088(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_16282.method_6176().method_6311(false);
		if (!this.hasReached()) {
			this.field_16282.method_16088(false);
		} else if (!this.field_16282.method_16086()) {
			this.field_16282.method_16088(true);
		}
	}

	@Override
	protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.isAir(blockPos.up()) && viewableWorld.getBlockState(blockPos).getBlock().matches(BlockTags.field_16443);
	}
}
