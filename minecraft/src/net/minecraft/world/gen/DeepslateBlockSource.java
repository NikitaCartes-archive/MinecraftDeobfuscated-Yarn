package net.minecraft.world.gen;

import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class DeepslateBlockSource implements BlockSource {
	private static final int field_31468 = -8;
	private static final int field_31469 = 0;
	private final ChunkRandom random;
	private final long seed;
	private final BlockState defaultBlock;
	private final BlockState deepslateState;
	private final Supplier<ChunkGeneratorSettings> settings;

	public DeepslateBlockSource(long seed, BlockState defaultBlock, BlockState deepslateState, Supplier<ChunkGeneratorSettings> settings) {
		this.random = new ChunkRandom(seed);
		this.seed = seed;
		this.defaultBlock = defaultBlock;
		this.deepslateState = deepslateState;
		this.settings = settings;
	}

	@Override
	public BlockState sample(int x, int y, int z) {
		if (!((ChunkGeneratorSettings)this.settings.get()).hasDeepslate()) {
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
