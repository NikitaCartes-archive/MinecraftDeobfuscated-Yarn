package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class TallPlantBlock extends PlantBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

	public TallPlantBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		if (direction.getAxis() != Direction.Axis.Y
			|| doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP)
			|| blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf) {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return Blocks.AIR.getDefaultState();
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return blockPos.getY() < 255 && itemPlacementContext.getWorld().getBlockState(blockPos.up()).canReplace(itemPlacementContext)
			? super.getPlacementState(itemPlacementContext)
			: null;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.setBlockState(blockPos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		if (blockState.get(HALF) != DoubleBlockHalf.UPPER) {
			return super.canPlaceAt(blockState, worldView, blockPos);
		} else {
			BlockState blockState2 = worldView.getBlockState(blockPos.method_10074());
			return blockState2.getBlock() == this && blockState2.get(HALF) == DoubleBlockHalf.LOWER;
		}
	}

	public void placeAt(IWorld iWorld, BlockPos blockPos, int i) {
		iWorld.setBlockState(blockPos, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER), i);
		iWorld.setBlockState(blockPos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), i);
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.AIR.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.LOWER ? blockPos.up() : blockPos.method_10074();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf) {
			world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 35);
			world.playLevelEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			if (!world.isClient && !playerEntity.isCreative()) {
				dropStacks(blockState, world, blockPos, null, playerEntity, playerEntity.getMainHandStack());
				dropStacks(blockState2, world, blockPos2, null, playerEntity, playerEntity.getMainHandStack());
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HALF);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos.getX(), blockPos.down(blockState.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
	}
}
