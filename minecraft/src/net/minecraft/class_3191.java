package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3191 {
	private final int field_13859;
	private final class_2338 field_13860;
	private int field_13858;
	private int field_13857;

	public class_3191(int i, class_2338 arg) {
		this.field_13859 = i;
		this.field_13860 = arg;
	}

	public class_2338 method_13991() {
		return this.field_13860;
	}

	public void method_13987(int i) {
		if (i > 10) {
			i = 10;
		}

		this.field_13858 = i;
	}

	public int method_13988() {
		return this.field_13858;
	}

	public void method_13989(int i) {
		this.field_13857 = i;
	}

	public int method_13990() {
		return this.field_13857;
	}
}
