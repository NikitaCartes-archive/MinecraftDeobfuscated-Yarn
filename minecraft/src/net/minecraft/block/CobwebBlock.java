package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CobwebBlock extends Block {
	public CobwebBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.method_5844(blockState, new Vec3d(0.25, 0.05F, 0.25));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}
}
