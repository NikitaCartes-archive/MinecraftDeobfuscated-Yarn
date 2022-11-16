package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock extends AbstractSkullBlock {
	public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
	private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
	public static final IntProperty ROTATION = Properties.ROTATION;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
	protected static final VoxelShape PIGLIN_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

	protected SkullBlock(SkullBlock.SkullType skullType, AbstractBlock.Settings settings) {
		super(skullType, settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getSkullType() == SkullBlock.Type.PIGLIN ? PIGLIN_SHAPE : SHAPE;
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(ROTATION, Integer.valueOf(RotationPropertyHelper.fromYaw(ctx.getPlayerYaw() + 180.0F)));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, Integer.valueOf(rotation.rotate((Integer)state.get(ROTATION), MAX_ROTATIONS)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, Integer.valueOf(mirror.mirror((Integer)state.get(ROTATION), MAX_ROTATIONS)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ROTATION);
	}

	public interface SkullType {
	}

	public static enum Type implements SkullBlock.SkullType {
		SKELETON,
		WITHER_SKELETON,
		PLAYER,
		ZOMBIE,
		CREEPER,
		PIGLIN,
		DRAGON;
	}
}
