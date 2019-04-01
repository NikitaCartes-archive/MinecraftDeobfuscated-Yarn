package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_976<T extends class_1309, M extends class_583<T> & class_3882> extends class_3887<T, M> {
	public class_976(class_3883<T, M> arg) {
		super(arg);
	}

	public void method_17159(T arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6118(class_1304.field_6169);
		if (!lv.method_7960()) {
			class_1792 lv2 = lv.method_7909();
			GlStateManager.pushMatrix();
			if (arg.method_5715()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			boolean bl = arg instanceof class_1646 || arg instanceof class_1641;
			if (arg.method_6109() && !(arg instanceof class_1646)) {
				float m = 2.0F;
				float n = 1.4F;
				GlStateManager.translatef(0.0F, 0.5F * l, 0.0F);
				GlStateManager.scalef(0.7F, 0.7F, 0.7F);
				GlStateManager.translatef(0.0F, 16.0F * l, 0.0F);
			}

			this.method_17165().method_17148(0.0625F);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (lv2 instanceof class_1747 && ((class_1747)lv2).method_7711() instanceof class_2190) {
				float m = 1.1875F;
				GlStateManager.scalef(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					GlStateManager.translatef(0.0F, 0.0625F, 0.0F);
				}

				GameProfile gameProfile = null;
				if (lv.method_7985()) {
					class_2487 lv3 = lv.method_7969();
					if (lv3.method_10573("SkullOwner", 10)) {
						gameProfile = class_2512.method_10683(lv3.method_10562("SkullOwner"));
					} else if (lv3.method_10573("SkullOwner", 8)) {
						String string = lv3.method_10558("SkullOwner");
						if (!StringUtils.isBlank(string)) {
							gameProfile = class_2631.method_11335(new GameProfile(null, string));
							lv3.method_10566("SkullOwner", class_2512.method_10684(new class_2487(), gameProfile));
						}
					}
				}

				class_836.field_4392.method_3581(-0.5F, 0.0F, -0.5F, null, 180.0F, ((class_2190)((class_1747)lv2).method_7711()).method_9327(), gameProfile, -1, f);
			} else if (!(lv2 instanceof class_1738) || ((class_1738)lv2).method_7685() != class_1304.field_6169) {
				float mx = 0.625F;
				GlStateManager.translatef(0.0F, -0.25F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scalef(0.625F, -0.625F, -0.625F);
				if (bl) {
					GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
				}

				class_310.method_1551().method_1489().method_3233(arg, lv, class_809.class_811.field_4316);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
