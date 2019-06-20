package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3966 extends class_239 {
	private final class_1297 field_17592;
	private final float field_23656;

	public class_3966(class_1297 arg, float f) {
		this(arg, new class_243(arg.field_5987, arg.field_6010, arg.field_6035), f);
	}

	public class_3966(class_1297 arg, class_243 arg2, float f) {
		super(arg2);
		this.field_17592 = arg;
		this.field_23656 = f;
	}

	public class_1297 method_17782() {
		return this.field_17592;
	}

	@Override
	public class_239.class_240 method_17783() {
		return class_239.class_240.field_1331;
	}

	@Environment(EnvType.CLIENT)
	public float method_26747() {
		return this.field_23656;
	}
}
