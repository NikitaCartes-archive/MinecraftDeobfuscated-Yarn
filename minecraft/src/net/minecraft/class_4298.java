package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4298 extends class_3887<class_1548, class_562<class_1548>> {
	private static final class_2960 field_19257 = new class_2960("textures/entity/creeper/creeper_special.png");
	private final class_562<class_1548> field_19258 = new class_562<>(2.0F);

	public class_4298(class_3883<class_1548, class_562<class_1548>> arg) {
		super(arg);
	}

	public void method_20284(class_1548 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_20232() && !arg.method_5767()) {
			this.method_17164(field_19257);
			this.method_17165().method_17081(this.field_19258);
			this.field_19258.method_2819(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
