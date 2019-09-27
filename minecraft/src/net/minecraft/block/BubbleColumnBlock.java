package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BubbleColumnBlock extends Block implements FluidDrainable {
	public static final BooleanProperty DRAG = Properties.DRAG;

	public BubbleColumnBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(DRAG, Boolean.valueOf(true)));
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		BlockState blockState2 = world.getBlockState(blockPos.up());
		if (blockState2.isAir()) {
			entity.onBubbleColumnSurfaceCollision((Boolean)blockState.get(DRAG));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 2; i++) {
					serverWorld.spawnParticles(
						ParticleTypes.SPLASH,
						(double)((float)blockPos.getX() + world.random.nextFloat()),
						(double)(blockPos.getY() + 1),
						(double)((float)blockPos.getZ() + world.random.nextFloat()),
						1,
						0.0,
						0.0,
						0.0,
						1.0
					);
					serverWorld.spawnParticles(
						ParticleTypes.BUBBLE,
						(double)((float)blockPos.getX() + world.random.nextFloat()),
						(double)(blockPos.getY() + 1),
						(double)((float)blockPos.getZ() + world.random.nextFloat()),
						1,
						0.0,
						0.01,
						0.0,
						0.2
					);
				}
			}
		} else {
			entity.onBubbleColumnCollision((Boolean)blockState.get(DRAG));
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		update(world, blockPos.up(), calculateDrag(world, blockPos.method_10074()));
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		update(serverWorld, blockPos.up(), calculateDrag(serverWorld, blockPos));
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.WATER.getStill(false);
	}

	public static void update(IWorld iWorld, BlockPos blockPos, boolean bl) {
		if (isStillWater(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(bl)), 2);
		}
	}

	public static boolean isStillWater(IWorld iWorld, BlockPos blockPos) {
		FluidState fluidState = iWorld.getFluidState(blockPos);
		return iWorld.getBlockState(blockPos).getBlock() == Blocks.WATER && fluidState.getLevel() >= 8 && fluidState.isStill();
	}

	private static boolean calculateDrag(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block == Blocks.BUBBLE_COLUMN ? (Boolean)blockState.get(DRAG) : block != Blocks.SOUL_SAND;
	}

	@Override
	public int getTickRate(class_4538 arg) {
		return 5;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		if ((Boolean)blockState.get(DRAG)) {
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
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.WATER.getDefaultState();
		} else {
			if (direction == Direction.DOWN) {
				iWorld.setBlockState(blockPos, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(calculateDrag(iWorld, blockPos2))), 2);
			} else if (direction == Direction.UP && blockState2.getBlock() != Blocks.BUBBLE_COLUMN && isStillWater(iWorld, blockPos2)) {
				iWorld.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(iWorld));
			}

			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		Block block = arg.getBlockState(blockPos.method_10074()).getBlock();
		return block == Blocks.BUBBLE_COLUMN || block == Blocks.MAGMA_BLOCK || block == Blocks.SOUL_SAND;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(DRAG);
	}

	@Override
	public Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
		return Fluids.WATER;
	}
}
