package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2866 implements class_2596<class_2792> {
	private int field_13050;
	private int field_13049;

	public class_2866() {
	}

	@Environment(EnvType.CLIENT)
	public class_2866(int i, int j) {
		this.field_13050 = i;
		this.field_13049 = j;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13050 = arg.method_10816();
		this.field_13049 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_13050);
		arg.method_10804(this.field_13049);
	}

	public void method_12434(class_2792 arg) {
		arg.method_12057(this);
	}

	public int method_12436() {
		return this.field_13050;
	}

	public int method_12435() {
		return this.field_13049;
	}
}
