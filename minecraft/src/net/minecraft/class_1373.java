package net.minecraft;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class class_1373 extends MoveToTargetPosGoal {
	private final CatEntity field_6545;

	public class_1373(CatEntity catEntity, double d) {
		super(catEntity, d, 8);
		this.field_6545 = catEntity;
	}

	@Override
	public boolean canStart() {
		return this.field_6545.isTamed() && !this.field_6545.isSitting() && super.canStart();
	}

	@Override
	public void start() {
		super.start();
		this.field_6545.method_6176().method_6311(false);
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.field_6545.setSitting(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_6545.method_6176().method_6311(false);
		if (!this.hasReached()) {
			this.field_6545.setSitting(false);
		} else if (!this.field_6545.isSitting()) {
			this.field_6545.setSitting(true);
		}
	}

	@Override
	protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
		if (!viewableWorld.isAir(blockPos.up())) {
			return false;
		} else {
			BlockState blockState = viewableWorld.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.field_10034) {
				return ChestBlockEntity.method_11048(viewableWorld, blockPos) < 1;
			} else {
				return block == Blocks.field_10181 && blockState.get(FurnaceBlock.field_11105)
					? true
					: block.matches(BlockTags.field_16443) && blockState.get(BedBlock.field_9967) != BedPart.field_12560;
			}
		}
	}
}
