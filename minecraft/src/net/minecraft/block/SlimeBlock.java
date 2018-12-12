package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SlimeBlock extends TransparentBlock {
	public SlimeBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		if (entity.isSneaking()) {
			super.onLandedUpon(world, blockPos, entity, f);
		} else {
			entity.handleFallDamage(f, 0.0F);
		}
	}

	@Override
	public void onEntityLand(BlockView blockView, Entity entity) {
		if (entity.isSneaking()) {
			super.onEntityLand(blockView, entity);
		} else if (entity.velocityY < 0.0) {
			entity.velocityY = -entity.velocityY;
			if (!(entity instanceof LivingEntity)) {
				entity.velocityY *= 0.8;
			}
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		if (Math.abs(entity.velocityY) < 0.1 && !entity.isSneaking()) {
			double d = 0.4 + Math.abs(entity.velocityY) * 0.2;
			entity.velocityX *= d;
			entity.velocityZ *= d;
		}

		super.onSteppedOn(world, blockPos, entity);
	}
}
