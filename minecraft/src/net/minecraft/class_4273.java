package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4273 implements class_2596<class_2602> {
	private int field_19146;

	public class_4273() {
	}

	public class_4273(int i) {
		this.field_19146 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_19146 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_19146);
	}

	public void method_20205(class_2602 arg) {
		arg.method_20203(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_20206() {
		return this.field_19146;
	}
}
