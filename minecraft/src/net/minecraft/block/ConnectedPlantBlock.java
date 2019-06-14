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
	public static final BooleanProperty field_11332 = Properties.field_12489;
	public static final BooleanProperty field_11335 = Properties.field_12487;
	public static final BooleanProperty field_11331 = Properties.field_12540;
	public static final BooleanProperty field_11328 = Properties.field_12527;
	public static final BooleanProperty field_11327 = Properties.field_12519;
	public static final BooleanProperty field_11330 = Properties.field_12546;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = SystemUtil.consume(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.field_11043, field_11332);
		enumMap.put(Direction.field_11034, field_11335);
		enumMap.put(Direction.field_11035, field_11331);
		enumMap.put(Direction.field_11039, field_11328);
		enumMap.put(Direction.field_11036, field_11327);
		enumMap.put(Direction.field_11033, field_11330);
	});
	protected final VoxelShape[] field_11333;

	protected ConnectedPlantBlock(float f, Block.Settings settings) {
		super(settings);
		this.field_11333 = this.method_10370(f);
	}

	private VoxelShape[] method_10370(float f) {
		float g = 0.5F - f;
		float h = 0.5F + f;
		VoxelShape voxelShape = Block.method_9541(
			(double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F), (double)(h * 16.0F)
		);
		VoxelShape[] voxelShapes = new VoxelShape[FACINGS.length];

		for (int i = 0; i < FACINGS.length; i++) {
			Direction direction = FACINGS[i];
			voxelShapes[i] = VoxelShapes.method_1081(
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
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.field_11333[this.method_10368(blockState)];
	}

	protected int method_10368(BlockState blockState) {
		int i = 0;

		for (int j = 0; j < FACINGS.length; j++) {
			if ((Boolean)blockState.method_11654((Property)FACING_PROPERTIES.get(FACINGS[j]))) {
				i |= 1 << j;
			}
		}

		return i;
	}
}
