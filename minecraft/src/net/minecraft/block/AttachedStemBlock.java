package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class AttachedStemBlock extends PlantBlock {
	public static final DirectionProperty field_9873 = HorizontalFacingBlock.field_11177;
	private final GourdBlock field_9875;
	private static final Map<Direction, VoxelShape> field_9874 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.SOUTH,
			Block.createCubeShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			Direction.WEST,
			Block.createCubeShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			Direction.NORTH,
			Block.createCubeShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			Direction.EAST,
			Block.createCubeShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);

	protected AttachedStemBlock(GourdBlock gourdBlock, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_9873, Direction.NORTH));
		this.field_9875 = gourdBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (VoxelShape)field_9874.get(blockState.get(field_9873));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return blockState2.getBlock() != this.field_9875 && direction == blockState.get(field_9873)
			? this.field_9875.getStem().getDefaultState().with(StemBlock.field_11584, Integer.valueOf(7))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	@Environment(EnvType.CLIENT)
	protected Item method_9337() {
		if (this.field_9875 == Blocks.field_10261) {
			return Items.field_8706;
		} else {
			return this.field_9875 == Blocks.field_10545 ? Items.field_8188 : Items.AIR;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.method_9337());
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_9873, rotation.method_10503(blockState.get(field_9873)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_9873)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_9873);
	}
}
