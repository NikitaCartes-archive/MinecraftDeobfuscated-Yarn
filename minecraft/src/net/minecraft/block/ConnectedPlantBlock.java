package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
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
	private static final Direction[] field_11334 = Direction.values();
	public static final BooleanProperty NORTH = Properties.NORTH_BOOL;
	public static final BooleanProperty EAST = Properties.EAST_BOOL;
	public static final BooleanProperty SOUTH = Properties.SOUTH_BOOL;
	public static final BooleanProperty WEST = Properties.WEST_BOOL;
	public static final BooleanProperty UP = Properties.UP_BOOL;
	public static final BooleanProperty DOWN = Properties.DOWN_BOOL;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.NORTH, NORTH);
		enumMap.put(Direction.EAST, EAST);
		enumMap.put(Direction.SOUTH, SOUTH);
		enumMap.put(Direction.WEST, WEST);
		enumMap.put(Direction.UP, UP);
		enumMap.put(Direction.DOWN, DOWN);
	});
	protected final VoxelShape[] field_11333;

	protected ConnectedPlantBlock(float f, Block.Settings settings) {
		super(settings);
		this.field_11333 = this.method_10370(f);
	}

	private VoxelShape[] method_10370(float f) {
		float g = 0.5F - f;
		float h = 0.5F + f;
		VoxelShape voxelShape = Block.createCubeShape(
			(double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F)
		);
		VoxelShape[] voxelShapes = new VoxelShape[field_11334.length];

		for (int i = 0; i < field_11334.length; i++) {
			Direction direction = field_11334[i];
			voxelShapes[i] = VoxelShapes.cube(
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

			for (int k = 0; k < field_11334.length; k++) {
				if ((j & 1 << k) != 0) {
					voxelShape2 = VoxelShapes.method_1084(voxelShape2, voxelShapes[k]);
				}
			}

			voxelShapes2[j] = voxelShape2;
		}

		return voxelShapes2;
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.field_11333[this.method_10368(blockState)];
	}

	protected int method_10368(BlockState blockState) {
		int i = 0;

		for (int j = 0; j < field_11334.length; j++) {
			if ((Boolean)blockState.get((Property)FACING_PROPERTIES.get(field_11334[j]))) {
				i |= 1 << j;
			}
		}

		return i;
	}
}
