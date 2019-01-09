package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_835 extends class_827<class_2669> {
	private final class_776 field_4389 = class_310.method_1551().method_1541();

	public void method_3576(class_2669 arg, double d, double e, double f, float g, int i) {
		class_2338 lv = arg.method_11016().method_10093(arg.method_11506().method_10153());
		class_2680 lv2 = arg.method_11495();
		if (!lv2.method_11588() && !(arg.method_11499(g) >= 1.0F)) {
			class_289 lv3 = class_289.method_1348();
			class_287 lv4 = lv3.method_1349();
			this.method_3566(class_1059.field_5275);
			class_308.method_1450();
			GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			if (class_310.method_1588()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			lv4.method_1328(7, class_290.field_1582);
			lv4.method_1331(
				d - (double)lv.method_10263() + (double)arg.method_11494(g),
				e - (double)lv.method_10264() + (double)arg.method_11511(g),
				f - (double)lv.method_10260() + (double)arg.method_11507(g)
			);
			class_1937 lv5 = this.method_3565();
			if (lv2.method_11614() == class_2246.field_10379 && arg.method_11499(g) <= 4.0F) {
				lv2 = lv2.method_11657(class_2671.field_12227, Boolean.valueOf(true));
				this.method_3575(lv, lv2, lv4, lv5, false);
			} else if (arg.method_11515() && !arg.method_11501()) {
				class_2764 lv6 = lv2.method_11614() == class_2246.field_10615 ? class_2764.field_12634 : class_2764.field_12637;
				class_2680 lv7 = class_2246.field_10379
					.method_9564()
					.method_11657(class_2671.field_12224, lv6)
					.method_11657(class_2671.field_10927, lv2.method_11654(class_2665.field_10927));
				lv7 = lv7.method_11657(class_2671.field_12227, Boolean.valueOf(arg.method_11499(g) >= 0.5F));
				this.method_3575(lv, lv7, lv4, lv5, false);
				class_2338 lv8 = lv.method_10093(arg.method_11506());
				lv4.method_1331(d - (double)lv8.method_10263(), e - (double)lv8.method_10264(), f - (double)lv8.method_10260());
				lv2 = lv2.method_11657(class_2665.field_12191, Boolean.valueOf(true));
				this.method_3575(lv8, lv2, lv4, lv5, true);
			} else {
				this.method_3575(lv, lv2, lv4, lv5, false);
			}

			lv4.method_1331(0.0, 0.0, 0.0);
			lv3.method_1350();
			class_308.method_1452();
		}
	}

	private boolean method_3575(class_2338 arg, class_2680 arg2, class_287 arg3, class_1937 arg4, boolean bl) {
		return this.field_4389.method_3350().method_3374(arg4, this.field_4389.method_3349(arg2), arg2, arg, arg3, bl, new Random(), arg2.method_11617(arg));
	}
}
