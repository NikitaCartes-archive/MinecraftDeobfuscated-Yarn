package net.minecraft;

@FunctionalInterface
public interface class_234<T> {
	void method_974(T object, class_236<T> arg, long l);

	public abstract static class class_235<T, C extends class_234<T>> {
		private final class_2960 field_1309;
		private final Class<?> field_1310;

		public class_235(class_2960 arg, Class<?> class_) {
			this.field_1309 = arg;
			this.field_1310 = class_;
		}

		public class_2960 method_977() {
			return this.field_1309;
		}

		public Class<?> method_978() {
			return this.field_1310;
		}

		public abstract void method_975(class_2487 arg, C arg2);

		public abstract C method_976(class_2487 arg);
	}
}
