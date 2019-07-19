package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager;
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

	protected SaplingBlock(SaplingGenerator generator, Block.Settings settings) {
		super(settings);
		this.generator = generator;
		this.setDefaultState(this.stateManager.getDefaultState().with(STAGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		super.onScheduledTick(state, world, pos, random);
		if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
			this.generate(world, pos, state, random);
		}
	}

	public void generate(IWorld world, BlockPos pos, BlockState state, Random random) {
		if ((Integer)state.get(STAGE) == 0) {
			world.setBlockState(pos, state.cycle(STAGE), 4);
		} else {
			this.generator.generate(world, pos, state, random);
		}
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return (double)world.random.nextFloat() < 0.45;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.generate(world, pos, state, random);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}
}
