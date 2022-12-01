package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;

/**
 * Tracks the size of NBT elements. Throws {@link RuntimeException} if the
 * tracked element becomes larger than {@link #maxBytes} during addition.
 */
public class NbtTagSizeTracker {
	public static final NbtTagSizeTracker EMPTY = new NbtTagSizeTracker(0L) {
		@Override
		public void add(long bytes) {
		}
	};
	private final long maxBytes;
	private long allocatedBytes;

	public NbtTagSizeTracker(long maxBytes) {
		this.maxBytes = maxBytes;
	}

	public void add(long bytes) {
		this.allocatedBytes += bytes;
		if (this.allocatedBytes > this.maxBytes) {
			throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.allocatedBytes + "bytes where max allowed: " + this.maxBytes);
		}
	}

	@VisibleForTesting
	public long getAllocatedBytes() {
		return this.allocatedBytes;
	}
}
