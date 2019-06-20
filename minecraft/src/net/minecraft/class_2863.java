package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2863 implements class_2596<class_2792> {
	private int field_13036;

	public class_2863() {
	}

	@Environment(EnvType.CLIENT)
	public class_2863(int i) {
		this.field_13036 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13036 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13036);
	}

	public void method_12430(class_2792 arg) {
		arg.method_12080(this);
	}

	public int method_12431() {
		return this.field_13036;
	}
}
