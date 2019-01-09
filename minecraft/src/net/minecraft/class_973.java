package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_973<T extends class_1309, M extends class_583<T>> extends class_3887<T, M> {
	private final class_898 field_17153;

	public class_973(class_922<T, M> arg) {
		super(arg);
		this.field_17153 = arg.method_3940();
	}

	public void method_17158(T arg, float f, float g, float h, float i, float j, float k, float l) {
		int m = arg.method_6022();
		if (m > 0) {
			class_1297 lv = new class_1667(arg.field_6002, arg.field_5987, arg.field_6010, arg.field_6035);
			Random random = new Random((long)arg.method_5628());
			class_308.method_1450();

			for (int n = 0; n < m; n++) {
				GlStateManager.pushMatrix();
				class_630 lv2 = this.method_17165().method_17101(random);
				class_628 lv3 = (class_628)lv2.field_3663.get(random.nextInt(lv2.field_3663.size()));
				lv2.method_2847(0.0625F);
				float o = random.nextFloat();
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = class_3532.method_16439(o, lv3.field_3645, lv3.field_3648) / 16.0F;
				float s = class_3532.method_16439(p, lv3.field_3644, lv3.field_3647) / 16.0F;
				float t = class_3532.method_16439(q, lv3.field_3643, lv3.field_3646) / 16.0F;
				GlStateManager.translatef(r, s, t);
				o = o * 2.0F - 1.0F;
				p = p * 2.0F - 1.0F;
				q = q * 2.0F - 1.0F;
				o *= -1.0F;
				p *= -1.0F;
				q *= -1.0F;
				float u = class_3532.method_15355(o * o + q * q);
				lv.field_6031 = (float)(Math.atan2((double)o, (double)q) * 180.0F / (float)Math.PI);
				lv.field_5965 = (float)(Math.atan2((double)p, (double)u) * 180.0F / (float)Math.PI);
				lv.field_5982 = lv.field_6031;
				lv.field_6004 = lv.field_5965;
				double d = 0.0;
				double e = 0.0;
				double v = 0.0;
				this.field_17153.method_3954(lv, 0.0, 0.0, 0.0, 0.0F, h, false);
				GlStateManager.popMatrix();
			}

			class_308.method_1452();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
