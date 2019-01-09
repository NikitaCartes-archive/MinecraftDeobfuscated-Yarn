package net.minecraft;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2814<T> implements class_2837<T> {
	private final class_2361<T> field_12821;
	private final class_3513<T> field_12824;
	private final class_2835<T> field_12825;
	private final Function<class_2487, T> field_12823;
	private final Function<T, class_2487> field_12826;
	private final int field_12822;

	public class_2814(class_2361<T> arg, int i, class_2835<T> arg2, Function<class_2487, T> function, Function<T, class_2487> function2) {
		this.field_12821 = arg;
		this.field_12822 = i;
		this.field_12825 = arg2;
		this.field_12823 = function;
		this.field_12826 = function2;
		this.field_12824 = new class_3513<>(1 << i);
	}

	@Override
	public int method_12291(T object) {
		int i = this.field_12824.method_15231(object);
		if (i == -1) {
			i = this.field_12824.method_15225(object);
			if (i >= 1 << this.field_12822) {
				i = this.field_12825.onResize(this.field_12822 + 1, object);
			}
		}

		return i;
	}

	@Nullable
	@Override
	public T method_12288(int i) {
		return this.field_12824.method_10200(i);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(class_2540 arg) {
		this.field_12824.method_15229();
		int i = arg.method_10816();

		for (int j = 0; j < i; j++) {
			this.field_12824.method_15225(this.field_12821.method_10200(arg.method_10816()));
		}
	}

	@Override
	public void method_12287(class_2540 arg) {
		int i = this.method_12197();
		arg.method_10804(i);

		for (int j = 0; j < i; j++) {
			arg.method_10804(this.field_12821.method_10206(this.field_12824.method_10200(j)));
		}
	}

	@Override
	public int method_12290() {
		int i = class_2540.method_10815(this.method_12197());

		for (int j = 0; j < this.method_12197(); j++) {
			i += class_2540.method_10815(this.field_12821.method_10206(this.field_12824.method_10200(j)));
		}

		return i;
	}

	public int method_12197() {
		return this.field_12824.method_15227();
	}

	@Override
	public void method_12286(class_2499 arg) {
		this.field_12824.method_15229();

		for (int i = 0; i < arg.size(); i++) {
			this.field_12824.method_15225((T)this.field_12823.apply(arg.method_10602(i)));
		}
	}

	public void method_12196(class_2499 arg) {
		for (int i = 0; i < this.method_12197(); i++) {
			arg.method_10606((class_2520)this.field_12826.apply(this.field_12824.method_10200(i)));
		}
	}
}
