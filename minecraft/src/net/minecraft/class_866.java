package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_866 implements class_863.class_864 {
	private final class_310 field_4612;

	public class_866(class_310 arg) {
		this.field_4612 = arg;
	}

	@Override
	public void method_3715(long l) {
		class_4184 lv = this.field_4612.field_1773.method_19418();
		class_1937 lv2 = this.field_4612.field_1687;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.disableTexture();
		class_2338 lv3 = new class_2338(lv.method_19326());
		LongSet longSet = new LongOpenHashSet();

		for (class_2338 lv4 : class_2338.method_10097(lv3.method_10069(-10, -10, -10), lv3.method_10069(10, 10, 10))) {
			int i = lv2.method_8314(class_1944.field_9284, lv4);
			float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = class_3532.method_15369(f, 0.9F, 0.9F);
			long m = class_4076.method_18691(lv4.method_10063());
			if (longSet.add(m)) {
				class_863.method_19429(
					lv2.method_8398().method_12130().method_15564(class_1944.field_9284, class_4076.method_18677(m)),
					(double)(class_4076.method_18686(m) * 16 + 8),
					(double)(class_4076.method_18689(m) * 16 + 8),
					(double)(class_4076.method_18690(m) * 16 + 8),
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				class_863.method_3714(String.valueOf(i), (double)lv4.method_10263() + 0.5, (double)lv4.method_10264() + 0.25, (double)lv4.method_10260() + 0.5, j);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
