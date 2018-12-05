package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
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
	public static final EnumProperty<BlockHalf> PROPERTY_HALF = Properties.DOOR_HALF;

	public TallPlantBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(PROPERTY_HALF, BlockHalf.field_12607));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		BlockHalf blockHalf = blockState.get(PROPERTY_HALF);
		if (direction.getAxis() != Direction.Axis.Y
			|| blockHalf == BlockHalf.field_12607 != (direction == Direction.UP)
			|| blockState2.getBlock() == this && blockState2.get(PROPERTY_HALF) != blockHalf) {
			return blockHalf == BlockHalf.field_12607 && direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.field_10124.getDefaultState()
				: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return Blocks.field_10124.getDefaultState();
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getPos();
		return blockPos.getY() < 255 && itemPlacementContext.getWorld().getBlockState(blockPos.up()).method_11587(itemPlacementContext)
			? super.getPlacementState(itemPlacementContext)
			: null;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.setBlockState(blockPos.up(), this.getDefaultState().with(PROPERTY_HALF, BlockHalf.field_12609), 3);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		if (blockState.get(PROPERTY_HALF) != BlockHalf.field_12609) {
			return super.canPlaceAt(blockState, viewableWorld, blockPos);
		} else {
			BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
			return blockState2.getBlock() == this && blockState2.get(PROPERTY_HALF) == BlockHalf.field_12607;
		}
	}

	public void method_10021(IWorld iWorld, BlockPos blockPos, int i) {
		iWorld.setBlockState(blockPos, this.getDefaultState().with(PROPERTY_HALF, BlockHalf.field_12607), i);
		iWorld.setBlockState(blockPos.up(), this.getDefaultState().with(PROPERTY_HALF, BlockHalf.field_12609), i);
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.field_10124.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		BlockHalf blockHalf = blockState.get(PROPERTY_HALF);
		BlockPos blockPos2 = blockHalf == BlockHalf.field_12607 ? blockPos.up() : blockPos.down();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(PROPERTY_HALF) != blockHalf) {
			world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 35);
			world.fireWorldEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			if (!world.isRemote && !playerEntity.isCreative()) {
				dropStacks(blockState, world, blockPos, null, playerEntity, playerEntity.getMainHandStack());
				dropStacks(blockState2, world, blockPos2, null, playerEntity, playerEntity.getMainHandStack());
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(PROPERTY_HALF);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getPosRandom(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos.getX(), blockPos.down(blockState.get(PROPERTY_HALF) == BlockHalf.field_12607 ? 0 : 1).getY(), blockPos.getZ());
	}
}
