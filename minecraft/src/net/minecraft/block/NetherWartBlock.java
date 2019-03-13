package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NetherWartBlock extends PlantBlock {
	public static final IntegerProperty field_11306 = Properties.field_12497;
	private static final VoxelShape[] field_11305 = new VoxelShape[]{
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
	};

	protected NetherWartBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11306, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11305[blockState.method_11654(field_11306)];
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10114;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = (Integer)blockState.method_11654(field_11306);
		if (i < 3 && random.nextInt(10) == 0) {
			blockState = blockState.method_11657(field_11306, Integer.valueOf(i + 1));
			world.method_8652(blockPos, blockState, 2);
		}

		super.method_9588(blockState, world, blockPos, random);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.field_8790);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11306);
	}
}
