package net.minecraft;

import java.util.function.Predicate;

public class class_3959 {
	private final class_243 field_17553;
	private final class_243 field_17554;
	private final class_3959.class_3960 field_17555;
	private final class_3959.class_242 field_17556;
	private final class_3726 field_17557;

	public class_3959(class_243 arg, class_243 arg2, class_3959.class_3960 arg3, class_3959.class_242 arg4, class_1297 arg5) {
		this.field_17553 = arg;
		this.field_17554 = arg2;
		this.field_17555 = arg3;
		this.field_17556 = arg4;
		this.field_17557 = class_3726.method_16195(arg5);
	}

	public class_243 method_17747() {
		return this.field_17554;
	}

	public class_243 method_17750() {
		return this.field_17553;
	}

	public class_265 method_17748(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_17555.get(arg, arg2, arg3, this.field_17557);
	}

	public class_265 method_17749(class_3610 arg, class_1922 arg2, class_2338 arg3) {
		return this.field_17556.method_17751(arg) ? arg.method_17776(arg2, arg3) : class_259.method_1073();
	}

	public static enum class_242 {
		field_1348(arg -> false),
		field_1345(class_3610::method_15771),
		field_1347(arg -> !arg.method_15769());

		private final Predicate<class_3610> field_1346;

		private class_242(Predicate<class_3610> predicate) {
			this.field_1346 = predicate;
		}

		public boolean method_17751(class_3610 arg) {
			return this.field_1346.test(arg);
		}
	}

	public static enum class_3960 implements class_3959.class_3961 {
		field_17558(class_2680::method_16337),
		field_17559(class_2680::method_11606);

		private final class_3959.class_3961 field_17560;

		private class_3960(class_3959.class_3961 arg) {
			this.field_17560 = arg;
		}

		@Override
		public class_265 get(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
			return this.field_17560.get(arg, arg2, arg3, arg4);
		}
	}

	public interface class_3961 {
		class_265 get(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4);
	}
}
