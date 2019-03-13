package net.minecraft.block;

import java.util.Map;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class HorizontalConnectedBlock extends Block implements Waterloggable {
	public static final BooleanProperty field_10905 = ConnectedPlantBlock.field_11332;
	public static final BooleanProperty field_10907 = ConnectedPlantBlock.field_11335;
	public static final BooleanProperty field_10904 = ConnectedPlantBlock.field_11331;
	public static final BooleanProperty field_10903 = ConnectedPlantBlock.field_11328;
	public static final BooleanProperty field_10900 = Properties.field_12508;
	protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal())
		.collect(SystemUtil.toMap());
	protected final VoxelShape[] field_10901;
	protected final VoxelShape[] field_10906;

	protected HorizontalConnectedBlock(float f, float g, float h, float i, float j, Block.Settings settings) {
		super(settings);
		this.field_10901 = this.method_9984(f, g, j, 0.0F, j);
		this.field_10906 = this.method_9984(f, g, h, 0.0F, i);
	}

	protected VoxelShape[] method_9984(float f, float g, float h, float i, float j) {
		float k = 8.0F - f;
		float l = 8.0F + f;
		float m = 8.0F - g;
		float n = 8.0F + g;
		VoxelShape voxelShape = Block.method_9541((double)k, 0.0, (double)k, (double)l, (double)h, (double)l);
		VoxelShape voxelShape2 = Block.method_9541((double)m, (double)i, 0.0, (double)n, (double)j, (double)n);
		VoxelShape voxelShape3 = Block.method_9541((double)m, (double)i, (double)m, (double)n, (double)j, 16.0);
		VoxelShape voxelShape4 = Block.method_9541(0.0, (double)i, (double)m, (double)n, (double)j, (double)n);
		VoxelShape voxelShape5 = Block.method_9541((double)m, (double)i, (double)m, 16.0, (double)j, (double)n);
		VoxelShape voxelShape6 = VoxelShapes.method_1084(voxelShape2, voxelShape5);
		VoxelShape voxelShape7 = VoxelShapes.method_1084(voxelShape3, voxelShape4);
		VoxelShape[] voxelShapes = new VoxelShape[]{
			VoxelShapes.method_1073(),
			voxelShape3,
			voxelShape4,
			voxelShape7,
			voxelShape2,
			VoxelShapes.method_1084(voxelShape3, voxelShape2),
			VoxelShapes.method_1084(voxelShape4, voxelShape2),
			VoxelShapes.method_1084(voxelShape7, voxelShape2),
			voxelShape5,
			VoxelShapes.method_1084(voxelShape3, voxelShape5),
			VoxelShapes.method_1084(voxelShape4, voxelShape5),
			VoxelShapes.method_1084(voxelShape7, voxelShape5),
			voxelShape6,
			VoxelShapes.method_1084(voxelShape3, voxelShape6),
			VoxelShapes.method_1084(voxelShape4, voxelShape6),
			VoxelShapes.method_1084(voxelShape7, voxelShape6)
		};

		for (int o = 0; o < 16; o++) {
			voxelShapes[o] = VoxelShapes.method_1084(voxelShape, voxelShapes[o]);
		}

		return voxelShapes;
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !(Boolean)blockState.method_11654(field_10900);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.field_10906[this.method_9987(blockState)];
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.field_10901[this.method_9987(blockState)];
	}

	private static int method_9985(Direction direction) {
		return 1 << direction.getHorizontal();
	}

	protected int method_9987(BlockState blockState) {
		int i = 0;
		if ((Boolean)blockState.method_11654(field_10905)) {
			i |= method_9985(Direction.NORTH);
		}

		if ((Boolean)blockState.method_11654(field_10907)) {
			i |= method_9985(Direction.EAST);
		}

		if ((Boolean)blockState.method_11654(field_10904)) {
			i |= method_9985(Direction.SOUTH);
		}

		if ((Boolean)blockState.method_11654(field_10903)) {
			i |= method_9985(Direction.WEST);
		}

		return i;
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_10900) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				return blockState.method_11657(field_10905, blockState.method_11654(field_10904))
					.method_11657(field_10907, blockState.method_11654(field_10903))
					.method_11657(field_10904, blockState.method_11654(field_10905))
					.method_11657(field_10903, blockState.method_11654(field_10907));
			case ROT_270:
				return blockState.method_11657(field_10905, blockState.method_11654(field_10907))
					.method_11657(field_10907, blockState.method_11654(field_10904))
					.method_11657(field_10904, blockState.method_11654(field_10903))
					.method_11657(field_10903, blockState.method_11654(field_10905));
			case ROT_90:
				return blockState.method_11657(field_10905, blockState.method_11654(field_10903))
					.method_11657(field_10907, blockState.method_11654(field_10905))
					.method_11657(field_10904, blockState.method_11654(field_10907))
					.method_11657(field_10903, blockState.method_11654(field_10904));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return blockState.method_11657(field_10905, blockState.method_11654(field_10904)).method_11657(field_10904, blockState.method_11654(field_10905));
			case FRONT_BACK:
				return blockState.method_11657(field_10907, blockState.method_11654(field_10903)).method_11657(field_10903, blockState.method_11654(field_10907));
			default:
				return super.method_9569(blockState, mirror);
		}
	}
}
