package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_330 implements AutoCloseable {
	private static final class_2960 field_2044 = new class_2960("textures/map/map_icons.png");
	private final class_1060 field_2043;
	private final Map<String, class_330.class_331> field_2045 = Maps.<String, class_330.class_331>newHashMap();

	public class_330(class_1060 arg) {
		this.field_2043 = arg;
	}

	public void method_1769(class_22 arg) {
		this.method_1774(arg).method_1776();
	}

	public void method_1773(class_22 arg, boolean bl) {
		this.method_1774(arg).method_1777(bl);
	}

	private class_330.class_331 method_1774(class_22 arg) {
		class_330.class_331 lv = (class_330.class_331)this.field_2045.get(arg.method_76());
		if (lv == null) {
			lv = new class_330.class_331(arg);
			this.field_2045.put(arg.method_76(), lv);
		}

		return lv;
	}

	@Nullable
	public class_330.class_331 method_1768(String string) {
		return (class_330.class_331)this.field_2045.get(string);
	}

	public void method_1771() {
		for (class_330.class_331 lv : this.field_2045.values()) {
			lv.close();
		}

		this.field_2045.clear();
	}

	@Nullable
	public class_22 method_1772(@Nullable class_330.class_331 arg) {
		return arg != null ? arg.field_2046 : null;
	}

	public void close() {
		this.method_1771();
	}

	@Environment(EnvType.CLIENT)
	class class_331 implements AutoCloseable {
		private final class_22 field_2046;
		private final class_1043 field_2048;
		private final class_2960 field_2049;

		private class_331(class_22 arg2) {
			this.field_2046 = arg2;
			this.field_2048 = new class_1043(128, 128, true);
			this.field_2049 = class_330.this.field_2043.method_4617("map/" + arg2.method_76(), this.field_2048);
		}

		private void method_1776() {
			for (int i = 0; i < 128; i++) {
				for (int j = 0; j < 128; j++) {
					int k = j + i * 128;
					int l = this.field_2046.field_122[k] & 255;
					if (l / 4 == 0) {
						this.field_2048.method_4525().method_4305(j, i, (k + k / 128 & 1) * 8 + 16 << 24);
					} else {
						this.field_2048.method_4525().method_4305(j, i, class_3620.field_16006[l / 4].method_15820(l & 3));
					}
				}
			}

			this.field_2048.method_4524();
		}

		private void method_1777(boolean bl) {
			int i = 0;
			int j = 0;
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			float f = 0.0F;
			class_330.this.field_2043.method_4618(this.field_2049);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5146, GlStateManager.class_1027.field_5078
			);
			GlStateManager.disableAlphaTest();
			lv2.method_1328(7, class_290.field_1585);
			lv2.method_1315(0.0, 128.0, -0.01F).method_1312(0.0, 1.0).method_1344();
			lv2.method_1315(128.0, 128.0, -0.01F).method_1312(1.0, 1.0).method_1344();
			lv2.method_1315(128.0, 0.0, -0.01F).method_1312(1.0, 0.0).method_1344();
			lv2.method_1315(0.0, 0.0, -0.01F).method_1312(0.0, 0.0).method_1344();
			lv.method_1350();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			int k = 0;

			for (class_20 lv3 : this.field_2046.field_117.values()) {
				if (!bl || lv3.method_94()) {
					class_330.this.field_2043.method_4618(class_330.field_2044);
					GlStateManager.pushMatrix();
					GlStateManager.translatef(0.0F + (float)lv3.method_90() / 2.0F + 64.0F, 0.0F + (float)lv3.method_91() / 2.0F + 64.0F, -0.02F);
					GlStateManager.rotatef((float)(lv3.method_89() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.scalef(4.0F, 4.0F, 3.0F);
					GlStateManager.translatef(-0.125F, 0.125F, 0.0F);
					byte b = lv3.method_92();
					float g = (float)(b % 16 + 0) / 16.0F;
					float h = (float)(b / 16 + 0) / 16.0F;
					float l = (float)(b % 16 + 1) / 16.0F;
					float m = (float)(b / 16 + 1) / 16.0F;
					lv2.method_1328(7, class_290.field_1585);
					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					float n = -0.001F;
					lv2.method_1315(-1.0, 1.0, (double)((float)k * -0.001F)).method_1312((double)g, (double)h).method_1344();
					lv2.method_1315(1.0, 1.0, (double)((float)k * -0.001F)).method_1312((double)l, (double)h).method_1344();
					lv2.method_1315(1.0, -1.0, (double)((float)k * -0.001F)).method_1312((double)l, (double)m).method_1344();
					lv2.method_1315(-1.0, -1.0, (double)((float)k * -0.001F)).method_1312((double)g, (double)m).method_1344();
					lv.method_1350();
					GlStateManager.popMatrix();
					if (lv3.method_88() != null) {
						class_327 lv4 = class_310.method_1551().field_1772;
						String string = lv3.method_88().method_10863();
						float o = (float)lv4.method_1727(string);
						float p = class_3532.method_15363(25.0F / o, 0.0F, 6.0F / 9.0F);
						GlStateManager.pushMatrix();
						GlStateManager.translatef(0.0F + (float)lv3.method_90() / 2.0F + 64.0F - o * p / 2.0F, 0.0F + (float)lv3.method_91() / 2.0F + 64.0F + 4.0F, -0.025F);
						GlStateManager.scalef(p, p, 1.0F);
						class_329.method_1785(-1, -1, (int)o, 9 - 1, Integer.MIN_VALUE);
						GlStateManager.translatef(0.0F, 0.0F, -0.1F);
						lv4.method_1729(string, 0.0F, 0.0F, -1);
						GlStateManager.popMatrix();
					}

					k++;
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, -0.04F);
			GlStateManager.scalef(1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}

		public void close() {
			this.field_2048.close();
		}
	}
}
