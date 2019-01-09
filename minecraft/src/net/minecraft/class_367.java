package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_367 implements class_368 {
	private final class_161 field_2205;
	private boolean field_2206;

	public class_367(class_161 arg) {
		this.field_2205 = arg;
	}

	@Override
	public class_368.class_369 method_1986(class_374 arg, long l) {
		arg.method_1995().method_1531().method_4618(field_2207);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		class_185 lv = this.field_2205.method_686();
		arg.method_1788(0, 0, 0, 0, 160, 32);
		if (lv != null) {
			List<String> list = arg.method_1995().field_1772.method_1728(lv.method_811().method_10863(), 125);
			int i = lv.method_815() == class_189.field_1250 ? 16746751 : 16776960;
			if (list.size() == 1) {
				arg.method_1995().field_1772.method_1729(class_1074.method_4662("advancements.toast." + lv.method_815().method_831()), 30.0F, 7.0F, i | 0xFF000000);
				arg.method_1995().field_1772.method_1729(lv.method_811().method_10863(), 30.0F, 18.0F, -1);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (l < 1500L) {
					int k = class_3532.method_15375(class_3532.method_15363((float)(1500L - l) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					arg.method_1995().field_1772.method_1729(class_1074.method_4662("advancements.toast." + lv.method_815().method_831()), 30.0F, 11.0F, i | k);
				} else {
					int k = class_3532.method_15375(class_3532.method_15363((float)(l - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int m = 16 - list.size() * 9 / 2;

					for (String string : list) {
						arg.method_1995().field_1772.method_1729(string, 30.0F, (float)m, 16777215 | k);
						m += 9;
					}
				}
			}

			if (!this.field_2206 && l > 0L) {
				this.field_2206 = true;
				if (lv.method_815() == class_189.field_1250) {
					arg.method_1995().method_1483().method_4873(class_1109.method_4757(class_3417.field_15195, 1.0F, 1.0F));
				}
			}

			class_308.method_1453();
			arg.method_1995().method_1480().method_4026(null, lv.method_821(), 8, 8);
			return l >= 5000L ? class_368.class_369.field_2209 : class_368.class_369.field_2210;
		} else {
			return class_368.class_369.field_2209;
		}
	}
}
