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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TallPlantBlock extends PlantBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

	public TallPlantBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HALF, DoubleBlockHalf.field_12607));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		if (direction.getAxis() != Direction.Axis.field_11052
			|| doubleBlockHalf == DoubleBlockHalf.field_12607 != (direction == Direction.field_11036)
			|| newState.isOf(this) && newState.get(HALF) != doubleBlockHalf) {
			return doubleBlockHalf == DoubleBlockHalf.field_12607 && direction == Direction.field_11033 && !state.canPlaceAt(world, pos)
				? Blocks.field_10124.getDefaultState()
				: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
		} else {
			return Blocks.field_10124.getDefaultState();
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		return blockPos.getY() < 255 && ctx.getWorld().getBlockState(blockPos.up()).canReplace(ctx) ? super.getPlacementState(ctx) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.field_12609), 3);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		if (state.get(HALF) != DoubleBlockHalf.field_12609) {
			return super.canPlaceAt(state, world, pos);
		} else {
			BlockState blockState = world.getBlockState(pos.method_10074());
			return blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.field_12607;
		}
	}

	public void placeAt(WorldAccess world, BlockPos pos, int flags) {
		world.setBlockState(pos, this.getDefaultState().with(HALF, DoubleBlockHalf.field_12607), flags);
		world.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.field_12609), flags);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient) {
			if (player.isCreative()) {
				method_30036(world, pos, state, player);
			} else {
				dropStacks(state, world, pos, null, player, player.getMainHandStack());
			}
		}

		super.onBreak(world, pos, state, player);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, Blocks.field_10124.getDefaultState(), blockEntity, stack);
	}

	protected static void method_30036(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		if (doubleBlockHalf == DoubleBlockHalf.field_12609) {
			BlockPos blockPos2 = blockPos.method_10074();
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.getBlock() == blockState.getBlock() && blockState2.get(HALF) == DoubleBlockHalf.field_12607) {
				world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 35);
				world.syncWorldEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HALF);
	}

	@Override
	public AbstractBlock.OffsetType getOffsetType() {
		return AbstractBlock.OffsetType.field_10657;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.method_10087(state.get(HALF) == DoubleBlockHalf.field_12607 ? 0 : 1).getY(), pos.getZ());
	}
}
