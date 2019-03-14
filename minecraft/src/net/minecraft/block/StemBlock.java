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
	public static final IntegerProperty AGE = Properties.AGE_7;
	protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private final GourdBlock gourdBlock;

	protected StemBlock(GourdBlock gourdBlock, Block.Settings settings) {
		super(settings);
		this.gourdBlock = gourdBlock;
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return AGE_TO_SHAPE[blockState.get(AGE)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.onScheduledTick(blockState, world, blockPos, random);
		if (world.getLightLevel(blockPos, 0) >= 9) {
			float f = CropBlock.method_9830(this, world, blockPos);
			if (random.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)blockState.get(AGE);
				if (i < 7) {
					blockState = blockState.with(AGE, Integer.valueOf(i + 1));
					world.setBlockState(blockPos, blockState, 2);
				} else {
					Direction direction = Direction.Type.HORIZONTAL.random(random);
					BlockPos blockPos2 = blockPos.offset(direction);
					Block block = world.getBlockState(blockPos2.down()).getBlock();
					if (world.getBlockState(blockPos2).isAir()
						&& (
							block == Blocks.field_10362 || block == Blocks.field_10566 || block == Blocks.field_10253 || block == Blocks.field_10520 || block == Blocks.field_10219
						)) {
						world.setBlockState(blockPos2, this.gourdBlock.getDefaultState());
						world.setBlockState(blockPos, this.gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.field_11177, direction));
					}
				}
			}
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected Item getPickItem() {
		if (this.gourdBlock == Blocks.field_10261) {
			return Items.field_8706;
		} else {
			return this.gourdBlock == Blocks.field_10545 ? Items.field_8188 : null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Item item = this.getPickItem();
		return item == null ? ItemStack.EMPTY : new ItemStack(item);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.get(AGE) != 7;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		int i = Math.min(7, (Integer)blockState.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
		BlockState blockState2 = blockState.with(AGE, Integer.valueOf(i));
		world.setBlockState(blockPos, blockState2, 2);
		if (i == 7) {
			blockState2.scheduledTick(world, blockPos, world.random);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(AGE);
	}

	public GourdBlock getGourdBlock() {
		return this.gourdBlock;
	}
}
