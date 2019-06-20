package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4282 implements class_2596<class_2602> {
	private int field_19206;
	private int field_19207;

	public class_4282() {
	}

	public class_4282(int i, int j) {
		this.field_19206 = i;
		this.field_19207 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_19206 = arg.method_10816();
		this.field_19207 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_19206);
		arg.method_10804(this.field_19207);
	}

	public void method_20321(class_2602 arg) {
		arg.method_20320(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_20322() {
		return this.field_19206;
	}

	@Environment(EnvType.CLIENT)
	public int method_20323() {
		return this.field_19207;
	}
}
