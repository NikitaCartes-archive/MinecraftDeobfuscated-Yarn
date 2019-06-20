package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2606 implements class_2596<class_2602> {
	private int field_11974;
	private double field_11972;
	private double field_11971;
	private double field_11970;
	private int field_11973;

	public class_2606() {
	}

	public class_2606(class_1303 arg) {
		this.field_11974 = arg.method_5628();
		this.field_11972 = arg.field_5987;
		this.field_11971 = arg.field_6010;
		this.field_11970 = arg.field_6035;
		this.field_11973 = arg.method_5919();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_11974 = arg.method_10816();
		this.field_11972 = arg.readDouble();
		this.field_11971 = arg.readDouble();
		this.field_11970 = arg.readDouble();
		this.field_11973 = arg.readShort();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_11974);
		arg.writeDouble(this.field_11972);
		arg.writeDouble(this.field_11971);
		arg.writeDouble(this.field_11970);
		arg.writeShort(this.field_11973);
	}

	public void method_11182(class_2602 arg) {
		arg.method_11091(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11183() {
		return this.field_11974;
	}

	@Environment(EnvType.CLIENT)
	public double method_11185() {
		return this.field_11972;
	}

	@Environment(EnvType.CLIENT)
	public double method_11181() {
		return this.field_11971;
	}

	@Environment(EnvType.CLIENT)
	public double method_11180() {
		return this.field_11970;
	}

	@Environment(EnvType.CLIENT)
	public int method_11184() {
		return this.field_11973;
	}
}
