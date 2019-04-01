package net.minecraft;

import java.util.Arrays;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2834<T> implements class_2837<T> {
	private final class_2361<T> field_12900;
	private final T[] field_12904;
	private final class_2835<T> field_12905;
	private final Function<class_2487, T> field_12902;
	private final int field_12903;
	private int field_12901;

	public class_2834(class_2361<T> arg, int i, class_2835<T> arg2, Function<class_2487, T> function) {
		this.field_12900 = arg;
		this.field_12904 = (T[])(new Object[1 << i]);
		this.field_12903 = i;
		this.field_12905 = arg2;
		this.field_12902 = function;
	}

	@Override
	public int method_12291(T object) {
		for (int i = 0; i < this.field_12901; i++) {
			if (this.field_12904[i] == object) {
				return i;
			}
		}

		int ix = this.field_12901;
		if (ix < this.field_12904.length) {
			this.field_12904[ix] = object;
			this.field_12901++;
			return ix;
		} else {
			return this.field_12905.onResize(this.field_12903 + 1, object);
		}
	}

	@Override
	public boolean method_19525(T object) {
		return Arrays.stream(this.field_12904).anyMatch(object2 -> object2 == object);
	}

	@Nullable
	@Override
	public T method_12288(int i) {
		return i >= 0 && i < this.field_12901 ? this.field_12904[i] : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(class_2540 arg) {
		this.field_12901 = arg.method_10816();

		for (int i = 0; i < this.field_12901; i++) {
			this.field_12904[i] = this.field_12900.method_10200(arg.method_10816());
		}
	}

	@Override
	public void method_12287(class_2540 arg) {
		arg.method_10804(this.field_12901);

		for (int i = 0; i < this.field_12901; i++) {
			arg.method_10804(this.field_12900.method_10206(this.field_12904[i]));
		}
	}

	@Override
	public int method_12290() {
		int i = class_2540.method_10815(this.method_12282());

		for (int j = 0; j < this.method_12282(); j++) {
			i += class_2540.method_10815(this.field_12900.method_10206(this.field_12904[j]));
		}

		return i;
	}

	public int method_12282() {
		return this.field_12901;
	}

	@Override
	public void method_12286(class_2499 arg) {
		for (int i = 0; i < arg.size(); i++) {
			this.field_12904[i] = (T)this.field_12902.apply(arg.method_10602(i));
		}

		this.field_12901 = arg.size();
	}
}
