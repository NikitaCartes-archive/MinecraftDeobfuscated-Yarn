package net.minecraft.block;

import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WebBlock extends Block {
	public WebBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.slowMovement(blockState, 0.25F, 0.05F, 0.25F);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
