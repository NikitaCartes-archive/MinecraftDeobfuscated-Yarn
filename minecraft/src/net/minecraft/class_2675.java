package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2675 implements class_2596<class_2602> {
	private float field_12258;
	private float field_12257;
	private float field_12256;
	private float field_12255;
	private float field_12252;
	private float field_12251;
	private float field_12260;
	private int field_12253;
	private boolean field_12254;
	private class_2394 field_12259;

	public class_2675() {
	}

	public <T extends class_2394> class_2675(T arg, boolean bl, float f, float g, float h, float i, float j, float k, float l, int m) {
		this.field_12259 = arg;
		this.field_12254 = bl;
		this.field_12258 = f;
		this.field_12257 = g;
		this.field_12256 = h;
		this.field_12255 = i;
		this.field_12252 = j;
		this.field_12251 = k;
		this.field_12260 = l;
		this.field_12253 = m;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		class_2396<?> lv = class_2378.field_11141.method_10200(arg.readInt());
		if (lv == null) {
			lv = class_2398.field_11235;
		}

		this.field_12254 = arg.readBoolean();
		this.field_12258 = arg.readFloat();
		this.field_12257 = arg.readFloat();
		this.field_12256 = arg.readFloat();
		this.field_12255 = arg.readFloat();
		this.field_12252 = arg.readFloat();
		this.field_12251 = arg.readFloat();
		this.field_12260 = arg.readFloat();
		this.field_12253 = arg.readInt();
		this.field_12259 = this.method_11542(arg, (class_2396<class_2394>)lv);
	}

	private <T extends class_2394> T method_11542(class_2540 arg, class_2396<T> arg2) {
		return arg2.method_10298().method_10297(arg2, arg);
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(class_2378.field_11141.method_10249((class_2396<? extends class_2394>)this.field_12259.method_10295()));
		arg.writeBoolean(this.field_12254);
		arg.writeFloat(this.field_12258);
		arg.writeFloat(this.field_12257);
		arg.writeFloat(this.field_12256);
		arg.writeFloat(this.field_12255);
		arg.writeFloat(this.field_12252);
		arg.writeFloat(this.field_12251);
		arg.writeFloat(this.field_12260);
		arg.writeInt(this.field_12253);
		this.field_12259.method_10294(arg);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11552() {
		return this.field_12254;
	}

	@Environment(EnvType.CLIENT)
	public double method_11544() {
		return (double)this.field_12258;
	}

	@Environment(EnvType.CLIENT)
	public double method_11547() {
		return (double)this.field_12257;
	}

	@Environment(EnvType.CLIENT)
	public double method_11546() {
		return (double)this.field_12256;
	}

	@Environment(EnvType.CLIENT)
	public float method_11548() {
		return this.field_12255;
	}

	@Environment(EnvType.CLIENT)
	public float method_11549() {
		return this.field_12252;
	}

	@Environment(EnvType.CLIENT)
	public float method_11550() {
		return this.field_12251;
	}

	@Environment(EnvType.CLIENT)
	public float method_11543() {
		return this.field_12260;
	}

	@Environment(EnvType.CLIENT)
	public int method_11545() {
		return this.field_12253;
	}

	@Environment(EnvType.CLIENT)
	public class_2394 method_11551() {
		return this.field_12259;
	}

	public void method_11553(class_2602 arg) {
		arg.method_11077(this);
	}
}
