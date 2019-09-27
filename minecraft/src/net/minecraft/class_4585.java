package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4585 implements class_4588 {
	protected boolean field_20889 = false;
	protected int field_20890 = 255;
	protected int field_20891 = 255;
	protected int field_20892 = 255;
	protected int field_20893 = 255;
	protected boolean field_20894 = false;
	protected int field_20895 = 0;
	protected int field_20896 = 10;

	public void method_22901(int i, int j, int k, int l) {
		this.field_20890 = i;
		this.field_20891 = j;
		this.field_20892 = k;
		this.field_20893 = l;
		this.field_20889 = true;
	}

	@Override
	public void method_22922(int i, int j) {
		this.field_20895 = i;
		this.field_20896 = j;
		this.field_20894 = true;
	}

	@Override
	public void method_22923() {
		this.field_20894 = false;
	}
}
