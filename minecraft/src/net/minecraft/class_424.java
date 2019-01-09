package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_424 extends class_437 {
	private final String field_2482;

	public class_424(String string) {
		this.field_2482 = string;
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2220(0);
		this.method_1789(this.field_2554, this.field_2482, this.field_2561 / 2, 70, 16777215);
		super.method_2214(i, j, f);
	}
}
