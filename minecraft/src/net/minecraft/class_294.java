package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_294 extends class_286 {
	private class_291 field_1604;

	@Override
	public void method_1309(class_287 arg) {
		arg.method_1343();
		this.field_1604.method_1352(arg.method_1342());
	}

	public void method_1372(class_291 arg) {
		this.field_1604 = arg;
	}
}
