package net.minecraft;

public class class_3965 extends class_239 {
	private final class_2350 field_17588;
	private final class_2338 field_17589;
	private final boolean field_17590;
	private final boolean field_17591;

	public static class_3965 method_17778(class_243 arg, class_2350 arg2, class_2338 arg3) {
		return new class_3965(true, arg, arg2, arg3, false);
	}

	public class_3965(class_243 arg, class_2350 arg2, class_2338 arg3, boolean bl) {
		this(false, arg, arg2, arg3, bl);
	}

	private class_3965(boolean bl, class_243 arg, class_2350 arg2, class_2338 arg3, boolean bl2) {
		super(arg);
		this.field_17590 = bl;
		this.field_17588 = arg2;
		this.field_17589 = arg3;
		this.field_17591 = bl2;
	}

	public class_3965 method_17779(class_2350 arg) {
		return new class_3965(this.field_17590, this.field_1329, arg, this.field_17589, this.field_17591);
	}

	public class_2338 method_17777() {
		return this.field_17589;
	}

	public class_2350 method_17780() {
		return this.field_17588;
	}

	@Override
	public class_239.class_240 method_17783() {
		return this.field_17590 ? class_239.class_240.field_1333 : class_239.class_240.field_1332;
	}

	public boolean method_17781() {
		return this.field_17591;
	}
}
