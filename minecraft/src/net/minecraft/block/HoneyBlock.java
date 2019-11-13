package net.minecraft.block;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HoneyBlock extends TransparentBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

	public HoneyBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return SHAPE;
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		this.method_23358(world, pos, entity);
		if (entity.handleFallDamage(distance, 0.2F)) {
			entity.playSound(this.soundGroup.getFallSound(), this.soundGroup.getVolume() * 0.5F, this.soundGroup.getPitch() * 0.75F);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (this.method_23356(pos, entity)) {
			Vec3d vec3d = entity.getVelocity();
			if (vec3d.y < -0.05) {
				if (entity instanceof ServerPlayerEntity && vec3d.y < -0.127) {
					Criterions.SLIDE_DOWN_BLOCK.test((ServerPlayerEntity)entity, world.getBlockState(pos));
				}

				entity.setVelocity(new Vec3d(vec3d.x, -0.05, vec3d.z));
			}

			entity.fallDistance = 0.0F;
			this.method_23357(world, pos, entity);
			if (world.getTime() % 10L == 0L) {
				entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
			}
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	private boolean method_23356(BlockPos blockPos, Entity entity) {
		if (entity.onGround) {
			return false;
		} else if (entity.getY() > (double)blockPos.getY() + 0.9375 - 1.0E-7) {
			return false;
		} else if (entity.getVelocity().y >= -0.04) {
			return false;
		} else {
			double d = Math.abs((double)blockPos.getX() + 0.5 - entity.getX());
			double e = Math.abs((double)blockPos.getZ() + 0.5 - entity.getZ());
			double f = 0.4375 + (double)(entity.getWidth() / 2.0F);
			return d + 1.0E-7 > f || e + 1.0E-7 > f;
		}
	}

	private void method_23357(World world, BlockPos blockPos, Entity entity) {
		float f = entity.getDimensions(EntityPose.STANDING).width;
		this.method_23355(
			entity,
			world,
			blockPos,
			1,
			((double)world.random.nextFloat() - 0.5) * (double)f,
			(double)(world.random.nextFloat() / 2.0F),
			((double)world.random.nextFloat() - 0.5) * (double)f,
			(double)world.random.nextFloat() - 0.5,
			(double)(world.random.nextFloat() - 1.0F),
			(double)world.random.nextFloat() - 0.5
		);
	}

	private void method_23358(World world, BlockPos blockPos, Entity entity) {
		float f = entity.getDimensions(EntityPose.STANDING).width;
		this.method_23355(
			entity,
			world,
			blockPos,
			10,
			((double)world.random.nextFloat() - 0.5) * (double)f,
			0.0,
			((double)world.random.nextFloat() - 0.5) * (double)f,
			(double)world.random.nextFloat() - 0.5,
			0.5,
			(double)world.random.nextFloat() - 0.5
		);
	}

	private void method_23355(Entity entity, World world, BlockPos blockPos, int i, double d, double e, double f, double g, double h, double j) {
		BlockState blockState = world.getBlockState(new BlockPos(blockPos));

		for (int k = 0; k < i; k++) {
			entity.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), entity.getX() + d, entity.getY() + e, entity.getZ() + f, g, h, j);
		}
	}
}
