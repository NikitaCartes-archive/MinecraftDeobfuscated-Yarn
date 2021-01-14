package net.minecraft.nbt;

public class NbtTagSizeTracker {
	public static final NbtTagSizeTracker EMPTY = new NbtTagSizeTracker(0L) {
		@Override
		public void add(long bits) {
		}
	};
	private final long maxBytes;
	private long allocatedBytes;

	public NbtTagSizeTracker(long maxBytes) {
		this.maxBytes = maxBytes;
	}

	public void add(long bits) {
		this.allocatedBytes += bits / 8L;
		if (this.allocatedBytes > this.maxBytes) {
			throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.allocatedBytes + "bytes where max allowed: " + this.maxBytes);
		}
	}
}
