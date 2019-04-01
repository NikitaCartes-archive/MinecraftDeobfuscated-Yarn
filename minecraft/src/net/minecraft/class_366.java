package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_366 implements class_368 {
	private final List<class_1860<?>> field_2202 = Lists.<class_1860<?>>newArrayList();
	private long field_2204;
	private boolean field_2203;

	public class_366(class_1860<?> arg) {
		this.field_2202.add(arg);
	}

	@Override
	public class_368.class_369 method_1986(class_374 arg, long l) {
		if (this.field_2203) {
			this.field_2204 = l;
			this.field_2203 = false;
		}

		if (this.field_2202.isEmpty()) {
			return class_368.class_369.field_2209;
		} else {
			arg.method_1995().method_1531().method_4618(field_2207);
			GlStateManager.color3f(1.0F, 1.0F, 1.0F);
			arg.blit(0, 0, 0, 32, 160, 32);
			arg.method_1995().field_1772.method_1729(class_1074.method_4662("recipe.toast.title"), 30.0F, 7.0F, -11534256);
			arg.method_1995().field_1772.method_1729(class_1074.method_4662("recipe.toast.description"), 30.0F, 18.0F, -16777216);
			class_308.method_1453();
			class_1860<?> lv = (class_1860<?>)this.field_2202.get((int)(l / (5000L / (long)this.field_2202.size()) % (long)this.field_2202.size()));
			class_1799 lv2 = lv.method_17447();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6F, 0.6F, 1.0F);
			arg.method_1995().method_1480().method_4026(null, lv2, 3, 3);
			GlStateManager.popMatrix();
			arg.method_1995().method_1480().method_4026(null, lv.method_8110(), 8, 8);
			return l - this.field_2204 >= 5000L ? class_368.class_369.field_2209 : class_368.class_369.field_2210;
		}
	}

	public void method_1984(class_1860<?> arg) {
		if (this.field_2202.add(arg)) {
			this.field_2203 = true;
		}
	}

	public static void method_1985(class_374 arg, class_1860<?> arg2) {
		class_366 lv = arg.method_1997(class_366.class, field_2208);
		if (lv == null) {
			arg.method_1999(new class_366(arg2));
		} else {
			lv.method_1984(arg2);
		}
	}
}
