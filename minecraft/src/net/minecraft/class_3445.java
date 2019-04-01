package net.minecraft;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3445<T> extends class_274 {
	private final class_3446 field_15319;
	private final T field_15320;
	private final class_3448<T> field_15321;

	protected class_3445(class_3448<T> arg, T object, class_3446 arg2) {
		super(method_14950(arg, object));
		this.field_15321 = arg;
		this.field_15319 = arg2;
		this.field_15320 = object;
	}

	public static <T> String method_14950(class_3448<T> arg, T object) {
		return method_14952(class_2378.field_11152.method_10221(arg)) + ":" + method_14952(arg.method_14959().method_10221(object));
	}

	private static <T> String method_14952(@Nullable class_2960 arg) {
		return arg.toString().replace(':', '.');
	}

	public class_3448<T> method_14949() {
		return this.field_15321;
	}

	public T method_14951() {
		return this.field_15320;
	}

	@Environment(EnvType.CLIENT)
	public String method_14953(int i) {
		return this.field_15319.format(i);
	}

	public boolean equals(Object object) {
		return this == object || object instanceof class_3445 && Objects.equals(this.method_1225(), ((class_3445)object).method_1225());
	}

	public int hashCode() {
		return this.method_1225().hashCode();
	}

	public String toString() {
		return "Stat{name=" + this.method_1225() + ", formatter=" + this.field_15319 + '}';
	}
}
