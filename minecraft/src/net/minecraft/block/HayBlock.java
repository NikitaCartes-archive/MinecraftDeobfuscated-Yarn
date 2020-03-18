package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock extends PillarBlock {
	public HayBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		entity.handleFallDamage(distance, 0.2F);
	}
}
