package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
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
			entity.method_5700((Boolean)blockState.get(DRAG));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 2; i++) {
					serverWorld.method_14199(
						ParticleTypes.field_11202,
						(double)((float)blockPos.getX() + world.random.nextFloat()),
						(double)(blockPos.getY() + 1),
						(double)((float)blockPos.getZ() + world.random.nextFloat()),
						1,
						0.0,
						0.0,
						0.0,
						1.0
					);
					serverWorld.method_14199(
						ParticleTypes.field_11247,
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
			entity.method_5764((Boolean)blockState.get(DRAG));
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		method_9657(world, blockPos.up(), method_9656(world, blockPos.down()));
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		method_9657(world, blockPos.up(), method_9656(world, blockPos));
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.WATER.getState(false);
	}

	public static void method_9657(IWorld iWorld, BlockPos blockPos, boolean bl) {
		if (method_9658(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, Blocks.field_10422.getDefaultState().with(DRAG, Boolean.valueOf(bl)), 2);
		}
	}

	public static boolean method_9658(IWorld iWorld, BlockPos blockPos) {
		FluidState fluidState = iWorld.getFluidState(blockPos);
		return iWorld.getBlockState(blockPos).getBlock() == Blocks.field_10382 && fluidState.getLevel() >= 8 && fluidState.isStill();
	}

	private static boolean method_9656(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block == Blocks.field_10422 ? (Boolean)blockState.get(DRAG) : block != Blocks.field_10114;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 5;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		if ((Boolean)blockState.get(DRAG)) {
			world.addImportantParticle(ParticleTypes.field_11243, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
			if (random.nextInt(200) == 0) {
				world.playSound(d, e, f, SoundEvents.field_14650, SoundCategory.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		} else {
			world.addImportantParticle(ParticleTypes.field_11238, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
			world.addImportantParticle(
				ParticleTypes.field_11238, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0
			);
			if (random.nextInt(200) == 0) {
				world.playSound(d, e, f, SoundEvents.field_15161, SoundCategory.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10382.getDefaultState();
		} else {
			if (direction == Direction.DOWN) {
				iWorld.setBlockState(blockPos, Blocks.field_10422.getDefaultState().with(DRAG, Boolean.valueOf(method_9656(iWorld, blockPos2))), 2);
			} else if (direction == Direction.UP && blockState2.getBlock() != Blocks.field_10422 && method_9658(iWorld, blockPos2)) {
				iWorld.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(iWorld));
			}

			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos.down()).getBlock();
		return block == Blocks.field_10422 || block == Blocks.field_10092 || block == Blocks.field_10114;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(DRAG);
	}

	@Override
	public Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 11);
		return Fluids.WATER;
	}
}
