package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2656 implements class_2596<class_2602> {
	private class_1792 field_12154;
	private int field_12155;

	public class_2656() {
	}

	public class_2656(class_1792 arg, int i) {
		this.field_12154 = arg;
		this.field_12155 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12154 = class_1792.method_7875(arg.method_10816());
		this.field_12155 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(class_1792.method_7880(this.field_12154));
		arg.method_10804(this.field_12155);
	}

	public void method_11455(class_2602 arg) {
		arg.method_11087(this);
	}

	@Environment(EnvType.CLIENT)
	public class_1792 method_11453() {
		return this.field_12154;
	}

	@Environment(EnvType.CLIENT)
	public int method_11454() {
		return this.field_12155;
	}
}
