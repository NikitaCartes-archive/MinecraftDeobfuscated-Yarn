package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_768 {
	private int field_4145;
	private int field_4144;
	private int field_4143;
	private int field_4142;

	public class_768(int i, int j, int k, int l) {
		this.field_4145 = i;
		this.field_4144 = j;
		this.field_4143 = k;
		this.field_4142 = l;
	}

	public int method_3321() {
		return this.field_4145;
	}

	public int method_3322() {
		return this.field_4144;
	}

	public int method_3319() {
		return this.field_4143;
	}

	public int method_3320() {
		return this.field_4142;
	}

	public boolean method_3318(int i, int j) {
		return i >= this.field_4145 && i <= this.field_4145 + this.field_4143 && j >= this.field_4144 && j <= this.field_4144 + this.field_4142;
	}
}
