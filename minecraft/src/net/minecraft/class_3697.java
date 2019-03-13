package net.minecraft;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
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
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18407, Goal.class_4134.field_18405));
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
	protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8623(blockPos.up()) && viewableWorld.method_8320(blockPos).getBlock().method_9525(BlockTags.field_16443);
	}
}
