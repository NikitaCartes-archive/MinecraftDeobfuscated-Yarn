package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2885 implements class_2596<class_2792> {
	private class_3965 field_17602;
	private class_1268 field_13134;

	public class_2885() {
	}

	@Environment(EnvType.CLIENT)
	public class_2885(class_1268 arg, class_3965 arg2) {
		this.field_13134 = arg;
		this.field_17602 = arg2;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13134 = arg.method_10818(class_1268.class);
		this.field_17602 = arg.method_17814();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10817(this.field_13134);
		arg.method_17813(this.field_17602);
	}

	public void method_12547(class_2792 arg) {
		arg.method_12046(this);
	}

	public class_1268 method_12546() {
		return this.field_13134;
	}

	public class_3965 method_12543() {
		return this.field_17602;
	}
}
