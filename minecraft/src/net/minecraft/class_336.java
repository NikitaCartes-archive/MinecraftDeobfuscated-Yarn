package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_336 implements class_334 {
	private final class_310 field_2057;

	public class_336(class_310 arg) {
		this.field_2057 = arg;
	}

	@Override
	public void method_1794(class_2556 arg, class_2561 arg2) {
		this.field_2057.field_1705.method_1758(arg2, false);
	}
}
