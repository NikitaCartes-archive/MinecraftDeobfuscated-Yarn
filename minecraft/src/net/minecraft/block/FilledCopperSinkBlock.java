package net.minecraft.block;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FilledCopperSinkBlock extends AbstractCauldronBlock {
	public FilledCopperSinkBlock(AbstractBlock.Settings settings) {
		super(settings, CauldronBehavior.FILLED_COPPER_SINK_BEHAVIOR);
	}

	@Override
	public boolean isFull(BlockState state) {
		return true;
	}

	@Override
	protected double getFluidHeight(BlockState state) {
		return 0.9375;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
			entity.extinguish();
		}
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 3;
	}
}
