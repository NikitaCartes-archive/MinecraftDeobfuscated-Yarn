package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class HorizontalConnectingBlock extends Block implements Waterloggable {
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal())
		.collect(Util.toMap());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] boundingShapes;
	private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

	protected HorizontalConnectingBlock(
		float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, AbstractBlock.Settings settings
	) {
		super(settings);
		this.collisionShapes = this.createShapes(radius1, radius2, collisionHeight, 0.0F, collisionHeight);
		this.boundingShapes = this.createShapes(radius1, radius2, boundingHeight1, 0.0F, boundingHeight2);

		for (BlockState blockState : this.stateManager.getStates()) {
			this.getShapeIndex(blockState);
		}
	}

	@Override
	protected abstract MapCodec<? extends HorizontalConnectingBlock> getCodec();

	protected VoxelShape[] createShapes(float radius1, float radius2, float height1, float offset2, float height2) {
		float f = 8.0F - radius1;
		float g = 8.0F + radius1;
		float h = 8.0F - radius2;
		float i = 8.0F + radius2;
		VoxelShape voxelShape = Block.createCuboidShape((double)f, 0.0, (double)f, (double)g, (double)height1, (double)g);
		VoxelShape voxelShape2 = Block.createCuboidShape((double)h, (double)offset2, 0.0, (double)i, (double)height2, (double)i);
		VoxelShape voxelShape3 = Block.createCuboidShape((double)h, (double)offset2, (double)h, (double)i, (double)height2, 16.0);
		VoxelShape voxelShape4 = Block.createCuboidShape(0.0, (double)offset2, (double)h, (double)i, (double)height2, (double)i);
		VoxelShape voxelShape5 = Block.createCuboidShape((double)h, (double)offset2, (double)h, 16.0, (double)height2, (double)i);
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

		for (int j = 0; j < 16; j++) {
			voxelShapes[j] = VoxelShapes.union(voxelShape, voxelShapes[j]);
		}

		return voxelShapes;
	}

	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return !(Boolean)state.get(WATERLOGGED);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.boundingShapes[this.getShapeIndex(state)];
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.collisionShapes[this.getShapeIndex(state)];
	}

	private static int getDirectionMask(Direction dir) {
		return 1 << dir.getHorizontal();
	}

	protected int getShapeIndex(BlockState state) {
		return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, statex -> {
			int i = 0;
			if ((Boolean)statex.get(NORTH)) {
				i |= getDirectionMask(Direction.NORTH);
			}

			if ((Boolean)statex.get(EAST)) {
				i |= getDirectionMask(Direction.EAST);
			}

			if ((Boolean)statex.get(SOUTH)) {
				i |= getDirectionMask(Direction.SOUTH);
			}

			if ((Boolean)statex.get(WEST)) {
				i |= getDirectionMask(Direction.WEST);
			}

			return i;
		});
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, (Boolean)state.get(SOUTH))
					.with(EAST, (Boolean)state.get(WEST))
					.with(SOUTH, (Boolean)state.get(NORTH))
					.with(WEST, (Boolean)state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, (Boolean)state.get(EAST))
					.with(EAST, (Boolean)state.get(SOUTH))
					.with(SOUTH, (Boolean)state.get(WEST))
					.with(WEST, (Boolean)state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, (Boolean)state.get(WEST))
					.with(EAST, (Boolean)state.get(NORTH))
					.with(SOUTH, (Boolean)state.get(EAST))
					.with(WEST, (Boolean)state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, (Boolean)state.get(SOUTH)).with(SOUTH, (Boolean)state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, (Boolean)state.get(WEST)).with(WEST, (Boolean)state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}
}
