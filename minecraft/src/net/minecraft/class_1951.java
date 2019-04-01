package net.minecraft;

public interface class_1951<T> {
	boolean method_8674(class_2338 arg, T object);

	default void method_8676(class_2338 arg, T object, int i) {
		this.method_8675(arg, object, i, class_1953.field_9314);
	}

	void method_8675(class_2338 arg, T object, int i, class_1953 arg2);

	boolean method_8677(class_2338 arg, T object);
}
