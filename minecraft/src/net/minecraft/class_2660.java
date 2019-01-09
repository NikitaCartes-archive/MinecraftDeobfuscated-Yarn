package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2660 implements class_2596<class_2602> {
	private class_2960 field_12170;
	private class_3419 field_12171;
	private int field_12169;
	private int field_12168 = Integer.MAX_VALUE;
	private int field_12167;
	private float field_12166;
	private float field_12172;

	public class_2660() {
	}

	public class_2660(class_2960 arg, class_3419 arg2, class_243 arg3, float f, float g) {
		this.field_12170 = arg;
		this.field_12171 = arg2;
		this.field_12169 = (int)(arg3.field_1352 * 8.0);
		this.field_12168 = (int)(arg3.field_1351 * 8.0);
		this.field_12167 = (int)(arg3.field_1350 * 8.0);
		this.field_12166 = f;
		this.field_12172 = g;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12170 = arg.method_10810();
		this.field_12171 = arg.method_10818(class_3419.class);
		this.field_12169 = arg.readInt();
		this.field_12168 = arg.readInt();
		this.field_12167 = arg.readInt();
		this.field_12166 = arg.readFloat();
		this.field_12172 = arg.readFloat();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10812(this.field_12170);
		arg.method_10817(this.field_12171);
		arg.writeInt(this.field_12169);
		arg.writeInt(this.field_12168);
		arg.writeInt(this.field_12167);
		arg.writeFloat(this.field_12166);
		arg.writeFloat(this.field_12172);
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_11460() {
		return this.field_12170;
	}

	@Environment(EnvType.CLIENT)
	public class_3419 method_11459() {
		return this.field_12171;
	}

	@Environment(EnvType.CLIENT)
	public double method_11462() {
		return (double)((float)this.field_12169 / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public double method_11461() {
		return (double)((float)this.field_12168 / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public double method_11465() {
		return (double)((float)this.field_12167 / 8.0F);
	}

	@Environment(EnvType.CLIENT)
	public float method_11463() {
		return this.field_12166;
	}

	@Environment(EnvType.CLIENT)
	public float method_11464() {
		return this.field_12172;
	}

	public void method_11466(class_2602 arg) {
		arg.method_11104(this);
	}
}
