package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ConnectedPlantBlock extends Block {
	private static final Direction[] FACINGS = Direction.values();
	public static final BooleanProperty NORTH = Properties.NORTH;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty SOUTH = Properties.SOUTH;
	public static final BooleanProperty WEST = Properties.WEST;
	public static final BooleanProperty UP = Properties.UP;
	public static final BooleanProperty DOWN = Properties.DOWN;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.field_11043, NORTH);
		enumMap.put(Direction.field_11034, EAST);
		enumMap.put(Direction.field_11035, SOUTH);
		enumMap.put(Direction.field_11039, WEST);
		enumMap.put(Direction.field_11036, UP);
		enumMap.put(Direction.field_11033, DOWN);
	});
	protected final VoxelShape[] CONNECTIONS_TO_SHAPE;

	protected ConnectedPlantBlock(float f, Block.Settings settings) {
		super(settings);
		this.CONNECTIONS_TO_SHAPE = this.generateFacingsToShapeMap(f);
	}

	private VoxelShape[] generateFacingsToShapeMap(float f) {
		float g = 0.5F - f;
		float h = 0.5F + f;
		VoxelShape voxelShape = Block.createCuboidShape(
			(double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F)
		);
		VoxelShape[] voxelShapes = new VoxelShape[FACINGS.length];

		for (int i = 0; i < FACINGS.length; i++) {
			Direction direction = FACINGS[i];
			voxelShapes[i] = VoxelShapes.cuboid(
				0.5 + Math.min((double)(-f), (double)direction.getOffsetX() * 0.5),
				0.5 + Math.min((double)(-f), (double)direction.getOffsetY() * 0.5),
				0.5 + Math.min((double)(-f), (double)direction.getOffsetZ() * 0.5),
				0.5 + Math.max((double)f, (double)direction.getOffsetX() * 0.5),
				0.5 + Math.max((double)f, (double)direction.getOffsetY() * 0.5),
				0.5 + Math.max((double)f, (double)direction.getOffsetZ() * 0.5)
			);
		}

		VoxelShape[] voxelShapes2 = new VoxelShape[64];

		for (int j = 0; j < 64; j++) {
			VoxelShape voxelShape2 = voxelShape;

			for (int k = 0; k < FACINGS.length; k++) {
				if ((j & 1 << k) != 0) {
					voxelShape2 = VoxelShapes.union(voxelShape2, voxelShapes[k]);
				}
			}

			voxelShapes2[j] = voxelShape2;
		}

		return voxelShapes2;
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.CONNECTIONS_TO_SHAPE[this.getConnectionMask(blockState)];
	}

	protected int getConnectionMask(BlockState blockState) {
		int i = 0;

		for (int j = 0; j < FACINGS.length; j++) {
			if ((Boolean)blockState.get((Property)FACING_PROPERTIES.get(FACINGS[j]))) {
				i |= 1 << j;
			}
		}

		return i;
	}
}
