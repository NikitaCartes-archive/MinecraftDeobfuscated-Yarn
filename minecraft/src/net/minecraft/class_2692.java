package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2692 implements class_2596<class_2602> {
	private double field_12324;
	private double field_12322;
	private double field_12320;
	private float field_12323;
	private float field_12321;

	public class_2692() {
	}

	public class_2692(class_1297 arg) {
		this.field_12324 = arg.field_5987;
		this.field_12322 = arg.field_6010;
		this.field_12320 = arg.field_6035;
		this.field_12323 = arg.field_6031;
		this.field_12321 = arg.field_5965;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12324 = arg.readDouble();
		this.field_12322 = arg.readDouble();
		this.field_12320 = arg.readDouble();
		this.field_12323 = arg.readFloat();
		this.field_12321 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeDouble(this.field_12324);
		arg.writeDouble(this.field_12322);
		arg.writeDouble(this.field_12320);
		arg.writeFloat(this.field_12323);
		arg.writeFloat(this.field_12321);
	}

	public void method_11672(class_2602 arg) {
		arg.method_11134(this);
	}

	@Environment(EnvType.CLIENT)
	public double method_11673() {
		return this.field_12324;
	}

	@Environment(EnvType.CLIENT)
	public double method_11674() {
		return this.field_12322;
	}

	@Environment(EnvType.CLIENT)
	public double method_11670() {
		return this.field_12320;
	}

	@Environment(EnvType.CLIENT)
	public float method_11675() {
		return this.field_12323;
	}

	@Environment(EnvType.CLIENT)
	public float method_11671() {
		return this.field_12321;
	}
}
