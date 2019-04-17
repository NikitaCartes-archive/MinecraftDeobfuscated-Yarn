package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class MagmaBlock extends Block {
	public MagmaBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(DamageSource.HOT_FLOOR, 1.0F);
		}

		super.onSteppedOn(world, blockPos, entity);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getBlockBrightness(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		return 15728880;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		BubbleColumnBlock.update(world, blockPos.up(), true);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.UP && blockState2.getBlock() == Blocks.field_10382) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(iWorld));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void onRandomTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		BlockPos blockPos2 = blockPos.up();
		if (world.getFluidState(blockPos).matches(FluidTags.field_15517)) {
			world.playSound(
				null, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
			);
			if (world instanceof ServerWorld) {
				((ServerWorld)world)
					.spawnParticles(
						ParticleTypes.field_11237, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0
					);
			}
		}
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 20;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return entityType.isFireImmune();
	}

	@Override
	public boolean shouldPostProcess(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
