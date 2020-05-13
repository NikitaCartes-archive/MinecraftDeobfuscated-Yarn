package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TorchBlock extends Block {
	protected static final VoxelShape BOUNDING_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
	protected final ParticleEffect particle;

	protected TorchBlock(AbstractBlock.Settings settings, ParticleEffect particle) {
		super(settings);
		this.particle = particle;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return sideCoversSmallSquare(world, pos.down(), Direction.UP);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX() + 0.5;
		double e = (double)pos.getY() + 0.7;
		double f = (double)pos.getZ() + 0.5;
		world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
		world.addParticle(this.particle, d, e, f, 0.0, 0.0, 0.0);
	}
}
