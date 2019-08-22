package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
		if (entity.method_21750()) {
			super.onLandedUpon(world, blockPos, entity, f);
		} else {
			entity.handleFallDamage(f, 0.0F);
		}
	}

	@Override
	public void onEntityLand(BlockView blockView, Entity entity) {
		if (entity.method_21750()) {
			super.onEntityLand(blockView, entity);
		} else {
			this.method_21847(entity);
		}
	}

	private void method_21847(Entity entity) {
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.y < 0.0) {
			double d = entity instanceof LivingEntity ? 1.0 : 0.8;
			entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		double d = Math.abs(entity.getVelocity().y);
		if (d < 0.1 && !entity.method_21749()) {
			double e = 0.4 + d * 0.2;
			entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
		}

		super.onSteppedOn(world, blockPos, entity);
	}
}
