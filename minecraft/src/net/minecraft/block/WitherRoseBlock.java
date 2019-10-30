package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
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
	public WitherRoseBlock(StatusEffect effect, Block.Settings settings) {
		super(effect, 8, settings);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
		Block block = floor.getBlock();
		return super.canPlantOnTop(floor, view, pos) || block == Blocks.NETHERRACK || block == Blocks.SOUL_SAND;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		VoxelShape voxelShape = this.getOutlineShape(state, world, pos, EntityContext.absent());
		Vec3d vec3d = voxelShape.getBoundingBox().getCenter();
		double d = (double)pos.getX() + vec3d.x;
		double e = (double)pos.getZ() + vec3d.z;

		for (int i = 0; i < 3; i++) {
			if (random.nextBoolean()) {
				world.addParticle(
					ParticleTypes.SMOKE,
					d + (double)(random.nextFloat() / 5.0F),
					(double)pos.getY() + (0.5 - (double)random.nextFloat()),
					e + (double)(random.nextFloat() / 5.0F),
					0.0,
					0.0,
					0.0
				);
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (!livingEntity.isInvulnerableTo(DamageSource.WITHER)) {
					livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40));
				}
			}
		}
	}
}
