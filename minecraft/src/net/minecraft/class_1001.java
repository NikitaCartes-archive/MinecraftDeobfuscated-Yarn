package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1001 extends class_3887<class_1474, class_583<class_1474>> {
	private final class_612<class_1474> field_17157 = new class_612<>(0.008F);
	private final class_615<class_1474> field_4903 = new class_615<>(0.008F);

	public class_1001(class_3883<class_1474, class_583<class_1474>> arg) {
		super(arg);
	}

	public void method_4205(class_1474 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (!arg.method_5767()) {
			class_583<class_1474> lv = (class_583<class_1474>)(arg.method_6654() == 0 ? this.field_17157 : this.field_4903);
			this.method_17164(arg.method_6646());
			float[] fs = arg.method_6655();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.method_17165().method_17081(lv);
			lv.method_2816(arg, f, g, h);
			lv.method_2819(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
