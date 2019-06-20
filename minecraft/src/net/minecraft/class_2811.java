package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2811 implements class_2596<class_2792> {
	private int field_12813;
	private int field_12812;

	public class_2811() {
	}

	@Environment(EnvType.CLIENT)
	public class_2811(int i, int j) {
		this.field_12813 = i;
		this.field_12812 = j;
	}

	public void method_12185(class_2792 arg) {
		arg.method_12055(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12813 = arg.readByte();
		this.field_12812 = arg.readByte();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeByte(this.field_12813);
		arg.writeByte(this.field_12812);
	}

	public int method_12187() {
		return this.field_12813;
	}

	public int method_12186() {
		return this.field_12812;
	}
}
