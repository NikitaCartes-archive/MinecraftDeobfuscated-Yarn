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
	public void method_3715(long l) {
		class_4184 lv = this.field_4505.field_1773.method_19418();
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.disableTexture();
		class_2338 lv2 = new class_2338(lv.method_19326().field_1352, 0.0, lv.method_19326().field_1350);
		class_289 lv3 = class_289.method_1348();
		class_287 lv4 = lv3.method_1349();
		lv4.method_1328(5, class_290.field_1576);

		for (Entry<class_2338, class_2338> entry : this.field_4507.entrySet()) {
			class_2338 lv5 = (class_2338)entry.getKey();
			class_2338 lv6 = (class_2338)entry.getValue();
			float g = (float)(lv6.method_10263() * 128 % 256) / 256.0F;
			float h = (float)(lv6.method_10264() * 128 % 256) / 256.0F;
			float i = (float)(lv6.method_10260() * 128 % 256) / 256.0F;
			float j = (Float)this.field_4508.get(lv5);
			if (lv2.method_19771(lv5, 160.0)) {
				class_761.method_3253(
					lv4,
					(double)((float)lv5.method_10263() + 0.5F) - d - (double)j,
					(double)((float)lv5.method_10264() + 0.5F) - e - (double)j,
					(double)((float)lv5.method_10260() + 0.5F) - f - (double)j,
					(double)((float)lv5.method_10263() + 0.5F) - d + (double)j,
					(double)((float)lv5.method_10264() + 0.5F) - e + (double)j,
					(double)((float)lv5.method_10260() + 0.5F) - f + (double)j,
					g,
					h,
					i,
					0.5F
				);
			}
		}

		for (class_2338 lv7 : this.field_4506) {
			if (lv2.method_19771(lv7, 160.0)) {
				class_761.method_3253(
					lv4,
					(double)lv7.method_10263() - d,
					(double)lv7.method_10264() - e,
					(double)lv7.method_10260() - f,
					(double)((float)lv7.method_10263() + 1.0F) - d,
					(double)((float)lv7.method_10264() + 1.0F) - e,
					(double)((float)lv7.method_10260() + 1.0F) - f,
					1.0F,
					1.0F,
					1.0F,
					1.0F
				);
			}
		}

		lv3.method_1350();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
