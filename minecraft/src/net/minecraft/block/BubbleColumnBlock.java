package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
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
	public static final BooleanProperty field_10680 = Properties.field_12526;

	public BubbleColumnBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10680, Boolean.valueOf(true)));
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		BlockState blockState2 = world.method_8320(blockPos.up());
		if (blockState2.isAir()) {
			entity.onBubbleColumnSurfaceCollision((Boolean)blockState.method_11654(field_10680));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 2; i++) {
					serverWorld.spawnParticles(
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
					serverWorld.spawnParticles(
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
			entity.onBubbleColumnCollision((Boolean)blockState.method_11654(field_10680));
		}
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		update(world, blockPos.up(), calculateDrag(world, blockPos.down()));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		update(world, blockPos.up(), calculateDrag(world, blockPos));
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return Fluids.WATER.method_15729(false);
	}

	public static void update(IWorld iWorld, BlockPos blockPos, boolean bl) {
		if (isStillWater(iWorld, blockPos)) {
			iWorld.method_8652(blockPos, Blocks.field_10422.method_9564().method_11657(field_10680, Boolean.valueOf(bl)), 2);
		}
	}

	public static boolean isStillWater(IWorld iWorld, BlockPos blockPos) {
		FluidState fluidState = iWorld.method_8316(blockPos);
		return iWorld.method_8320(blockPos).getBlock() == Blocks.field_10382 && fluidState.getLevel() >= 8 && fluidState.isStill();
	}

	private static boolean calculateDrag(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.method_8320(blockPos);
		Block block = blockState.getBlock();
		return block == Blocks.field_10422 ? (Boolean)blockState.method_11654(field_10680) : block != Blocks.field_10114;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 5;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		if ((Boolean)blockState.method_11654(field_10680)) {
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
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10382.method_9564();
		} else {
			if (direction == Direction.field_11033) {
				iWorld.method_8652(blockPos, Blocks.field_10422.method_9564().method_11657(field_10680, Boolean.valueOf(calculateDrag(iWorld, blockPos2))), 2);
			} else if (direction == Direction.field_11036 && blockState2.getBlock() != Blocks.field_10422 && isStillWater(iWorld, blockPos2)) {
				iWorld.method_8397().schedule(blockPos, this, this.getTickRate(iWorld));
			}

			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.method_8320(blockPos.down()).getBlock();
		return block == Blocks.field_10422 || block == Blocks.field_10092 || block == Blocks.field_10114;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.method_1073();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9179;
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10680);
	}

	@Override
	public Fluid method_9700(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		iWorld.method_8652(blockPos, Blocks.field_10124.method_9564(), 11);
		return Fluids.WATER;
	}
}
