package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
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
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.NORTH, NORTH);
		enumMap.put(Direction.EAST, EAST);
		enumMap.put(Direction.SOUTH, SOUTH);
		enumMap.put(Direction.WEST, WEST);
		enumMap.put(Direction.UP, UP);
		enumMap.put(Direction.DOWN, DOWN);
	});
	protected final VoxelShape[] CONNECTIONS_TO_SHAPE;

	protected ConnectedPlantBlock(float radius, Block.Settings settings) {
		super(settings);
		this.CONNECTIONS_TO_SHAPE = this.generateFacingsToShapeMap(radius);
	}

	private VoxelShape[] generateFacingsToShapeMap(float radius) {
		float f = 0.5F - radius;
		float g = 0.5F + radius;
		VoxelShape voxelShape = Block.createCuboidShape(
			(double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F)
		);
		VoxelShape[] voxelShapes = new VoxelShape[FACINGS.length];

		for (int i = 0; i < FACINGS.length; i++) {
			Direction direction = FACINGS[i];
			voxelShapes[i] = VoxelShapes.cuboid(
				0.5 + Math.min((double)(-radius), (double)direction.getOffsetX() * 0.5),
				0.5 + Math.min((double)(-radius), (double)direction.getOffsetY() * 0.5),
				0.5 + Math.min((double)(-radius), (double)direction.getOffsetZ() * 0.5),
				0.5 + Math.max((double)radius, (double)direction.getOffsetX() * 0.5),
				0.5 + Math.max((double)radius, (double)direction.getOffsetY() * 0.5),
				0.5 + Math.max((double)radius, (double)direction.getOffsetZ() * 0.5)
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
	public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return this.CONNECTIONS_TO_SHAPE[this.getConnectionMask(state)];
	}

	protected int getConnectionMask(BlockState state) {
		int i = 0;

		for (int j = 0; j < FACINGS.length; j++) {
			if ((Boolean)state.get((Property)FACING_PROPERTIES.get(FACINGS[j]))) {
				i |= 1 << j;
			}
		}

		return i;
	}
}
