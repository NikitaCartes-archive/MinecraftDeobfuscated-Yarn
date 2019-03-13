package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StemBlock extends PlantBlock implements Fertilizable {
	public static final IntegerProperty field_11584 = Properties.field_12550;
	protected static final VoxelShape[] field_11583 = new VoxelShape[]{
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
		Block.method_9541(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private final GourdBlock field_11585;

	protected StemBlock(GourdBlock gourdBlock, Block.Settings settings) {
		super(settings);
		this.field_11585 = gourdBlock;
		this.method_9590(this.field_10647.method_11664().method_11657(field_11584, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11583[blockState.method_11654(field_11584)];
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.method_9588(blockState, world, blockPos, random);
		if (world.method_8624(blockPos, 0) >= 9) {
			float f = CropBlock.method_9830(this, world, blockPos);
			if (random.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)blockState.method_11654(field_11584);
				if (i < 7) {
					blockState = blockState.method_11657(field_11584, Integer.valueOf(i + 1));
					world.method_8652(blockPos, blockState, 2);
				} else {
					Direction direction = Direction.Type.HORIZONTAL.random(random);
					BlockPos blockPos2 = blockPos.method_10093(direction);
					Block block = world.method_8320(blockPos2.down()).getBlock();
					if (world.method_8320(blockPos2).isAir()
						&& (
							block == Blocks.field_10362 || block == Blocks.field_10566 || block == Blocks.field_10253 || block == Blocks.field_10520 || block == Blocks.field_10219
						)) {
						world.method_8501(blockPos2, this.field_11585.method_9564());
						world.method_8501(blockPos, this.field_11585.getAttachedStem().method_9564().method_11657(HorizontalFacingBlock.field_11177, direction));
					}
				}
			}
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected Item getPickItem() {
		if (this.field_11585 == Blocks.field_10261) {
			return Items.field_8706;
		} else {
			return this.field_11585 == Blocks.field_10545 ? Items.field_8188 : null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Item item = this.getPickItem();
		return item == null ? ItemStack.EMPTY : new ItemStack(item);
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.method_11654(field_11584) != 7;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		int i = Math.min(7, (Integer)blockState.method_11654(field_11584) + MathHelper.nextInt(world.random, 2, 5));
		BlockState blockState2 = blockState.method_11657(field_11584, Integer.valueOf(i));
		world.method_8652(blockPos, blockState2, 2);
		if (i == 7) {
			blockState2.method_11585(world, blockPos, world.random);
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11584);
	}

	public GourdBlock method_10694() {
		return this.field_11585;
	}
}
