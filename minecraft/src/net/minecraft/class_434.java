package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_434 extends class_437 {
	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2220(0);
		this.method_1789(this.field_2554, class_1074.method_4662("multiplayer.downloadingTerrain"), this.field_2561 / 2, this.field_2559 / 2 - 50, 16777215);
		super.method_2214(i, j, f);
	}

	@Override
	public boolean method_2222() {
		return false;
	}
}
