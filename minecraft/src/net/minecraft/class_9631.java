package net.minecraft;

import java.util.Objects;

public class class_9631<T> {
	private static final class_9631<?> field_51312 = new class_9631(null);
	private final T field_51313;

	private class_9631(T object) {
		this.field_51313 = object;
	}

	public boolean method_59489() {
		return this == field_51312;
	}

	public boolean method_59491() {
		return !this.method_59489();
	}

	public T method_59492() {
		if (this.method_59489()) {
			throw new UnsupportedOperationException("No value");
		} else {
			return this.field_51313;
		}
	}

	public static <T> class_9631<T> method_59493() {
		return (class_9631<T>)field_51312;
	}

	public static <T> class_9631<T> method_59490(T object) {
		return new class_9631<>(object);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_9631<?> lv = (class_9631<?>)object;
			return this.method_59489() != lv.method_59489() ? false : Objects.equals(this.field_51313, lv.field_51313);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.method_59489() ? 0 : Objects.hashCode(this.field_51313);
	}
}
