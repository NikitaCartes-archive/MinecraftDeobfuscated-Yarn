package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock extends AbstractSkullBlock {
	public static final IntProperty ROTATION = Properties.ROTATION;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

	protected SkullBlock(SkullBlock.SkullType skullType, Block.Settings settings) {
		super(skullType, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(ROTATION, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(ROTATION, Integer.valueOf(MathHelper.floor((double)(ctx.getPlayerYaw() * 16.0F / 360.0F) + 0.5) & 15));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, Integer.valueOf(rotation.rotate((Integer)state.get(ROTATION), 16)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, Integer.valueOf(mirror.mirror((Integer)state.get(ROTATION), 16)));
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
		DRAGON;
	}
}
