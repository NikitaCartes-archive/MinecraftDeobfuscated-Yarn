package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3943 implements class_2596<class_2602> {
	private int field_17434;
	private class_1916 field_17435;

	public class_3943() {
	}

	public class_3943(int i, class_1916 arg) {
		this.field_17434 = i;
		this.field_17435 = arg;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_17434 = arg.method_10816();
		this.field_17435 = class_1916.method_8265(arg);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_17434);
		this.field_17435.method_8270(arg);
	}

	public void method_17588(class_2602 arg) {
		arg.method_17586(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_17589() {
		return this.field_17434;
	}

	@Environment(EnvType.CLIENT)
	public class_1916 method_17590() {
		return this.field_17435;
	}
}
