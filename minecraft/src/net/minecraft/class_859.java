package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_859 implements class_863.class_864 {
	private final class_310 field_4505;
	private final Map<class_2338, class_2338> field_4507 = Maps.<class_2338, class_2338>newHashMap();
	private final Map<class_2338, Float> field_4508 = Maps.<class_2338, Float>newHashMap();
	private final List<class_2338> field_4506 = Lists.<class_2338>newArrayList();

	public class_859(class_310 arg) {
		this.field_4505 = arg;
	}

	public void method_3704(class_2338 arg, List<class_2338> list, List<Float> list2) {
		for (int i = 0; i < list.size(); i++) {
			this.field_4507.put(list.get(i), arg);
			this.field_4508.put(list.get(i), list2.get(i));
		}

		this.field_4506.add(arg);
	}

	@Override
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4505.field_1724;
		class_1922 lv2 = this.field_4505.field_1687;
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.disableTexture();
		class_2338 lv3 = new class_2338(lv.field_5987, 0.0, lv.field_6035);
		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		lv5.method_1328(5, class_290.field_1576);

		for (Entry<class_2338, class_2338> entry : this.field_4507.entrySet()) {
			class_2338 lv6 = (class_2338)entry.getKey();
			class_2338 lv7 = (class_2338)entry.getValue();
			float h = (float)(lv7.method_10263() * 128 % 256) / 256.0F;
			float i = (float)(lv7.method_10264() * 128 % 256) / 256.0F;
			float j = (float)(lv7.method_10260() * 128 % 256) / 256.0F;
			float k = (Float)this.field_4508.get(lv6);
			if (lv3.method_10267(lv6) < 160.0) {
				class_761.method_3253(
					lv5,
					(double)((float)lv6.method_10263() + 0.5F) - d - (double)k,
					(double)((float)lv6.method_10264() + 0.5F) - e - (double)k,
					(double)((float)lv6.method_10260() + 0.5F) - g - (double)k,
					(double)((float)lv6.method_10263() + 0.5F) - d + (double)k,
					(double)((float)lv6.method_10264() + 0.5F) - e + (double)k,
					(double)((float)lv6.method_10260() + 0.5F) - g + (double)k,
					h,
					i,
					j,
					0.5F
				);
			}
		}

		for (class_2338 lv8 : this.field_4506) {
			if (lv3.method_10267(lv8) < 160.0) {
				class_761.method_3253(
					lv5,
					(double)lv8.method_10263() - d,
					(double)lv8.method_10264() - e,
					(double)lv8.method_10260() - g,
					(double)((float)lv8.method_10263() + 1.0F) - d,
					(double)((float)lv8.method_10264() + 1.0F) - e,
					(double)((float)lv8.method_10260() + 1.0F) - g,
					1.0F,
					1.0F,
					1.0F,
					1.0F
				);
			}
		}

		lv4.method_1350();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
