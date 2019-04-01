package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_512 extends class_361 {
	private final class_314 field_3123;
	private float field_3122;

	public class_512(class_314 arg) {
		super(0, 0, 35, 27, false);
		this.field_3123 = arg;
		this.method_1962(153, 2, 35, 0, class_507.field_3097);
	}

	public void method_2622(class_310 arg) {
		class_299 lv = arg.field_1724.method_3130();
		List<class_516> list = lv.method_1396(this.field_3123);
		if (arg.field_1724.field_7512 instanceof class_1729) {
			for (class_516 lv2 : list) {
				for (class_1860<?> lv3 : lv2.method_2651(lv.method_14880((class_1729<?>)arg.field_1724.field_7512))) {
					if (lv.method_14883(lv3)) {
						this.field_3122 = 15.0F;
						return;
					}
				}
			}
		}
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.field_3122 > 0.0F) {
			float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_3122 / 15.0F * (float) Math.PI));
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
			GlStateManager.scalef(1.0F, g, 1.0F);
			GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
		}

		class_310 lv = class_310.method_1551();
		lv.method_1531().method_4618(this.field_2193);
		GlStateManager.disableDepthTest();
		int k = this.field_2192;
		int l = this.field_2191;
		if (this.field_2194) {
			k += this.field_2190;
		}

		if (this.isHovered()) {
			l += this.field_2189;
		}

		int m = this.x;
		if (this.field_2194) {
			m -= 2;
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.blit(m, this.y, k, l, this.width, this.height);
		GlStateManager.enableDepthTest();
		class_308.method_1453();
		GlStateManager.disableLighting();
		this.method_2621(lv.method_1480());
		GlStateManager.enableLighting();
		class_308.method_1450();
		if (this.field_3122 > 0.0F) {
			GlStateManager.popMatrix();
			this.field_3122 -= f;
		}
	}

	private void method_2621(class_918 arg) {
		List<class_1799> list = this.field_3123.method_1623();
		int i = this.field_2194 ? -2 : 0;
		if (list.size() == 1) {
			arg.method_4023((class_1799)list.get(0), this.x + 9 + i, this.y + 5);
		} else if (list.size() == 2) {
			arg.method_4023((class_1799)list.get(0), this.x + 3 + i, this.y + 5);
			arg.method_4023((class_1799)list.get(1), this.x + 14 + i, this.y + 5);
		}
	}

	public class_314 method_2623() {
		return this.field_3123;
	}

	public boolean method_2624(class_299 arg) {
		List<class_516> list = arg.method_1396(this.field_3123);
		this.visible = false;
		if (list != null) {
			for (class_516 lv : list) {
				if (lv.method_2652() && lv.method_2657()) {
					this.visible = true;
					break;
				}
			}
		}

		return this.visible;
	}
}
