package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4205 implements class_863.class_864 {
	private final class_310 field_18780;
	private final Map<Integer, List<class_4205.class_4206>> field_18781 = Maps.<Integer, List<class_4205.class_4206>>newHashMap();

	public void method_19430(int i, List<class_4205.class_4206> list) {
		this.field_18781.put(i, list);
	}

	public class_4205(class_310 arg) {
		this.field_18780 = arg;
	}

	@Override
	public void method_3715(long l) {
		class_4184 lv = this.field_18780.field_1773.method_19418();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		class_2338 lv2 = new class_2338(lv.method_19326().field_1352, 0.0, lv.method_19326().field_1350);
		this.field_18781.forEach((integer, list) -> {
			for (int i = 0; i < list.size(); i++) {
				class_4205.class_4206 lvx = (class_4205.class_4206)list.get(i);
				if (lv2.method_19771(lvx.field_18782, 160.0)) {
					double d = (double)lvx.field_18782.method_10263() + 0.5;
					double e = (double)lvx.field_18782.method_10264() + 2.0 + (double)i * 0.25;
					double f = (double)lvx.field_18782.method_10260() + 0.5;
					int j = lvx.field_18785 ? -16711936 : -3355444;
					class_863.method_3714(lvx.field_18784, d, e, f, j);
				}
			}
		});
		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	@Environment(EnvType.CLIENT)
	public static class class_4206 {
		public final class_2338 field_18782;
		public final int field_18783;
		public final String field_18784;
		public final boolean field_18785;

		public class_4206(class_2338 arg, int i, String string, boolean bl) {
			this.field_18782 = arg;
			this.field_18783 = i;
			this.field_18784 = string;
			this.field_18785 = bl;
		}
	}
}
