package net.minecraft.nbt;

public class PositionTracker {
	public static final PositionTracker DEFAULT = new PositionTracker(0L) {
		@Override
		public void add(long l) {
		}
	};
	private final long max;
	private long pos;

	public PositionTracker(long l) {
		this.max = l;
	}

	public void add(long l) {
		this.pos += l / 8L;
		if (this.pos > this.max) {
			throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.pos + "bytes where max allowed: " + this.max);
		}
	}
}
