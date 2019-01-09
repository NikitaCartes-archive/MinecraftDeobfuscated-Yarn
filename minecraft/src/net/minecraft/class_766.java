package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_766 {
	private final class_310 field_4139;
	private final class_751 field_4141;
	private float field_4140;

	public class_766(class_751 arg) {
		this.field_4141 = arg;
		this.field_4139 = class_310.method_1551();
	}

	public void method_3317(float f) {
		this.field_4140 += f;
		this.field_4141.method_3156(this.field_4139, class_3532.method_15374(this.field_4140 * 0.001F) * 5.0F + 25.0F, -this.field_4140 * 0.1F);
		this.field_4139.field_1704.method_4493(class_310.field_1703);
	}
}
