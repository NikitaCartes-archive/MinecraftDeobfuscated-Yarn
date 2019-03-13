package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class CatSitOnBlockGoal extends MoveToTargetPosGoal {
	private final CatEntity field_6545;

	public CatSitOnBlockGoal(CatEntity catEntity, double d) {
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
	protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
		if (!viewableWorld.method_8623(blockPos.up())) {
			return false;
		} else {
			BlockState blockState = viewableWorld.method_8320(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.field_10034) {
				return ChestBlockEntity.method_11048(viewableWorld, blockPos) < 1;
			} else {
				return block == Blocks.field_10181 && blockState.method_11654(FurnaceBlock.field_11105)
					? true
					: block.method_9525(BlockTags.field_16443) && blockState.method_11654(BedBlock.field_9967) != BedPart.field_12560;
			}
		}
	}
}
