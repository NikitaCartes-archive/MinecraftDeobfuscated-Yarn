package net.minecraft.block;

import net.minecraft.class_8293;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SlimeBlock extends TransparentBlock {
	public SlimeBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (entity.bypassesLandingEffects()) {
			super.onLandedUpon(world, state, pos, entity, fallDistance);
		} else {
			entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
		}
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.bypassesLandingEffects()) {
			super.onEntityLand(world, entity);
		} else {
			this.bounce(entity);
		}
	}

	private void bounce(Entity entity) {
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.y < 0.0) {
			double d = entity instanceof LivingEntity ? 1.0 : 0.8;
			double e = -vec3d.y;
			if (class_8293.field_43538.method_50116()) {
				double f = Math.max(0.0, -(2.0 - e));
				e = Math.max(f * 5.5, e);
				e *= 2.0;
			}

			entity.setVelocity(vec3d.x, e * d, vec3d.z);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		double d = Math.abs(entity.getVelocity().y);
		if (d < 0.1 && !entity.bypassesSteppingEffects()) {
			double e = 0.4 + d * 0.2;
			entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	@Override
	public boolean isSticky(BlockState state) {
		return true;
	}

	@Override
	public boolean sticksTo(World world, BlockPos pos, BlockState state, BlockPos otherPos, BlockState otherState, Direction face, Direction otherFace) {
		return otherState.isOf(Blocks.HONEY_BLOCK)
			? false
			: !class_8293.field_43578.method_50116() || !otherState.getCullingFace(world, otherPos, face.getOpposite()).isEmpty();
	}
}
