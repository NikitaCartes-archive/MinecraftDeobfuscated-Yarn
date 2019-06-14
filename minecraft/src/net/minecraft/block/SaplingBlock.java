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
	public static final IntProperty field_11476 = Properties.field_12549;
	protected static final VoxelShape field_11478 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
	private final SaplingGenerator field_11477;

	protected SaplingBlock(SaplingGenerator saplingGenerator, Block.Settings settings) {
		super(settings);
		this.field_11477 = saplingGenerator;
		this.method_9590(this.field_10647.method_11664().method_11657(field_11476, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11478;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.method_9588(blockState, world, blockPos, random);
		if (world.getLightLevel(blockPos.up()) >= 9 && random.nextInt(7) == 0) {
			this.method_10507(world, blockPos, blockState, random);
		}
	}

	public void method_10507(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		if ((Integer)blockState.method_11654(field_11476) == 0) {
			iWorld.method_8652(blockPos, blockState.method_11572(field_11476), 4);
		} else {
			this.field_11477.method_11431(iWorld, blockPos, blockState, random);
		}
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return (double)world.random.nextFloat() < 0.45;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.method_10507(world, blockPos, blockState, random);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11476);
	}
}
