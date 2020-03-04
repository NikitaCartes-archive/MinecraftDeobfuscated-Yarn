package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public abstract class AbstractFireBlock extends Block {
	private final float damage;
	protected static final VoxelShape field_22497 = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_22498 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	protected static final VoxelShape field_22499 = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final VoxelShape field_22500 = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_22501 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final VoxelShape field_22502 = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public AbstractFireBlock(Block.Settings settings, float damage) {
		super(settings);
		this.damage = damage;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getState(ctx.getWorld(), ctx.getBlockPos());
	}

	public static BlockState getState(BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.getBlock() == Blocks.SOUL_SOIL ? Blocks.SOUL_FIRE.getDefaultState() : ((FireBlock)Blocks.FIRE).getStateForPosition(world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return field_22498;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(24) == 0) {
			world.playSound(
				(double)((float)pos.getX() + 0.5F),
				(double)((float)pos.getY() + 0.5F),
				(double)((float)pos.getZ() + 0.5F),
				SoundEvents.BLOCK_FIRE_AMBIENT,
				SoundCategory.BLOCKS,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
			if (this.isFlammable(world.getBlockState(pos.west()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble() * 0.1F;
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.east()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)(pos.getX() + 1) - random.nextDouble() * 0.1F;
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.north()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)pos.getZ() + random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.south()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)(pos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.up()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)(pos.getY() + 1) - random.nextDouble() * 0.1F;
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)pos.getX() + random.nextDouble();
				double e = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)pos.getZ() + random.nextDouble();
				world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	protected abstract boolean isFlammable(BlockState state);

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.isFireImmune()
			&& (!(entity instanceof LivingEntity) || !EnchantmentHelper.hasFrostWalker((LivingEntity)entity))
			&& !entity.isTouchingWaterOrRain()) {
			entity.setFireTicks(entity.getFireTicks() + 1);
			if (entity.getFireTicks() == 0) {
				entity.setOnFireFor(8);
			}

			entity.damage(DamageSource.IN_FIRE, this.damage);
		}

		super.onEntityCollision(state, world, pos, entity);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		if (oldState.getBlock() != state.getBlock()) {
			if (world.dimension.getType() != DimensionType.OVERWORLD && world.dimension.getType() != DimensionType.THE_NETHER
				|| !NetherPortalBlock.createPortalAt(world, pos)) {
				if (!state.canPlaceAt(world, pos)) {
					world.removeBlock(pos, false);
				}
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onBlockRemoved(state, world, pos, newState, moved);
		world.playLevelEvent(null, 1009, pos, 0);
	}
}
