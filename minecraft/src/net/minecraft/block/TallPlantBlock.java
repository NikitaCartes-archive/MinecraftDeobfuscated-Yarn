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
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TallPlantBlock extends PlantBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

	public TallPlantBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(HALF, DoubleBlockHalf.field_12607));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		if (direction.getAxis() != Direction.Axis.Y
			|| doubleBlockHalf == DoubleBlockHalf.field_12607 != (direction == Direction.UP)
			|| blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf) {
			return doubleBlockHalf == DoubleBlockHalf.field_12607 && direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.field_10124.getDefaultState()
				: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return Blocks.field_10124.getDefaultState();
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return blockPos.getY() < 255 && itemPlacementContext.getWorld().getBlockState(blockPos.up()).method_11587(itemPlacementContext)
			? super.getPlacementState(itemPlacementContext)
			: null;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.setBlockState(blockPos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.field_12609), 3);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		if (blockState.get(HALF) != DoubleBlockHalf.field_12609) {
			return super.canPlaceAt(blockState, viewableWorld, blockPos);
		} else {
			BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
			return blockState2.getBlock() == this && blockState2.get(HALF) == DoubleBlockHalf.field_12607;
		}
	}

	public void method_10021(IWorld iWorld, BlockPos blockPos, int i) {
		iWorld.setBlockState(blockPos, this.getDefaultState().with(HALF, DoubleBlockHalf.field_12607), i);
		iWorld.setBlockState(blockPos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.field_12609), i);
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.field_10124.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.field_12607 ? blockPos.up() : blockPos.down();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf) {
			world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 35);
			world.fireWorldEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			if (!world.isClient && !playerEntity.isCreative()) {
				dropStacks(blockState, world, blockPos, null, playerEntity, playerEntity.getMainHandStack());
				dropStacks(blockState2, world, blockPos2, null, playerEntity, playerEntity.getMainHandStack());
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(HALF);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos.getX(), blockPos.down(blockState.get(HALF) == DoubleBlockHalf.field_12607 ? 0 : 1).getY(), blockPos.getZ());
	}
}
