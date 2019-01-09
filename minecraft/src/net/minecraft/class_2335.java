package net.minecraft;

public enum class_2335 {
	field_10962 {
		@Override
		public int method_10056(int i, int j, int k, class_2350.class_2351 arg) {
			return arg.method_10173(i, j, k);
		}

		@Override
		public class_2350.class_2351 method_10058(class_2350.class_2351 arg) {
			return arg;
		}

		@Override
		public class_2335 method_10055() {
			return this;
		}
	},
	field_10963 {
		@Override
		public int method_10056(int i, int j, int k, class_2350.class_2351 arg) {
			return arg.method_10173(k, i, j);
		}

		@Override
		public class_2350.class_2351 method_10058(class_2350.class_2351 arg) {
			return field_10961[Math.floorMod(arg.ordinal() + 1, 3)];
		}

		@Override
		public class_2335 method_10055() {
			return field_10965;
		}
	},
	field_10965 {
		@Override
		public int method_10056(int i, int j, int k, class_2350.class_2351 arg) {
			return arg.method_10173(j, k, i);
		}

		@Override
		public class_2350.class_2351 method_10058(class_2350.class_2351 arg) {
			return field_10961[Math.floorMod(arg.ordinal() - 1, 3)];
		}

		@Override
		public class_2335 method_10055() {
			return field_10963;
		}
	};

	public static final class_2350.class_2351[] field_10961 = class_2350.class_2351.values();
	public static final class_2335[] field_10960 = values();

	private class_2335() {
	}

	public abstract int method_10056(int i, int j, int k, class_2350.class_2351 arg);

	public abstract class_2350.class_2351 method_10058(class_2350.class_2351 arg);

	public abstract class_2335 method_10055();

	public static class_2335 method_10057(class_2350.class_2351 arg, class_2350.class_2351 arg2) {
		return field_10960[Math.floorMod(arg2.ordinal() - arg.ordinal(), 3)];
	}
}
