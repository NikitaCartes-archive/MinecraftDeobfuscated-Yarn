package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_618 {
	public final class_243 field_3605;
	public final float field_3604;
	public final float field_3603;

	public class_618(float f, float g, float h, float i, float j) {
		this(new class_243((double)f, (double)g, (double)h), i, j);
	}

	public class_618 method_2837(float f, float g) {
		return new class_618(this, f, g);
	}

	public class_618(class_618 arg, float f, float g) {
		this.field_3605 = arg.field_3605;
		this.field_3604 = f;
		this.field_3603 = g;
	}

	public class_618(class_243 arg, float f, float g) {
		this.field_3605 = arg;
		this.field_3604 = f;
		this.field_3603 = g;
	}
}
