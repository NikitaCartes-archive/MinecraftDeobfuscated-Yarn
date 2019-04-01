package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.Validate;

public class class_2767 implements class_2596<class_2602> {
	private class_3414 field_12661;
	private class_3419 field_12660;
	private int field_12659;
	private int field_12658;
	private int field_12657;
	private float field_12656;
	private float field_12662;

	public class_2767() {
	}

	public class_2767(class_3414 arg, class_3419 arg2, double d, double e, double f, float g, float h) {
		Validate.notNull(arg, "sound");
		this.field_12661 = arg;
		this.field_12660 = arg2;
		this.field_12659 = (int)(d * 8.0);
		this.field_12658 = (int)(e * 8.0);
		this.field_12657 = (int)(f * 8.0);
		this.field_12656 = g;
		this.field_12662 = h;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12661 = class_2378.field_11156.method_10200(arg.method_10816());
		this.field_12660 = arg.method_10818(class_3419.class);
		this.field_12659 = arg.readInt();
		this.field_12658 = arg.readInt();
		this.field_12657 = arg.readInt();
		this.field_12656 = arg.readFloat();
		this.field_12662 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(class_2378.field_11156.method_10249(this.field_12661));
		arg.method_10817(this.field_12660);
		arg.writeInt(this.field_12659);
		arg.writeInt(this.field_12658);
		arg.writeInt(this.field_12657);
		arg.writeFloat(this.field_12656);
		arg.writeFloat(this.field_12662);
	}

	@Environment(EnvType.CLIENT)
	public class_3414 method_11894() {
		return this.field_12661;
	}

	@Environment(EnvType.CLIENT)
	public class_3419 method_11888() {
		return this.field_12660;
	}

	@Environment(EnvType.CLIENT)
	public double method_11890() {
		return (double)((float)this.field_12659 / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public double method_11889() {
		return (double)((float)this.field_12658 / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public double method_11893() {
		return (double)((float)this.field_12657 / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public float method_11891() {
		return this.field_12656;
	}

	@Environment(EnvType.CLIENT)
	public float method_11892() {
		return this.field_12662;
	}

	public void method_11895(class_2602 arg) {
		arg.method_11146(this);
	}
}
