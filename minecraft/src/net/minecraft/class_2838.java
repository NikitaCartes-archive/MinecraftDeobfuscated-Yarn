package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2838 implements class_2596<class_2792> {
	private int field_12908;

	public class_2838() {
	}

	@Environment(EnvType.CLIENT)
	public class_2838(int i) {
		this.field_12908 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12908 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12908);
	}

	public void method_12292(class_2792 arg) {
		arg.method_12084(this);
	}

	public int method_12293() {
		return this.field_12908;
	}
}
