package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_979<T extends class_1309, M extends class_583<T>> extends class_3887<T, M> {
	private static final class_2960 field_4850 = new class_2960("textures/entity/elytra.png");
	private final class_563<T> field_4852 = new class_563<>();

	public class_979(class_3883<T, M> arg) {
		super(arg);
	}

	public void method_17161(T arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6118(class_1304.field_6174);
		if (lv.method_7909() == class_1802.field_8833) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084);
			if (arg instanceof class_742) {
				class_742 lv2 = (class_742)arg;
				if (lv2.method_3126() && lv2.method_3122() != null) {
					this.method_17164(lv2.method_3122());
				} else if (lv2.method_3125() && lv2.method_3119() != null && lv2.method_7348(class_1664.field_7559)) {
					this.method_17164(lv2.method_3119());
				} else {
					this.method_17164(field_4850);
				}
			} else {
				this.method_17164(field_4850);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 0.125F);
			this.field_4852.method_17079(arg, f, g, i, j, k, l);
			this.field_4852.method_17078(arg, f, g, i, j, k, l);
			if (lv.method_7942()) {
				class_970.method_4171(this::method_17164, arg, this.field_4852, f, g, h, i, j, k, l);
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
