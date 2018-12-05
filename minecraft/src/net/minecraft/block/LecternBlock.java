package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class LecternBlock extends BlockWithEntity {
	public static final DirectionProperty field_16404 = HorizontalFacingBlock.field_11177;
	public static final VoxelShape field_16406 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final VoxelShape field_16405 = Block.createCubeShape(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
	public static final VoxelShape field_16403 = VoxelShapes.method_1084(field_16406, field_16405);

	protected LecternBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_16404, Direction.NORTH));
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_16404, itemPlacementContext.method_8042().getOpposite());
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_16403;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_16403;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_16404, rotation.method_10503(blockState.get(field_16404)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_16404)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16404);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new LecternBlockEntity();
	}
}
