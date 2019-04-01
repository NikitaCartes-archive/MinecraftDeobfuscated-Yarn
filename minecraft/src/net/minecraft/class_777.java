package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_777 {
	protected final int[] field_4175;
	protected final int field_4174;
	protected final class_2350 field_4173;
	protected final class_1058 field_4176;

	public class_777(int[] is, int i, class_2350 arg, class_1058 arg2) {
		this.field_4175 = is;
		this.field_4174 = i;
		this.field_4173 = arg;
		this.field_4176 = arg2;
	}

	public class_1058 method_3356() {
		return this.field_4176;
	}

	public int[] method_3357() {
		return this.field_4175;
	}

	public boolean method_3360() {
		return this.field_4174 != -1;
	}

	public int method_3359() {
		return this.field_4174;
	}

	public class_2350 method_3358() {
		return this.field_4173;
	}
}
