package net.minecraft;

import java.util.Random;
import java.util.Set;

public abstract class class_4148<E extends class_1309> {
	private static final Random field_19294 = new Random();
	private final int field_18464;
	private long field_18463;

	public class_4148(int i) {
		this.field_18464 = i;
		this.field_18463 = (long)field_19294.nextInt(i);
	}

	public class_4148() {
		this(20);
	}

	public final void method_19100(class_3218 arg, E arg2) {
		if (--this.field_18463 <= 0L) {
			this.field_18463 = (long)this.field_18464;
			this.method_19101(arg, arg2);
		}
	}

	protected abstract void method_19101(class_3218 arg, E arg2);

	public abstract Set<class_4140<?>> method_19099();
}
