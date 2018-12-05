package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final IntegerProperty field_10897 = Properties.POWER;
	public static final BooleanProperty field_10899 = Properties.INVERTED;
	protected static final VoxelShape field_10898 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	public DaylightDetectorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10897, Integer.valueOf(0)).with(field_10899, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10898;
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return (Integer)blockState.get(field_10897);
	}

	public static void method_9983(BlockState blockState, World world, BlockPos blockPos) {
		if (world.dimension.method_12451()) {
			int i = world.getLightLevel(LightType.field_9284, blockPos) - world.getAmbientDarkness();
			float f = world.method_8442(1.0F);
			boolean bl = (Boolean)blockState.get(field_10899);
			if (bl) {
				i = 15 - i;
			} else if (i > 0) {
				float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
				f += (g - f) * 0.2F;
				i = Math.round((float)i * MathHelper.cos(f));
			}

			i = MathHelper.clamp(i, 0, 15);
			if ((Integer)blockState.get(field_10897) != i) {
				world.setBlockState(blockPos, blockState.with(field_10897, Integer.valueOf(i)), 3);
			}
		}
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (playerEntity.canModifyWorld()) {
			if (world.isRemote) {
				return true;
			} else {
				BlockState blockState2 = blockState.method_11572(field_10899);
				world.setBlockState(blockPos, blockState2, 4);
				method_9983(blockState2, world, blockPos);
				return true;
			}
		} else {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, direction, f, g, h);
		}
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new DaylightDetectorBlockEntity();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10897, field_10899);
	}
}
