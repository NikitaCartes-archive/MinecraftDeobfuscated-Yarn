package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class AzaleaBlock extends PlantBlock implements Fertilizable {
	public static final MapCodec<AzaleaBlock> CODEC = createCodec(AzaleaBlock::new);
	private static final VoxelShape SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0), Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 8.0, 10.0)
	);

	@Override
	public MapCodec<AzaleaBlock> getCodec() {
		return CODEC;
	}

	protected AzaleaBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.CLAY) || super.canPlantOnTop(floor, world, pos);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return world.getFluidState(pos.up()).isEmpty();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return (double)world.random.nextFloat() < 0.45;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		SaplingGenerator.AZALEA.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
