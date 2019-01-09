package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2793 implements class_2596<class_2792> {
	private int field_12758;

	public class_2793() {
	}

	@Environment(EnvType.CLIENT)
	public class_2793(int i) {
		this.field_12758 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12758 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12758);
	}

	public void method_12085(class_2792 arg) {
		arg.method_12050(this);
	}

	public int method_12086() {
		return this.field_12758;
	}
}
