package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class HorizontalConnectedBlock extends Block implements Waterloggable {
	public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal())
		.collect(SystemUtil.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] boundingShapes;
	private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

	protected HorizontalConnectedBlock(float f, float g, float h, float i, float j, Block.Settings settings) {
		super(settings);
		this.collisionShapes = this.createShapes(f, g, j, 0.0F, j);
		this.boundingShapes = this.createShapes(f, g, h, 0.0F, i);
	}

	protected VoxelShape[] createShapes(float f, float g, float h, float i, float j) {
		float k = 8.0F - f;
		float l = 8.0F + f;
		float m = 8.0F - g;
		float n = 8.0F + g;
		VoxelShape voxelShape = Block.createCuboidShape((double)k, 0.0, (double)k, (double)l, (double)h, (double)l);
		VoxelShape voxelShape2 = Block.createCuboidShape((double)m, (double)i, 0.0, (double)n, (double)j, (double)n);
		VoxelShape voxelShape3 = Block.createCuboidShape((double)m, (double)i, (double)m, (double)n, (double)j, 16.0);
		VoxelShape voxelShape4 = Block.createCuboidShape(0.0, (double)i, (double)m, (double)n, (double)j, (double)n);
		VoxelShape voxelShape5 = Block.createCuboidShape((double)m, (double)i, (double)m, 16.0, (double)j, (double)n);
		VoxelShape voxelShape6 = VoxelShapes.union(voxelShape2, voxelShape5);
		VoxelShape voxelShape7 = VoxelShapes.union(voxelShape3, voxelShape4);
		VoxelShape[] voxelShapes = new VoxelShape[]{
			VoxelShapes.empty(),
			voxelShape3,
			voxelShape4,
			voxelShape7,
			voxelShape2,
			VoxelShapes.union(voxelShape3, voxelShape2),
			VoxelShapes.union(voxelShape4, voxelShape2),
			VoxelShapes.union(voxelShape7, voxelShape2),
			voxelShape5,
			VoxelShapes.union(voxelShape3, voxelShape5),
			VoxelShapes.union(voxelShape4, voxelShape5),
			VoxelShapes.union(voxelShape7, voxelShape5),
			voxelShape6,
			VoxelShapes.union(voxelShape3, voxelShape6),
			VoxelShapes.union(voxelShape4, voxelShape6),
			VoxelShapes.union(voxelShape7, voxelShape6)
		};

		for (int o = 0; o < 16; o++) {
			voxelShapes[o] = VoxelShapes.union(voxelShape, voxelShapes[o]);
		}

		return voxelShapes;
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !(Boolean)blockState.get(WATERLOGGED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.boundingShapes[this.getShapeIndex(blockState)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.collisionShapes[this.getShapeIndex(blockState)];
	}

	private static int getDirectionMask(Direction direction) {
		return 1 << direction.getHorizontal();
	}

	protected int getShapeIndex(BlockState blockState) {
		return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(blockState, blockStatex -> {
			int i = 0;
			if ((Boolean)blockStatex.get(NORTH)) {
				i |= getDirectionMask(Direction.field_11043);
			}

			if ((Boolean)blockStatex.get(EAST)) {
				i |= getDirectionMask(Direction.field_11034);
			}

			if ((Boolean)blockStatex.get(SOUTH)) {
				i |= getDirectionMask(Direction.field_11035);
			}

			if ((Boolean)blockStatex.get(WEST)) {
				i |= getDirectionMask(Direction.field_11039);
			}

			return i;
		});
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				return blockState.with(NORTH, blockState.get(SOUTH)).with(EAST, blockState.get(WEST)).with(SOUTH, blockState.get(NORTH)).with(WEST, blockState.get(EAST));
			case field_11465:
				return blockState.with(NORTH, blockState.get(EAST)).with(EAST, blockState.get(SOUTH)).with(SOUTH, blockState.get(WEST)).with(WEST, blockState.get(NORTH));
			case field_11463:
				return blockState.with(NORTH, blockState.get(WEST)).with(EAST, blockState.get(NORTH)).with(SOUTH, blockState.get(EAST)).with(WEST, blockState.get(SOUTH));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		switch (blockMirror) {
			case field_11300:
				return blockState.with(NORTH, blockState.get(SOUTH)).with(SOUTH, blockState.get(NORTH));
			case field_11301:
				return blockState.with(EAST, blockState.get(WEST)).with(WEST, blockState.get(EAST));
			default:
				return super.mirror(blockState, blockMirror);
		}
	}
}
