package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3753 implements class_2596<class_2792> {
	private class_2338 field_16565;
	private class_2960 field_16563;
	private class_2960 field_16566;
	private String field_16564;

	public class_3753(class_2338 arg, class_2960 arg2, class_2960 arg3, String string) {
		this.field_16565 = arg;
		this.field_16563 = arg2;
		this.field_16566 = arg3;
		this.field_16564 = string;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_16565 = arg.method_10811();
		this.field_16563 = arg.method_10810();
		this.field_16566 = arg.method_10810();
		this.field_16564 = arg.method_10800(32767);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_16565);
		arg.method_10812(this.field_16563);
		arg.method_10812(this.field_16566);
		arg.method_10814(this.field_16564);
	}

	public void method_16392(class_2792 arg) {
		arg.method_16383(this);
	}

	public class_2338 method_16396() {
		return this.field_16565;
	}

	public class_2960 method_16394() {
		return this.field_16566;
	}

	public class_2960 method_16395() {
		return this.field_16563;
	}

	public String method_16393() {
		return this.field_16564;
	}
}
