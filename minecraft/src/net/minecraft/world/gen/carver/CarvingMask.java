package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class CarvingMask {
	private final int bottomY;
	private final BitSet mask;
	private CarvingMask.MaskPredicate maskPredicate = (offsetX, y, offsetZ) -> false;

	public CarvingMask(int height, int bottomY) {
		this.bottomY = bottomY;
		this.mask = new BitSet(256 * height);
	}

	public void setMaskPredicate(CarvingMask.MaskPredicate maskPredicate) {
		this.maskPredicate = maskPredicate;
	}

	public CarvingMask(long[] mask, int bottomY) {
		this.bottomY = bottomY;
		this.mask = BitSet.valueOf(mask);
	}

	private int getIndex(int offsetX, int y, int offsetZ) {
		return offsetX & 15 | (offsetZ & 15) << 4 | y - this.bottomY << 8;
	}

	public void set(int offsetX, int y, int offsetZ) {
		this.mask.set(this.getIndex(offsetX, y, offsetZ));
	}

	public boolean get(int offsetX, int y, int offsetZ) {
		return this.maskPredicate.test(offsetX, y, offsetZ) || this.mask.get(this.getIndex(offsetX, y, offsetZ));
	}

	public Stream<BlockPos> streamBlockPos(ChunkPos chunkPos) {
		return this.mask.stream().mapToObj(mask -> {
			int i = mask & 15;
			int j = mask >> 4 & 15;
			int k = mask >> 8;
			return chunkPos.getBlockPos(i, k + this.bottomY, j);
		});
	}

	public long[] getMask() {
		return this.mask.toLongArray();
	}

	public interface MaskPredicate {
		boolean test(int offsetX, int y, int offsetZ);
	}
}
