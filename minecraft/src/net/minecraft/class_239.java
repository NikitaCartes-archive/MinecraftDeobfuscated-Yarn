package net.minecraft;

public class class_239 {
	private class_2338 field_1328;
	public final class_239.class_240 field_1330;
	public class_2350 field_1327;
	public final class_243 field_1329;
	public class_1297 field_1326;

	public class_239(class_243 arg, class_2350 arg2, class_2338 arg3) {
		this(class_239.class_240.field_1332, arg, arg2, arg3);
	}

	public class_239(class_1297 arg) {
		this(arg, new class_243(arg.field_5987, arg.field_6010, arg.field_6035));
	}

	public class_239(class_239.class_240 arg, class_243 arg2, class_2350 arg3, class_2338 arg4) {
		this.field_1330 = arg;
		this.field_1328 = arg4;
		this.field_1327 = arg3;
		this.field_1329 = new class_243(arg2.field_1352, arg2.field_1351, arg2.field_1350);
	}

	public class_239(class_1297 arg, class_243 arg2) {
		this.field_1330 = class_239.class_240.field_1331;
		this.field_1326 = arg;
		this.field_1329 = arg2;
	}

	public class_2338 method_1015() {
		return this.field_1328;
	}

	public String toString() {
		return "HitResult{type="
			+ this.field_1330
			+ ", blockpos="
			+ this.field_1328
			+ ", f="
			+ this.field_1327
			+ ", pos="
			+ this.field_1329
			+ ", entity="
			+ this.field_1326
			+ '}';
	}

	public static enum class_240 {
		field_1333,
		field_1332,
		field_1331;
	}
}
