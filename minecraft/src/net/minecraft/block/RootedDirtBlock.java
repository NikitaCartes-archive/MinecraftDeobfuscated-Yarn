package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RootedDirtBlock extends Block implements Fertilizable {
	public static final MapCodec<RootedDirtBlock> CODEC = createCodec(RootedDirtBlock::new);

	@Override
	public MapCodec<RootedDirtBlock> getCodec() {
		return CODEC;
	}

	public RootedDirtBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockState(pos.down()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos.down(), Blocks.HANGING_ROOTS.getDefaultState());
	}

	@Override
	public BlockPos getFertilizeParticlePos(BlockPos pos) {
		return pos.down();
	}
}
