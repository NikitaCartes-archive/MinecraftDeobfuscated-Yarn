package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class DeepslateBlockSource implements BlockSource {
	private static final int DEFAULT_MIN_Y = -8;
	private static final int MAX_Y = 0;
	private final long seed;
	private final BlockState defaultBlock;
	private final BlockState deepslateState;
	private final ChunkGeneratorSettings settings;

	public DeepslateBlockSource(long seed, BlockState defaultBlock, BlockState deepslateState, ChunkGeneratorSettings settings) {
		this.seed = seed;
		this.defaultBlock = defaultBlock;
		this.deepslateState = deepslateState;
		this.settings = settings;
	}

	@Override
	public BlockState sample(int x, int y, int z) {
		if (!this.settings.hasDeepslate()) {
			return this.defaultBlock;
		} else if (y < -8) {
			return this.deepslateState;
		} else if (y > 0) {
			return this.defaultBlock;
		} else {
			double d = (double)MathHelper.method_37490((float)y, -8.0F, 0.0F, 1.0F, 0.0F);
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setDeepslateSeed(this.seed, x, y, z);
			return (double)chunkRandom.nextFloat() < d ? this.deepslateState : this.defaultBlock;
		}
	}
}
