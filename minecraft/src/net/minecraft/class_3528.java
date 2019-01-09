package net.minecraft;

import java.util.function.Supplier;

public class class_3528<T> {
	private Supplier<T> field_15719;
	private T field_15718;

	public class_3528(Supplier<T> supplier) {
		this.field_15719 = supplier;
	}

	public T method_15332() {
		Supplier<T> supplier = this.field_15719;
		if (supplier != null) {
			this.field_15718 = (T)supplier.get();
			this.field_15719 = null;
		}

		return this.field_15718;
	}
}
