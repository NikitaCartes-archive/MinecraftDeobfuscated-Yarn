package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class DeepslateInterpolator implements BlockInterpolator {
	private static final int field_31468 = -8;
	private static final int field_31469 = 0;
	private final ChunkRandom random;
	private final long seed;
	private final BlockState defaultBlock;
	private final BlockState deepslateState;

	public DeepslateInterpolator(long seed, BlockState defaultBlock, BlockState deepslateState) {
		this.random = new ChunkRandom(seed);
		this.seed = seed;
		this.defaultBlock = defaultBlock;
		this.deepslateState = deepslateState;
	}

	@Override
	public BlockState sample(int x, int y, int z, ChunkGeneratorSettings settings) {
		if (!settings.hasDeepslate()) {
			return this.defaultBlock;
		} else if (y < -8) {
			return this.deepslateState;
		} else if (y > 0) {
			return this.defaultBlock;
		} else {
			double d = MathHelper.lerpFromProgress((double)y, -8.0, 0.0, 1.0, 0.0);
			this.random.setGrimstoneSeed(this.seed, x, y, z);
			return (double)this.random.nextFloat() < d ? this.deepslateState : this.defaultBlock;
		}
	}
}
