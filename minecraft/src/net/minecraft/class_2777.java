package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2777 implements class_2596<class_2602> {
	private int field_12705;
	private double field_12703;
	private double field_12702;
	private double field_12701;
	private byte field_12707;
	private byte field_12706;
	private boolean field_12704;

	public class_2777() {
	}

	public class_2777(class_1297 arg) {
		this.field_12705 = arg.method_5628();
		this.field_12703 = arg.field_5987;
		this.field_12702 = arg.field_6010;
		this.field_12701 = arg.field_6035;
		this.field_12707 = (byte)((int)(arg.field_6031 * 256.0F / 360.0F));
		this.field_12706 = (byte)((int)(arg.field_5965 * 256.0F / 360.0F));
		this.field_12704 = arg.field_5952;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12705 = arg.method_10816();
		this.field_12703 = arg.readDouble();
		this.field_12702 = arg.readDouble();
		this.field_12701 = arg.readDouble();
		this.field_12707 = arg.readByte();
		this.field_12706 = arg.readByte();
		this.field_12704 = arg.readBoolean();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12705);
		arg.writeDouble(this.field_12703);
		arg.writeDouble(this.field_12702);
		arg.writeDouble(this.field_12701);
		arg.writeByte(this.field_12707);
		arg.writeByte(this.field_12706);
		arg.writeBoolean(this.field_12704);
	}

	public void method_11922(class_2602 arg) {
		arg.method_11086(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11916() {
		return this.field_12705;
	}

	@Environment(EnvType.CLIENT)
	public double method_11917() {
		return this.field_12703;
	}

	@Environment(EnvType.CLIENT)
	public double method_11919() {
		return this.field_12702;
	}

	@Environment(EnvType.CLIENT)
	public double method_11918() {
		return this.field_12701;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11920() {
		return this.field_12707;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11921() {
		return this.field_12706;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11923() {
		return this.field_12704;
	}
}
