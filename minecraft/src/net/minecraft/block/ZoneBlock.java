package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ZoneBlock extends Block {
	protected ZoneBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getZ() + 0.5;

		for (int i = 0; i < 3; i++) {
			if (random.nextBoolean()) {
				world.addParticle(
					ParticleTypes.COMPOSTER,
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
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 3, true, true));
			((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1, true, true));
		}

		if (entity instanceof ItemEntity) {
			entity.setVelocity(entity.getVelocity().multiply(0.0, 2.0, 0.0));
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}
}
