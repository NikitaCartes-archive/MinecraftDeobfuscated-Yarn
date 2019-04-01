package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2873 implements class_2596<class_2792> {
	private int field_13071;
	private class_1799 field_13070 = class_1799.field_8037;

	public class_2873() {
	}

	@Environment(EnvType.CLIENT)
	public class_2873(int i, class_1799 arg) {
		this.field_13071 = i;
		this.field_13070 = arg.method_7972();
	}

	public void method_12480(class_2792 arg) {
		arg.method_12070(this);
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13071 = arg.readShort();
		this.field_13070 = arg.method_10819();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeShort(this.field_13071);
		arg.method_10793(this.field_13070);
	}

	public int method_12481() {
		return this.field_13071;
	}

	public class_1799 method_12479() {
		return this.field_13070;
	}
}
