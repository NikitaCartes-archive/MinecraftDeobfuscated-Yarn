package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;

/**
 * Tracks the size of NBT elements in bytes and in depth. Throws {@link
 * NbtSizeValidationException} if the tracked element becomes larger than {@link
 * #maxBytes} or if the depth exceeds {@link #maxDepth} during addition.
 */
public class NbtTagSizeTracker {
	private static final int DEFAULT_MAX_DEPTH = 512;
	private final long maxBytes;
	private long allocatedBytes;
	private final int maxDepth;
	private int depth;

	public NbtTagSizeTracker(long maxBytes, int maxDepth) {
		this.maxBytes = maxBytes;
		this.maxDepth = maxDepth;
	}

	public static NbtTagSizeTracker of(long maxBytes) {
		return new NbtTagSizeTracker(maxBytes, 512);
	}

	public static NbtTagSizeTracker ofUnlimitedBytes() {
		return new NbtTagSizeTracker(Long.MAX_VALUE, 512);
	}

	public void add(long bytes) {
		this.allocatedBytes += bytes;
		if (this.allocatedBytes > this.maxBytes) {
			throw new NbtSizeValidationException(
				"Tried to read NBT tag that was too big; tried to allocate: " + this.allocatedBytes + " bytes where max allowed: " + this.maxBytes
			);
		}
	}

	public void pushStack() {
		this.depth++;
		if (this.depth > this.maxDepth) {
			throw new NbtSizeValidationException("Tried to read NBT tag with too high complexity, depth > " + this.maxDepth);
		}
	}

	public void popStack() {
		this.depth--;
		if (this.depth < 0) {
			throw new NbtSizeValidationException("NBT-Accounter tried to pop stack-depth at top-level");
		}
	}

	@VisibleForTesting
	public long getAllocatedBytes() {
		return this.allocatedBytes;
	}

	@VisibleForTesting
	public int getDepth() {
		return this.depth;
	}
}
