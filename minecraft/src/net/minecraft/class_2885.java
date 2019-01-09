package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2885 implements class_2596<class_2792> {
	private class_2338 field_13135;
	private class_2350 field_13133;
	private class_1268 field_13134;
	private float field_13132;
	private float field_13131;
	private float field_13130;

	public class_2885() {
	}

	@Environment(EnvType.CLIENT)
	public class_2885(class_2338 arg, class_2350 arg2, class_1268 arg3, float f, float g, float h) {
		this.field_13135 = arg;
		this.field_13133 = arg2;
		this.field_13134 = arg3;
		this.field_13132 = f;
		this.field_13131 = g;
		this.field_13130 = h;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_13135 = arg.method_10811();
		this.field_13133 = arg.method_10818(class_2350.class);
		this.field_13134 = arg.method_10818(class_1268.class);
		this.field_13132 = arg.readFloat();
		this.field_13131 = arg.readFloat();
		this.field_13130 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10807(this.field_13135);
		arg.method_10817(this.field_13133);
		arg.method_10817(this.field_13134);
		arg.writeFloat(this.field_13132);
		arg.writeFloat(this.field_13131);
		arg.writeFloat(this.field_13130);
	}

	public void method_12547(class_2792 arg) {
		arg.method_12046(this);
	}

	public class_2338 method_12548() {
		return this.field_13135;
	}

	public class_2350 method_12545() {
		return this.field_13133;
	}

	public class_1268 method_12546() {
		return this.field_13134;
	}

	public float method_12549() {
		return this.field_13132;
	}

	public float method_12543() {
		return this.field_13131;
	}

	public float method_12544() {
		return this.field_13130;
	}
}
