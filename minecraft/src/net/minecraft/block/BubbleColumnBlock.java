package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BubbleColumnBlock extends Block implements FluidDrainable {
	public static final BooleanProperty DRAG = Properties.DRAG;

	public BubbleColumnBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(DRAG, Boolean.valueOf(true)));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockState blockState = world.getBlockState(pos.up());
		if (blockState.isAir()) {
			entity.onBubbleColumnSurfaceCollision((Boolean)state.get(DRAG));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 2; i++) {
					serverWorld.spawnParticles(
						ParticleTypes.SPLASH,
						(double)pos.getX() + world.random.nextDouble(),
						(double)(pos.getY() + 1),
						(double)pos.getZ() + world.random.nextDouble(),
						1,
						0.0,
						0.0,
						0.0,
						1.0
					);
					serverWorld.spawnParticles(
						ParticleTypes.BUBBLE,
						(double)pos.getX() + world.random.nextDouble(),
						(double)(pos.getY() + 1),
						(double)pos.getZ() + world.random.nextDouble(),
						1,
						0.0,
						0.01,
						0.0,
						0.2
					);
				}
			}
		} else {
			entity.onBubbleColumnCollision((Boolean)state.get(DRAG));
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		update(world, pos.up(), calculateDrag(world, pos));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	public static void update(WorldAccess world, BlockPos pos, boolean drag) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		while (isStillWater(world, mutable)) {
			world.setBlockState(mutable, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(drag)), 2);
			mutable.move(Direction.UP);
		}
	}

	public static boolean isStillWater(WorldAccess world, BlockPos pos) {
		FluidState fluidState = world.getFluidState(pos);
		return world.getBlockState(pos).isOf(Blocks.WATER) && fluidState.getLevel() >= 8 && fluidState.isStill();
	}

	private static boolean calculateDrag(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.isOf(Blocks.BUBBLE_COLUMN) ? (Boolean)blockState.get(DRAG) : !blockState.isOf(Blocks.SOUL_SAND);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		if ((Boolean)state.get(DRAG)) {
			world.addImportantParticle(ParticleTypes.CURRENT_DOWN, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
			if (random.nextInt(200) == 0) {
				world.playSound(
					d,
					e,
					f,
					SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
					SoundCategory.BLOCKS,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		} else {
			world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
			world.addImportantParticle(
				ParticleTypes.BUBBLE_COLUMN_UP, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0
			);
			if (random.nextInt(200) == 0) {
				world.playSound(
					d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false
				);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (!state.canPlaceAt(world, pos)) {
			return Blocks.WATER.getDefaultState();
		} else {
			if (direction == Direction.DOWN) {
				world.setBlockState(pos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(calculateDrag(world, neighborPos))), 2);
			} else if (direction == Direction.UP && !neighborState.isOf(Blocks.BUBBLE_COLUMN) && isStillWater(world, neighborPos)) {
				world.getBlockTickScheduler().schedule(pos, this, 5);
			}

			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		return blockState.isOf(Blocks.BUBBLE_COLUMN) || blockState.isOf(Blocks.MAGMA_BLOCK) || blockState.isOf(Blocks.SOUL_SAND);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(DRAG);
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
		return new ItemStack(Items.WATER_BUCKET);
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Fluids.WATER.getBucketFillSound();
	}
}
