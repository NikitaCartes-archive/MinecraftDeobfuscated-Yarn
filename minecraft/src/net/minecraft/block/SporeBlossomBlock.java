package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SporeBlossomBlock extends Block {
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 13.0, 2.0, 14.0, 16.0, 14.0);

	public SporeBlossomBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		double d = (double)i + random.nextDouble();
		double e = (double)j + 0.7;
		double f = (double)k + random.nextDouble();
		world.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
}
