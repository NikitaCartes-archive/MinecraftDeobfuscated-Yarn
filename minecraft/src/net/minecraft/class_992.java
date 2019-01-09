package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_992 extends class_3887<class_1452, class_587<class_1452>> {
	private static final class_2960 field_4888 = new class_2960("textures/entity/pig/pig_saddle.png");
	private final class_587<class_1452> field_4887 = new class_587<>(0.5F);

	public class_992(class_3883<class_1452, class_587<class_1452>> arg) {
		super(arg);
	}

	public void method_4196(class_1452 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_6575()) {
			this.method_17164(field_4888);
			this.method_17165().method_17081(this.field_4887);
			this.field_4887.method_2819(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
