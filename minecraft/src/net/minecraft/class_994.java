package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_994 extends class_3887<class_1472, class_601<class_1472>> {
	private static final class_2960 field_4892 = new class_2960("textures/entity/sheep/sheep_fur.png");
	private final class_598<class_1472> field_4891 = new class_598<>();

	public class_994(class_3883<class_1472, class_601<class_1472>> arg) {
		super(arg);
	}

	public void method_4198(class_1472 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (!arg.method_6629() && !arg.method_5767()) {
			this.method_17164(field_4892);
			if (arg.method_16914() && "jeb_".equals(arg.method_5477().method_10851())) {
				int m = 25;
				int n = arg.field_6012 / 25 + arg.method_5628();
				int o = class_1767.values().length;
				int p = n % o;
				int q = (n + 1) % o;
				float r = ((float)(arg.field_6012 % 25) + h) / 25.0F;
				float[] fs = class_1472.method_6634(class_1767.method_7791(p));
				float[] gs = class_1472.method_6634(class_1767.method_7791(q));
				GlStateManager.color3f(fs[0] * (1.0F - r) + gs[0] * r, fs[1] * (1.0F - r) + gs[1] * r, fs[2] * (1.0F - r) + gs[2] * r);
			} else {
				float[] hs = class_1472.method_6634(arg.method_6633());
				GlStateManager.color3f(hs[0], hs[1], hs[2]);
			}

			this.method_17165().method_17081(this.field_4891);
			this.field_4891.method_17118(arg, f, g, h);
			this.field_4891.method_2819(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
