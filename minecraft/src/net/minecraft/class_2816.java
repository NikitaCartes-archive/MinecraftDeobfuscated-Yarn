package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2816<T> implements class_2837<T> {
	private final class_2361<T> field_12828;
	private final T field_12829;

	public class_2816(class_2361<T> arg, T object) {
		this.field_12828 = arg;
		this.field_12829 = object;
	}

	@Override
	public int method_12291(T object) {
		int i = this.field_12828.method_10206(object);
		return i == -1 ? 0 : i;
	}

	@Override
	public T method_12288(int i) {
		T object = this.field_12828.method_10200(i);
		return object == null ? this.field_12829 : object;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(class_2540 arg) {
	}

	@Override
	public void method_12287(class_2540 arg) {
	}

	@Override
	public int method_12290() {
		return class_2540.method_10815(0);
	}

	@Override
	public void method_12286(class_2499 arg) {
	}
}
