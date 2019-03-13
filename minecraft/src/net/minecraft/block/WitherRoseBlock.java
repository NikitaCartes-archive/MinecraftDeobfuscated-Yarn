package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherRoseBlock extends FlowerBlock {
	public WitherRoseBlock(StatusEffect statusEffect, Block.Settings settings) {
		super(statusEffect, 8, settings);
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Block block = blockState.getBlock();
		return super.method_9695(blockState, blockView, blockPos) || block == Blocks.field_10515 || block == Blocks.field_10114;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		VoxelShape voxelShape = this.method_9530(blockState, world, blockPos, VerticalEntityPosition.minValue());
		Vec3d vec3d = voxelShape.getBoundingBox().method_1005();
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
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
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
