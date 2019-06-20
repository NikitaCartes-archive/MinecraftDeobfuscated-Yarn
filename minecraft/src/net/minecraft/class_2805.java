package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2805 implements class_2596<class_2792> {
	private int field_12784;
	private String field_12785;

	public class_2805() {
	}

	@Environment(EnvType.CLIENT)
	public class_2805(int i, String string) {
		this.field_12784 = i;
		this.field_12785 = string;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12784 = arg.method_10816();
		this.field_12785 = arg.method_10800(32500);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12784);
		arg.method_10788(this.field_12785, 32500);
	}

	public void method_12147(class_2792 arg) {
		arg.method_12059(this);
	}

	public int method_12149() {
		return this.field_12784;
	}

	public String method_12148() {
		return this.field_12785;
	}
}
