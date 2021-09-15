package net.minecraft.world.gen.random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BlockPosRandomDeriver {
	private final long seed;

	public BlockPosRandomDeriver(long seed) {
		this.seed = seed;
	}

	public AtomicSimpleRandom createRandom(BlockPos pos) {
		return this.createRandom(pos.getX(), pos.getY(), pos.getZ());
	}

	public AtomicSimpleRandom createRandom(int x, int y, int z) {
		long l = MathHelper.hashCode(x, y, z);
		long m = l ^ this.seed;
		return new AtomicSimpleRandom(m);
	}
}
