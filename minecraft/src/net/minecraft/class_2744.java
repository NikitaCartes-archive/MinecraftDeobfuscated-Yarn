package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2744 implements class_2596<class_2602> {
	private int field_12565;
	private class_1304 field_12567;
	private class_1799 field_12566 = class_1799.field_8037;

	public class_2744() {
	}

	public class_2744(int i, class_1304 arg, class_1799 arg2) {
		this.field_12565 = i;
		this.field_12567 = arg;
		this.field_12566 = arg2.method_7972();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12565 = arg.method_10816();
		this.field_12567 = arg.method_10818(class_1304.class);
		this.field_12566 = arg.method_10819();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12565);
		arg.method_10817(this.field_12567);
		arg.method_10793(this.field_12566);
	}

	public void method_11823(class_2602 arg) {
		arg.method_11151(this);
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_11822() {
		return this.field_12566;
	}

	@Environment(EnvType.CLIENT)
	public int method_11820() {
		return this.field_12565;
	}

	@Environment(EnvType.CLIENT)
	public class_1304 method_11821() {
		return this.field_12567;
	}
}
