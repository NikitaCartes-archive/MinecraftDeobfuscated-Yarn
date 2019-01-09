package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class class_3440 extends class_18 {
	private LongSet field_15301 = new LongOpenHashSet();
	private LongSet field_15302 = new LongOpenHashSet();

	public class_3440(String string) {
		super(string);
	}

	@Override
	public void method_77(class_2487 arg) {
		this.field_15301 = new LongOpenHashSet(arg.method_10565("All"));
		this.field_15302 = new LongOpenHashSet(arg.method_10565("Remaining"));
	}

	@Override
	public class_2487 method_75(class_2487 arg) {
		arg.method_10564("All", this.field_15301.toLongArray());
		arg.method_10564("Remaining", this.field_15302.toLongArray());
		return arg;
	}

	public void method_14896(long l) {
		this.field_15301.add(l);
		this.field_15302.add(l);
	}

	public boolean method_14897(long l) {
		return this.field_15301.contains(l);
	}

	public boolean method_14894(long l) {
		return this.field_15302.contains(l);
	}

	public void method_14895(long l) {
		this.field_15302.remove(l);
	}

	public LongSet method_14898() {
		return this.field_15301;
	}
}
