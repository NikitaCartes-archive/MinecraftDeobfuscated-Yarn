package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class WitherRoseBlock extends FlowerBlock {
	public WitherRoseBlock(StatusEffect statusEffect, Block.Settings settings) {
		super(statusEffect, 8, settings);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		return blockState2.hasSolidTopSurface(viewableWorld, blockPos2) && block != Blocks.field_10613;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		VoxelShape voxelShape = this.getBoundingShape(blockState, world, blockPos);
		Vec3d vec3d = voxelShape.getBoundingBox().getCenter();
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getZ() + vec3d.z;

		for (int i = 0; i < 3; i++) {
			if (random.nextBoolean()) {
				world.method_8406(
					ParticleTypes.field_11251,
					d + (double)(random.nextFloat() / 5.0F),
					(double)blockPos.getY() + (0.5 - (double)random.nextFloat()),
					e + (double)(random.nextFloat() / 5.0F),
					0.0,
					0.0,
					0.0
				);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (!livingEntity.isInvulnerableTo(DamageSource.WITHER)) {
					livingEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 40));
				}
			}
		}
	}
}
