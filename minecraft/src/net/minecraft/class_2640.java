package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2640 extends class_2586 {
	public class_2640(class_2591<?> arg) {
		super(arg);
	}

	public class_2640() {
		this(class_2591.field_11898);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11400(class_2350 arg) {
		return arg == class_2350.field_11036;
	}
}
