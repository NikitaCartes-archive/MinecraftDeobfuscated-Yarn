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
	public void method_3715(float f, long l) {
		class_1657 lv = this.field_4612.field_1724;
		class_1937 lv2 = this.field_4612.field_1687;
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.disableTexture();
		class_2338 lv3 = new class_2338(lv.field_5987, lv.field_6010, lv.field_6035);
		Iterable<class_2338> iterable = class_2338.method_10097(lv3.method_10069(-10, -10, -10), lv3.method_10069(10, 10, 10));
		LongSet longSet = new LongOpenHashSet();

		for (class_2338 lv4 : iterable) {
			int i = lv2.method_8314(class_1944.field_9284, lv4);
			float h = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
			int j = class_3532.method_15369(h, 0.9F, 0.9F);
			long m = class_2338.method_10090(lv4.method_10063());
			if (longSet.add(m)) {
				class_863.method_3712(
					lv2.method_8398().method_12130().method_15564(class_1944.field_9284, class_2338.method_10092(m)),
					(double)(class_2338.method_10061(m) + 8),
					(double)(class_2338.method_10071(m) + 8),
					(double)(class_2338.method_10083(m) + 8),
					1.0F,
					16711680,
					0.3F
				);
			}

			if (i != 15) {
				class_863.method_3714(String.valueOf(i), (double)lv4.method_10263() + 0.5, (double)lv4.method_10264() + 0.25, (double)lv4.method_10260() + 0.5, 1.0F, j);
			}
		}

		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}
}
