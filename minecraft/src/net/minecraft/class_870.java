package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_870 implements class_863.class_864 {
	private final class_310 field_4624;
	private final Map<Integer, Map<String, class_3341>> field_4626 = Maps.<Integer, Map<String, class_3341>>newHashMap();
	private final Map<Integer, Map<String, class_3341>> field_4627 = Maps.<Integer, Map<String, class_3341>>newHashMap();
	private final Map<Integer, Map<String, Boolean>> field_4625 = Maps.<Integer, Map<String, Boolean>>newHashMap();

	public class_870(class_310 arg) {
		this.field_4624 = arg;
	}

	@Override
	public void method_3715(long l) {
		class_4184 lv = this.field_4624.field_1773.method_19418();
		class_1936 lv2 = this.field_4624.field_1687;
		int i = lv2.method_8401().method_218();
		double d = lv.method_19326().field_1352;
		double e = lv.method_19326().field_1351;
		double f = lv.method_19326().field_1350;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		GlStateManager.disableDepthTest();
		class_2338 lv3 = new class_2338(lv.method_19326().field_1352, 0.0, lv.method_19326().field_1350);
		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		lv5.method_1328(3, class_290.field_1576);
		GlStateManager.lineWidth(1.0F);
		if (this.field_4626.containsKey(i)) {
			for (class_3341 lv6 : ((Map)this.field_4626.get(i)).values()) {
				if (lv3.method_19771(lv6.method_19635(), 500.0)) {
					class_761.method_3258(
						lv5,
						(double)lv6.field_14381 - d,
						(double)lv6.field_14380 - e,
						(double)lv6.field_14379 - f,
						(double)(lv6.field_14378 + 1) - d,
						(double)(lv6.field_14377 + 1) - e,
						(double)(lv6.field_14376 + 1) - f,
						1.0F,
						1.0F,
						1.0F,
						1.0F
					);
				}
			}
		}

		if (this.field_4627.containsKey(i)) {
			for (Entry<String, class_3341> entry : ((Map)this.field_4627.get(i)).entrySet()) {
				String string = (String)entry.getKey();
				class_3341 lv7 = (class_3341)entry.getValue();
				Boolean boolean_ = (Boolean)((Map)this.field_4625.get(i)).get(string);
				if (lv3.method_19771(lv7.method_19635(), 500.0)) {
					if (boolean_) {
						class_761.method_3258(
							lv5,
							(double)lv7.field_14381 - d,
							(double)lv7.field_14380 - e,
							(double)lv7.field_14379 - f,
							(double)(lv7.field_14378 + 1) - d,
							(double)(lv7.field_14377 + 1) - e,
							(double)(lv7.field_14376 + 1) - f,
							0.0F,
							1.0F,
							0.0F,
							1.0F
						);
					} else {
						class_761.method_3258(
							lv5,
							(double)lv7.field_14381 - d,
							(double)lv7.field_14380 - e,
							(double)lv7.field_14379 - f,
							(double)(lv7.field_14378 + 1) - d,
							(double)(lv7.field_14377 + 1) - e,
							(double)(lv7.field_14376 + 1) - f,
							0.0F,
							0.0F,
							1.0F,
							1.0F
						);
					}
				}
			}
		}

		lv4.method_1350();
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	public void method_3871(class_3341 arg, List<class_3341> list, List<Boolean> list2, int i) {
		if (!this.field_4626.containsKey(i)) {
			this.field_4626.put(i, Maps.newHashMap());
		}

		if (!this.field_4627.containsKey(i)) {
			this.field_4627.put(i, Maps.newHashMap());
			this.field_4625.put(i, Maps.newHashMap());
		}

		((Map)this.field_4626.get(i)).put(arg.toString(), arg);

		for (int j = 0; j < list.size(); j++) {
			class_3341 lv = (class_3341)list.get(j);
			Boolean boolean_ = (Boolean)list2.get(j);
			((Map)this.field_4627.get(i)).put(lv.toString(), lv);
			((Map)this.field_4625.get(i)).put(lv.toString(), boolean_);
		}
	}
}
