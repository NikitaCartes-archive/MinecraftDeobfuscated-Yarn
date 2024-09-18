package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiFunction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class FlowerbedBlock extends PlantBlock implements Fertilizable {
	public static final MapCodec<FlowerbedBlock> CODEC = createCodec(FlowerbedBlock::new);
	public static final int field_42762 = 1;
	public static final int field_42763 = 4;
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final IntProperty FLOWER_AMOUNT = Properties.FLOWER_AMOUNT;
	private static final BiFunction<Direction, Integer, VoxelShape> FACING_AND_AMOUNT_TO_SHAPE = Util.memoize(
		(BiFunction<Direction, Integer, VoxelShape>)((facing, flowerAmount) -> {
			VoxelShape[] voxelShapes = new VoxelShape[]{
				Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 3.0, 16.0),
				Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 3.0, 8.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 3.0, 8.0),
				Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 3.0, 16.0)
			};
			VoxelShape voxelShape = VoxelShapes.empty();

			for (int i = 0; i < flowerAmount; i++) {
				int j = Math.floorMod(i - facing.getHorizontal(), 4);
				voxelShape = VoxelShapes.union(voxelShape, voxelShapes[j]);
			}

			return voxelShape.asCuboid();
		})
	);

	@Override
	public MapCodec<FlowerbedBlock> getCodec() {
		return CODEC;
	}

	protected FlowerbedBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FLOWER_AMOUNT, Integer.valueOf(1)));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(FLOWER_AMOUNT) < 4 ? true : super.canReplace(state, context);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)FACING_AND_AMOUNT_TO_SHAPE.apply((Direction)state.get(FACING), (Integer)state.get(FLOWER_AMOUNT));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		return blockState.isOf(this)
			? blockState.with(FLOWER_AMOUNT, Integer.valueOf(Math.min(4, (Integer)blockState.get(FLOWER_AMOUNT) + 1)))
			: this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, FLOWER_AMOUNT);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		int i = (Integer)state.get(FLOWER_AMOUNT);
		if (i < 4) {
			world.setBlockState(pos, state.with(FLOWER_AMOUNT, Integer.valueOf(i + 1)), Block.NOTIFY_LISTENERS);
		} else {
			dropStack(world, pos, new ItemStack(this));
		}
	}
}
