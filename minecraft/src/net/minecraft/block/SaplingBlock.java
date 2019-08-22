package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SaplingBlock extends PlantBlock implements Fertilizable {
	public static final IntProperty STAGE = Properties.STAGE;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
	private final SaplingGenerator generator;

	protected SaplingBlock(SaplingGenerator saplingGenerator, Block.Settings settings) {
		super(settings);
		this.generator = saplingGenerator;
		this.setDefaultState(this.stateFactory.getDefaultState().with(STAGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.onScheduledTick(blockState, world, blockPos, random);
		if (world.getLightLevel(blockPos.up()) >= 9 && random.nextInt(7) == 0) {
			this.generate(world, blockPos, blockState, random);
		}
	}

	public void generate(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		if ((Integer)blockState.get(STAGE) == 0) {
			iWorld.setBlockState(blockPos, blockState.cycle(STAGE), 4);
		} else {
			this.generator.generate(iWorld, blockPos, blockState, random);
		}
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return (double)world.random.nextFloat() < 0.45;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.generate(world, blockPos, blockState, random);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}
}
