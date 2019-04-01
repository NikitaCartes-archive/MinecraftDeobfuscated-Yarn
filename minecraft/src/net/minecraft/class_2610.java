package net.minecraft;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2610 implements class_2596<class_2602> {
	private int field_11996;
	private UUID field_11997;
	private int field_11995;
	private double field_11990;
	private double field_12003;
	private double field_12002;
	private int field_11993;
	private int field_11992;
	private int field_11991;
	private byte field_12000;
	private byte field_11999;
	private byte field_11998;
	private class_2945 field_11994;
	private List<class_2945.class_2946<?>> field_12001;

	public class_2610() {
	}

	public class_2610(class_1309 arg) {
		this.field_11996 = arg.method_5628();
		this.field_11997 = arg.method_5667();
		this.field_11995 = class_2378.field_11145.method_10249(arg.method_5864());
		this.field_11990 = arg.field_5987;
		this.field_12003 = arg.field_6010;
		this.field_12002 = arg.field_6035;
		this.field_12000 = (byte)((int)(arg.field_6031 * 256.0F / 360.0F));
		this.field_11999 = (byte)((int)(arg.field_5965 * 256.0F / 360.0F));
		this.field_11998 = (byte)((int)(arg.field_6241 * 256.0F / 360.0F));
		double d = 3.9;
		class_243 lv = arg.method_18798();
		double e = class_3532.method_15350(lv.field_1352, -3.9, 3.9);
		double f = class_3532.method_15350(lv.field_1351, -3.9, 3.9);
		double g = class_3532.method_15350(lv.field_1350, -3.9, 3.9);
		this.field_11993 = (int)(e * 8000.0);
		this.field_11992 = (int)(f * 8000.0);
		this.field_11991 = (int)(g * 8000.0);
		this.field_11994 = arg.method_5841();
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_11996 = arg.method_10816();
		this.field_11997 = arg.method_10790();
		this.field_11995 = arg.method_10816();
		this.field_11990 = arg.readDouble();
		this.field_12003 = arg.readDouble();
		this.field_12002 = arg.readDouble();
		this.field_12000 = arg.readByte();
		this.field_11999 = arg.readByte();
		this.field_11998 = arg.readByte();
		this.field_11993 = arg.readShort();
		this.field_11992 = arg.readShort();
		this.field_11991 = arg.readShort();
		this.field_12001 = class_2945.method_12788(arg);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_11996);
		arg.method_10797(this.field_11997);
		arg.method_10804(this.field_11995);
		arg.writeDouble(this.field_11990);
		arg.writeDouble(this.field_12003);
		arg.writeDouble(this.field_12002);
		arg.writeByte(this.field_12000);
		arg.writeByte(this.field_11999);
		arg.writeByte(this.field_11998);
		arg.writeShort(this.field_11993);
		arg.writeShort(this.field_11992);
		arg.writeShort(this.field_11991);
		this.field_11994.method_12780(arg);
	}

	public void method_11217(class_2602 arg) {
		arg.method_11138(this);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public List<class_2945.class_2946<?>> method_11208() {
		return this.field_12001;
	}

	@Environment(EnvType.CLIENT)
	public int method_11207() {
		return this.field_11996;
	}

	@Environment(EnvType.CLIENT)
	public UUID method_11213() {
		return this.field_11997;
	}

	@Environment(EnvType.CLIENT)
	public int method_11210() {
		return this.field_11995;
	}

	@Environment(EnvType.CLIENT)
	public double method_11214() {
		return this.field_11990;
	}

	@Environment(EnvType.CLIENT)
	public double method_11215() {
		return this.field_12003;
	}

	@Environment(EnvType.CLIENT)
	public double method_11216() {
		return this.field_12002;
	}

	@Environment(EnvType.CLIENT)
	public int method_11212() {
		return this.field_11993;
	}

	@Environment(EnvType.CLIENT)
	public int method_11211() {
		return this.field_11992;
	}

	@Environment(EnvType.CLIENT)
	public int method_11209() {
		return this.field_11991;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11205() {
		return this.field_12000;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11206() {
		return this.field_11999;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11204() {
		return this.field_11998;
	}
}
