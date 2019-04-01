package net.minecraft;

import java.util.Set;

public abstract class class_4148<E extends class_1309> {
	private final int field_18464;
	protected long field_18463;

	public class_4148(int i) {
		this.field_18464 = i;
	}

	public class_4148() {
		this(10);
	}

	public boolean method_19100(class_3218 arg, E arg2) {
		return arg.method_8510() - this.field_18463 >= (long)this.field_18464;
	}

	public abstract void method_19101(class_3218 arg, E arg2);

	public abstract Set<class_4140<?>> method_19099();
}
