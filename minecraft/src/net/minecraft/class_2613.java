package net.minecraft;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2613 implements class_2596<class_2602> {
	private int field_12017;
	private UUID field_12015;
	private double field_12014;
	private double field_12013;
	private double field_12021;
	private byte field_12020;
	private byte field_12019;
	private class_2945 field_12016;
	private List<class_2945.class_2946<?>> field_12018;

	public class_2613() {
	}

	public class_2613(class_1657 arg) {
		this.field_12017 = arg.method_5628();
		this.field_12015 = arg.method_7334().getId();
		this.field_12014 = arg.field_5987;
		this.field_12013 = arg.field_6010;
		this.field_12021 = arg.field_6035;
		this.field_12020 = (byte)((int)(arg.field_6031 * 256.0F / 360.0F));
		this.field_12019 = (byte)((int)(arg.field_5965 * 256.0F / 360.0F));
		this.field_12016 = arg.method_5841();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12017 = arg.method_10816();
		this.field_12015 = arg.method_10790();
		this.field_12014 = arg.readDouble();
		this.field_12013 = arg.readDouble();
		this.field_12021 = arg.readDouble();
		this.field_12020 = arg.readByte();
		this.field_12019 = arg.readByte();
		this.field_12018 = class_2945.method_12788(arg);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12017);
		arg.method_10797(this.field_12015);
		arg.writeDouble(this.field_12014);
		arg.writeDouble(this.field_12013);
		arg.writeDouble(this.field_12021);
		arg.writeByte(this.field_12020);
		arg.writeByte(this.field_12019);
		this.field_12016.method_12780(arg);
	}

	public void method_11235(class_2602 arg) {
		arg.method_11097(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public List<class_2945.class_2946<?>> method_11229() {
		return this.field_12018;
	}

	@Environment(EnvType.CLIENT)
	public int method_11227() {
		return this.field_12017;
	}

	@Environment(EnvType.CLIENT)
	public UUID method_11230() {
		return this.field_12015;
	}

	@Environment(EnvType.CLIENT)
	public double method_11231() {
		return this.field_12014;
	}

	@Environment(EnvType.CLIENT)
	public double method_11232() {
		return this.field_12013;
	}

	@Environment(EnvType.CLIENT)
	public double method_11233() {
		return this.field_12021;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11234() {
		return this.field_12020;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11228() {
		return this.field_12019;
	}
}
