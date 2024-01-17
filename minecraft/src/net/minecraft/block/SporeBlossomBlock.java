package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SporeBlossomBlock extends Block {
	public static final MapCodec<SporeBlossomBlock> CODEC = createCodec(SporeBlossomBlock::new);
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 13.0, 2.0, 14.0, 16.0, 14.0);
	private static final int field_31252 = 14;
	private static final int field_31253 = 10;
	private static final int field_31254 = 10;

	@Override
	public MapCodec<SporeBlossomBlock> getCodec() {
		return CODEC;
	}

	public SporeBlossomBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN) && !world.isWater(pos);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.UP && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		double d = (double)i + random.nextDouble();
		double e = (double)j + 0.7;
		double f = (double)k + random.nextDouble();
		world.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, d, e, f, 0.0, 0.0, 0.0);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = 0; l < 14; l++) {
			mutable.set(i + MathHelper.nextInt(random, -10, 10), j - random.nextInt(10), k + MathHelper.nextInt(random, -10, 10));
			BlockState blockState = world.getBlockState(mutable);
			if (!blockState.isFullCube(world, mutable)) {
				world.addParticle(
					ParticleTypes.SPORE_BLOSSOM_AIR,
					(double)mutable.getX() + random.nextDouble(),
					(double)mutable.getY() + random.nextDouble(),
					(double)mutable.getZ() + random.nextDouble(),
					0.0,
					0.0,
					0.0
				);
			}
		}
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
}
