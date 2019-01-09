package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class class_1932 extends class_18 {
	private LongSet field_9213 = new LongOpenHashSet();

	public class_1932(String string) {
		super(string);
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_9213 = new LongOpenHashSet(arg.method_10565("Forced"));
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		arg.method_10564("Forced", this.field_9213.toLongArray());
		return arg;
	}

	public LongSet method_8375() {
		return this.field_9213;
	}
}
