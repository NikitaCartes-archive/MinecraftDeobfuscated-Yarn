package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2607 implements class_2596<class_2602> {
	private int field_11979;
	private double field_11977;
	private double field_11976;
	private double field_11975;
	private int field_11978;

	public class_2607() {
	}

	public class_2607(class_1297 arg) {
		this.field_11979 = arg.method_5628();
		this.field_11977 = arg.field_5987;
		this.field_11976 = arg.field_6010;
		this.field_11975 = arg.field_6035;
		if (arg instanceof class_1538) {
			this.field_11978 = 1;
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_11979 = arg.method_10816();
		this.field_11978 = arg.readByte();
		this.field_11977 = arg.readDouble();
		this.field_11976 = arg.readDouble();
		this.field_11975 = arg.readDouble();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_11979);
		arg.writeByte(this.field_11978);
		arg.writeDouble(this.field_11977);
		arg.writeDouble(this.field_11976);
		arg.writeDouble(this.field_11975);
	}

	public void method_11188(class_2602 arg) {
		arg.method_11156(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11189() {
		return this.field_11979;
	}

	@Environment(EnvType.CLIENT)
	public double method_11191() {
		return this.field_11977;
	}

	@Environment(EnvType.CLIENT)
	public double method_11187() {
		return this.field_11976;
	}

	@Environment(EnvType.CLIENT)
	public double method_11186() {
		return this.field_11975;
	}

	@Environment(EnvType.CLIENT)
	public int method_11190() {
		return this.field_11978;
	}
}
