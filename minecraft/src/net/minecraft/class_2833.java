package net.minecraft;

import java.io.IOException;

public class class_2833 implements class_2596<class_2792> {
	private double field_12899;
	private double field_12897;
	private double field_12895;
	private float field_12898;
	private float field_12896;

	public class_2833() {
	}

	public class_2833(class_1297 arg) {
		this.field_12899 = arg.field_5987;
		this.field_12897 = arg.field_6010;
		this.field_12895 = arg.field_6035;
		this.field_12898 = arg.field_6031;
		this.field_12896 = arg.field_5965;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12899 = arg.readDouble();
		this.field_12897 = arg.readDouble();
		this.field_12895 = arg.readDouble();
		this.field_12898 = arg.readFloat();
		this.field_12896 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeDouble(this.field_12899);
		arg.writeDouble(this.field_12897);
		arg.writeDouble(this.field_12895);
		arg.writeFloat(this.field_12898);
		arg.writeFloat(this.field_12896);
	}

	public void method_12278(class_2792 arg) {
		arg.method_12078(this);
	}

	public double method_12279() {
		return this.field_12899;
	}

	public double method_12280() {
		return this.field_12897;
	}

	public double method_12276() {
		return this.field_12895;
	}

	public float method_12281() {
		return this.field_12898;
	}

	public float method_12277() {
		return this.field_12896;
	}
}
