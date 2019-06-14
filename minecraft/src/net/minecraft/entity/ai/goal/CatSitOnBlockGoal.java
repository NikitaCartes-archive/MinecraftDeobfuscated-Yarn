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
	private final CatEntity cat;

	public CatSitOnBlockGoal(CatEntity catEntity, double d) {
		super(catEntity, d, 8);
		this.cat = catEntity;
	}

	@Override
	public boolean canStart() {
		return this.cat.isTamed() && !this.cat.isSitting() && super.canStart();
	}

	@Override
	public void start() {
		super.start();
		this.cat.getSitGoal().setEnabledWithOwner(false);
	}

	@Override
	public void stop() {
		super.stop();
		this.cat.setSitting(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.cat.getSitGoal().setEnabledWithOwner(false);
		if (!this.hasReached()) {
			this.cat.setSitting(false);
		} else if (!this.cat.isSitting()) {
			this.cat.setSitting(true);
		}
	}

	@Override
	protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
		if (!viewableWorld.isAir(blockPos.up())) {
			return false;
		} else {
			BlockState blockState = viewableWorld.method_8320(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.field_10034) {
				return ChestBlockEntity.getPlayersLookingInChestCount(viewableWorld, blockPos) < 1;
			} else {
				return block == Blocks.field_10181 && blockState.method_11654(FurnaceBlock.field_11105)
					? true
					: block.matches(BlockTags.field_16443) && blockState.method_11654(BedBlock.field_9967) != BedPart.field_12560;
			}
		}
	}
}
