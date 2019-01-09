package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_958 extends class_927<class_1481, class_614<class_1481>> {
	private static final class_2960 field_4798 = new class_2960("textures/entity/turtle/big_sea_turtle.png");

	public class_958(class_898 arg) {
		super(arg, new class_614<>(0.0F), 0.35F);
	}

	public void method_4138(class_1481 arg, double d, double e, double f, float g, float h) {
		if (arg.method_6109()) {
			this.field_4673 *= 0.5F;
		}

		super.method_4072(arg, d, e, f, g, h);
	}

	@Nullable
	protected class_2960 method_4139(class_1481 arg) {
		return field_4798;
	}
}
