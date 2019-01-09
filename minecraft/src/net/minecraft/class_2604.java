package net.minecraft;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2604 implements class_2596<class_2602> {
	private int field_11953;
	private UUID field_11952;
	private double field_11948;
	private double field_11946;
	private double field_11956;
	private int field_11951;
	private int field_11950;
	private int field_11949;
	private int field_11947;
	private int field_11957;
	private int field_11955;
	private int field_11954;

	public class_2604() {
	}

	public class_2604(class_1297 arg, int i) {
		this(arg, i, 0);
	}

	public class_2604(class_1297 arg, int i, int j) {
		this.field_11953 = arg.method_5628();
		this.field_11952 = arg.method_5667();
		this.field_11948 = arg.field_5987;
		this.field_11946 = arg.field_6010;
		this.field_11956 = arg.field_6035;
		this.field_11947 = class_3532.method_15375(arg.field_5965 * 256.0F / 360.0F);
		this.field_11957 = class_3532.method_15375(arg.field_6031 * 256.0F / 360.0F);
		this.field_11955 = i;
		this.field_11954 = j;
		double d = 3.9;
		this.field_11951 = (int)(class_3532.method_15350(arg.field_5967, -3.9, 3.9) * 8000.0);
		this.field_11950 = (int)(class_3532.method_15350(arg.field_5984, -3.9, 3.9) * 8000.0);
		this.field_11949 = (int)(class_3532.method_15350(arg.field_6006, -3.9, 3.9) * 8000.0);
	}

	public class_2604(class_1297 arg, int i, int j, class_2338 arg2) {
		this(arg, i, j);
		this.field_11948 = (double)arg2.method_10263();
		this.field_11946 = (double)arg2.method_10264();
		this.field_11956 = (double)arg2.method_10260();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_11953 = arg.method_10816();
		this.field_11952 = arg.method_10790();
		this.field_11955 = arg.readByte();
		this.field_11948 = arg.readDouble();
		this.field_11946 = arg.readDouble();
		this.field_11956 = arg.readDouble();
		this.field_11947 = arg.readByte();
		this.field_11957 = arg.readByte();
		this.field_11954 = arg.readInt();
		this.field_11951 = arg.readShort();
		this.field_11950 = arg.readShort();
		this.field_11949 = arg.readShort();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_11953);
		arg.method_10797(this.field_11952);
		arg.writeByte(this.field_11955);
		arg.writeDouble(this.field_11948);
		arg.writeDouble(this.field_11946);
		arg.writeDouble(this.field_11956);
		arg.writeByte(this.field_11947);
		arg.writeByte(this.field_11957);
		arg.writeInt(this.field_11954);
		arg.writeShort(this.field_11951);
		arg.writeShort(this.field_11950);
		arg.writeShort(this.field_11949);
	}

	public void method_11178(class_2602 arg) {
		arg.method_11112(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11167() {
		return this.field_11953;
	}

	@Environment(EnvType.CLIENT)
	public UUID method_11164() {
		return this.field_11952;
	}

	@Environment(EnvType.CLIENT)
	public double method_11175() {
		return this.field_11948;
	}

	@Environment(EnvType.CLIENT)
	public double method_11174() {
		return this.field_11946;
	}

	@Environment(EnvType.CLIENT)
	public double method_11176() {
		return this.field_11956;
	}

	@Environment(EnvType.CLIENT)
	public int method_11170() {
		return this.field_11951;
	}

	@Environment(EnvType.CLIENT)
	public int method_11172() {
		return this.field_11950;
	}

	@Environment(EnvType.CLIENT)
	public int method_11173() {
		return this.field_11949;
	}

	@Environment(EnvType.CLIENT)
	public int method_11171() {
		return this.field_11947;
	}

	@Environment(EnvType.CLIENT)
	public int method_11168() {
		return this.field_11957;
	}

	@Environment(EnvType.CLIENT)
	public int method_11169() {
		return this.field_11955;
	}

	@Environment(EnvType.CLIENT)
	public int method_11166() {
		return this.field_11954;
	}

	public void method_11162(int i) {
		this.field_11951 = i;
	}

	public void method_11165(int i) {
		this.field_11950 = i;
	}

	public void method_11177(int i) {
		this.field_11949 = i;
	}

	@Environment(EnvType.CLIENT)
	public void method_11163(int i) {
		this.field_11954 = i;
	}
}
