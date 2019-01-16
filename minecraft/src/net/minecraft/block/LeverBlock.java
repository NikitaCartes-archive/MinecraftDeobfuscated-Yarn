package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeverBlock extends WallMountedBlock {
	public static final BooleanProperty field_11265 = Properties.POWERED;
	protected static final VoxelShape field_11267 = Block.createCubeShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final VoxelShape field_11263 = Block.createCubeShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final VoxelShape field_11260 = Block.createCubeShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final VoxelShape field_11262 = Block.createCubeShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final VoxelShape field_11264 = Block.createCubeShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final VoxelShape field_11261 = Block.createCubeShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final VoxelShape field_11268 = Block.createCubeShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final VoxelShape field_11266 = Block.createCubeShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	protected LeverBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11177, Direction.NORTH)
				.with(field_11265, Boolean.valueOf(false))
				.with(field_11007, WallMountLocation.field_12471)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch ((WallMountLocation)blockState.get(field_11007)) {
			case field_12475:
				switch (((Direction)blockState.get(field_11177)).getAxis()) {
					case X:
						return field_11261;
					case Z:
					default:
						return field_11264;
				}
			case field_12471:
				switch ((Direction)blockState.get(field_11177)) {
					case EAST:
						return field_11262;
					case WEST:
						return field_11260;
					case SOUTH:
						return field_11263;
					case NORTH:
					default:
						return field_11267;
				}
			case field_12473:
			default:
				switch (((Direction)blockState.get(field_11177)).getAxis()) {
					case X:
						return field_11266;
					case Z:
					default:
						return field_11268;
				}
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		blockState = blockState.method_11572(field_11265);
		boolean bl = (Boolean)blockState.get(field_11265);
		if (world.isClient) {
			if (bl) {
				method_10308(blockState, world, blockPos, 1.0F);
			}

			return true;
		} else {
			world.setBlockState(blockPos, blockState, 3);
			float f = bl ? 0.6F : 0.5F;
			world.playSound(null, blockPos, SoundEvents.field_14962, SoundCategory.field_15245, 0.3F, f);
			this.method_10309(blockState, world, blockPos);
			return true;
		}
	}

	private static void method_10308(BlockState blockState, IWorld iWorld, BlockPos blockPos, float f) {
		Direction direction = ((Direction)blockState.get(field_11177)).getOpposite();
		Direction direction2 = method_10119(blockState).getOpposite();
		double d = (double)blockPos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
		double e = (double)blockPos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
		double g = (double)blockPos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
		iWorld.addParticle(new DustParticleParameters(1.0F, 0.0F, 0.0F, f), d, e, g, 0.0, 0.0, 0.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_11265) && random.nextFloat() < 0.25F) {
			method_10308(blockState, world, blockPos, 0.5F);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.get(field_11265)) {
				this.method_10309(blockState, world, blockPos);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_11265) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_11265) && method_10119(blockState) == direction ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	private void method_10309(BlockState blockState, World world, BlockPos blockPos) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.offset(method_10119(blockState).getOpposite()), this);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11007, field_11177, field_11265);
	}
}
