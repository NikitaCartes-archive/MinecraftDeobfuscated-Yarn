package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_5605 {
	public static final class_5605 field_27715 = new class_5605(0.0F);
	final float field_27716;
	final float field_27717;
	final float field_27718;

	public class_5605(float f, float g, float h) {
		this.field_27716 = f;
		this.field_27717 = g;
		this.field_27718 = h;
	}

	public class_5605(float f) {
		this(f, f, f);
	}

	public class_5605 method_32094(float f) {
		return new class_5605(this.field_27716 + f, this.field_27717 + f, this.field_27718 + f);
	}

	public class_5605 method_32095(float f, float g, float h) {
		return new class_5605(this.field_27716 + f, this.field_27717 + g, this.field_27718 + h);
	}
}
