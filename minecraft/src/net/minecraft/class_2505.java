package net.minecraft;

public class class_2505 {
	public static final class_2505 field_11556 = new class_2505(0L) {
		@Override
		public void method_10623(long l) {
		}
	};
	private final long field_11557;
	private long field_11555;

	public class_2505(long l) {
		this.field_11557 = l;
	}

	public void method_10623(long l) {
		this.field_11555 += l / 8L;
		if (this.field_11555 > this.field_11557) {
			throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.field_11555 + "bytes where max allowed: " + this.field_11557);
		}
	}
}
