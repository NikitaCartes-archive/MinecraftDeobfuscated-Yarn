package net.minecraft.nbt;

public class PositionTracker {
	public static final PositionTracker DEFAULT = new PositionTracker(0L) {
		@Override
		public void add(long bits) {
		}
	};
	private final long max;
	private long pos;

	public PositionTracker(long max) {
		this.max = max;
	}

	public void add(long bits) {
		this.pos += bits / 8L;
		if (this.pos > this.max) {
			throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.pos + "bytes where max allowed: " + this.max);
		}
	}
}
