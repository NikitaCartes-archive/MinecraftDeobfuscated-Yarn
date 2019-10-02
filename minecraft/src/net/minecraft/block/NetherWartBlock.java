package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class NetherWartBlock extends PlantBlock {
	public static final IntProperty AGE = Properties.AGE_3;
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
	};

	protected NetherWartBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return AGE_TO_SHAPE[blockState.get(AGE)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.SOUL_SAND;
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		int i = (Integer)blockState.get(AGE);
		if (i < 3 && random.nextInt(10) == 0) {
			blockState = blockState.with(AGE, Integer.valueOf(i + 1));
			serverWorld.setBlockState(blockPos, blockState, 2);
		}

		super.scheduledTick(blockState, serverWorld, blockPos, random);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.NETHER_WART);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
