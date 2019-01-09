package net.minecraft;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class class_2348<T> extends class_2370<T> {
	private final class_2960 field_11014;
	private T field_11015;

	public class_2348(String string) {
		this.field_11014 = new class_2960(string);
	}

	@Override
	public <V extends T> V method_10273(int i, class_2960 arg, V object) {
		if (this.field_11014.equals(arg)) {
			this.field_11015 = (T)object;
		}

		return super.method_10273(i, arg, object);
	}

	@Override
	public int method_10249(@Nullable T object) {
		int i = super.method_10249(object);
		return i == -1 ? super.method_10249(this.field_11015) : i;
	}

	@Nonnull
	@Override
	public class_2960 method_10221(T object) {
		class_2960 lv = super.method_10221(object);
		return lv == null ? this.field_11014 : lv;
	}

	@Nonnull
	@Override
	public T method_10223(@Nullable class_2960 arg) {
		T object = super.method_10223(arg);
		return object == null ? this.field_11015 : object;
	}

	@Nonnull
	@Override
	public T method_10200(int i) {
		T object = super.method_10200(i);
		return object == null ? this.field_11015 : object;
	}

	@Nonnull
	@Override
	public T method_10240(Random random) {
		T object = super.method_10240(random);
		return object == null ? this.field_11015 : object;
	}

	public class_2960 method_10137() {
		return this.field_11014;
	}
}
