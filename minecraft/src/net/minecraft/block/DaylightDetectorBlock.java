package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends BlockWithEntity {
	public static final IntegerProperty field_10897 = Properties.field_12511;
	public static final BooleanProperty field_10899 = Properties.field_12501;
	protected static final VoxelShape field_10898 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

	public DaylightDetectorBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10897, Integer.valueOf(0)).method_11657(field_10899, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10898;
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return (Integer)blockState.method_11654(field_10897);
	}

	public static void method_9983(BlockState blockState, World world, BlockPos blockPos) {
		if (world.field_9247.hasSkyLight()) {
			int i = world.method_8314(LightType.SKY, blockPos) - world.getAmbientDarkness();
			float f = world.method_8442(1.0F);
			boolean bl = (Boolean)blockState.method_11654(field_10899);
			if (bl) {
				i = 15 - i;
			} else if (i > 0) {
				float g = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
				f += (g - f) * 0.2F;
				i = Math.round((float)i * MathHelper.cos(f));
			}

			i = MathHelper.clamp(i, 0, 15);
			if ((Integer)blockState.method_11654(field_10897) != i) {
				world.method_8652(blockPos, blockState.method_11657(field_10897, Integer.valueOf(i)), 3);
			}
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (playerEntity.canModifyWorld()) {
			if (world.isClient) {
				return true;
			} else {
				BlockState blockState2 = blockState.method_11572(field_10899);
				world.method_8652(blockPos, blockState2, 4);
				method_9983(blockState2, world, blockPos);
				return true;
			}
		} else {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		}
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new DaylightDetectorBlockEntity();
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10897, field_10899);
	}
}
