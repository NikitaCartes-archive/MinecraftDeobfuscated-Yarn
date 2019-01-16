package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class StandingBannerBlock extends AbstractBannerBlock {
	public static final IntegerProperty field_9924 = Properties.ROTATION_16;
	private static final Map<DyeColor, Block> field_9925 = Maps.<DyeColor, Block>newHashMap();
	private static final VoxelShape field_9923 = Block.createCubeShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	public StandingBannerBlock(DyeColor dyeColor, Block.Settings settings) {
		super(dyeColor, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_9924, Integer.valueOf(0)));
		field_9925.put(dyeColor, this);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.down()).getMaterial().method_15799();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_9923;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState()
			.with(field_9924, Integer.valueOf(MathHelper.floor((double)((180.0F + itemPlacementContext.getPlayerYaw()) * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_9924, Integer.valueOf(rotation.method_10502((Integer)blockState.get(field_9924), 16)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.with(field_9924, Integer.valueOf(mirror.method_10344((Integer)blockState.get(field_9924), 16)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_9924);
	}

	@Environment(EnvType.CLIENT)
	public static Block method_9398(DyeColor dyeColor) {
		return (Block)field_9925.getOrDefault(dyeColor, Blocks.field_10154);
	}
}
