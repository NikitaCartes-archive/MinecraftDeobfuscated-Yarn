package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class GrimstoneInterpolator implements BlockInterpolator {
	private final ChunkRandom random;
	private final long seed;
	private final BlockState defaultBlock;
	private final BlockState grimstone;

	public GrimstoneInterpolator(long seed, BlockState defaultBlock, BlockState grimstone) {
		this.random = new ChunkRandom(seed);
		this.seed = seed;
		this.defaultBlock = defaultBlock;
		this.grimstone = grimstone;
	}

	@Override
	public BlockState sample(int x, int y, int z, ChunkGeneratorSettings settings) {
		if (!settings.hasGrimstone()) {
			return this.defaultBlock;
		} else {
			this.random.setGrimstoneSeed(this.seed, x, y, z);
			double d = MathHelper.clampedLerpFromProgress((double)y, -8.0, 0.0, 1.0, 0.0);
			return (double)this.random.nextFloat() < d ? this.grimstone : this.defaultBlock;
		}
	}
}
