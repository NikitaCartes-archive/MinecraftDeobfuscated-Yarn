package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_868 implements class_863.class_864 {
	private final class_310 field_4614;
	private final Map<Integer, class_11> field_4616 = Maps.<Integer, class_11>newHashMap();
	private final Map<Integer, Float> field_4617 = Maps.<Integer, Float>newHashMap();
	private final Map<Integer, Long> field_4615 = Maps.<Integer, Long>newHashMap();

	public class_868(class_310 arg) {
		this.field_4614 = arg;
	}

	public void method_3869(int i, class_11 arg, float f) {
		this.field_4616.put(i, arg);
		this.field_4615.put(i, class_156.method_658());
		this.field_4617.put(i, f);
	}

	@Override
	public void method_3715(long l) {
		if (!this.field_4616.isEmpty()) {
			long m = class_156.method_658();

			for (Integer integer : this.field_4616.keySet()) {
				class_11 lv = (class_11)this.field_4616.get(integer);
				float f = (Float)this.field_4617.get(integer);
				method_20556(this.method_20557(), lv, f, true, true);
			}

			for (Integer integer2 : (Integer[])this.field_4615.keySet().toArray(new Integer[0])) {
				if (m - (Long)this.field_4615.get(integer2) > 20000L) {
					this.field_4616.remove(integer2);
					this.field_4615.remove(integer2);
				}
			}
		}
	}

	public static void method_20556(class_4184 arg, class_11 arg2, float f, boolean bl, boolean bl2) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
		);
		GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		GlStateManager.disableTexture();
		GlStateManager.lineWidth(6.0F);
		method_20558(arg, arg2, f, bl, bl2);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private static void method_20558(class_4184 arg, class_11 arg2, float f, boolean bl, boolean bl2) {
		method_20555(arg, arg2);
		double d = arg.method_19326().field_1352;
		double e = arg.method_19326().field_1351;
		double g = arg.method_19326().field_1350;
		class_9 lv = arg2.method_48();
		if (method_20554(arg, lv) <= 40.0F) {
			class_863.method_19695(
				new class_238(
						(double)((float)lv.field_40 + 0.25F),
						(double)((float)lv.field_39 + 0.25F),
						(double)lv.field_38 + 0.25,
						(double)((float)lv.field_40 + 0.75F),
						(double)((float)lv.field_39 + 0.75F),
						(double)((float)lv.field_38 + 0.75F)
					)
					.method_989(-d, -e, -g),
				0.0F,
				1.0F,
				0.0F,
				0.5F
			);

			for (int i = 0; i < arg2.method_38(); i++) {
				class_9 lv2 = arg2.method_40(i);
				if (method_20554(arg, lv2) <= 40.0F) {
					float h = i == arg2.method_39() ? 1.0F : 0.0F;
					float j = i == arg2.method_39() ? 0.0F : 1.0F;
					class_863.method_19695(
						new class_238(
								(double)((float)lv2.field_40 + 0.5F - f),
								(double)((float)lv2.field_39 + 0.01F * (float)i),
								(double)((float)lv2.field_38 + 0.5F - f),
								(double)((float)lv2.field_40 + 0.5F + f),
								(double)((float)lv2.field_39 + 0.25F + 0.01F * (float)i),
								(double)((float)lv2.field_38 + 0.5F + f)
							)
							.method_989(-d, -e, -g),
						h,
						0.0F,
						j,
						0.5F
					);
				}
			}
		}

		if (bl) {
			for (class_9 lv3 : arg2.method_37()) {
				if (method_20554(arg, lv3) <= 40.0F) {
					class_863.method_3714(String.format("%s", lv3.field_41), (double)lv3.field_40 + 0.5, (double)lv3.field_39 + 0.75, (double)lv3.field_38 + 0.5, -65536);
					class_863.method_3714(
						String.format(Locale.ROOT, "%.2f", lv3.field_43), (double)lv3.field_40 + 0.5, (double)lv3.field_39 + 0.25, (double)lv3.field_38 + 0.5, -65536
					);
				}
			}

			for (class_9 lv3x : arg2.method_43()) {
				if (method_20554(arg, lv3x) <= 40.0F) {
					class_863.method_3714(
						String.format("%s", lv3x.field_41), (double)lv3x.field_40 + 0.5, (double)lv3x.field_39 + 0.75, (double)lv3x.field_38 + 0.5, -16776961
					);
					class_863.method_3714(
						String.format(Locale.ROOT, "%.2f", lv3x.field_43), (double)lv3x.field_40 + 0.5, (double)lv3x.field_39 + 0.25, (double)lv3x.field_38 + 0.5, -16776961
					);
				}
			}
		}

		if (bl2) {
			for (int ix = 0; ix < arg2.method_38(); ix++) {
				class_9 lv2 = arg2.method_40(ix);
				if (method_20554(arg, lv2) <= 40.0F) {
					class_863.method_3714(String.format("%s", lv2.field_41), (double)lv2.field_40 + 0.5, (double)lv2.field_39 + 0.75, (double)lv2.field_38 + 0.5, -1);
					class_863.method_3714(
						String.format(Locale.ROOT, "%.2f", lv2.field_43), (double)lv2.field_40 + 0.5, (double)lv2.field_39 + 0.25, (double)lv2.field_38 + 0.5, -1
					);
				}
			}
		}
	}

	public static void method_20555(class_4184 arg, class_11 arg2) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		double d = arg.method_19326().field_1352;
		double e = arg.method_19326().field_1351;
		double f = arg.method_19326().field_1350;
		lv2.method_1328(3, class_290.field_1576);

		for (int i = 0; i < arg2.method_38(); i++) {
			class_9 lv3 = arg2.method_40(i);
			if (!(method_20554(arg, lv3) > 40.0F)) {
				float g = (float)i / (float)arg2.method_38() * 0.33F;
				int j = i == 0 ? 0 : class_3532.method_15369(g, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				lv2.method_1315((double)lv3.field_40 - d + 0.5, (double)lv3.field_39 - e + 0.5, (double)lv3.field_38 - f + 0.5).method_1323(k, l, m, 255).method_1344();
			}
		}

		lv.method_1350();
	}

	private static float method_20554(class_4184 arg, class_9 arg2) {
		return (float)(
			Math.abs((double)arg2.field_40 - arg.method_19326().field_1352)
				+ Math.abs((double)arg2.field_39 - arg.method_19326().field_1351)
				+ Math.abs((double)arg2.field_38 - arg.method_19326().field_1350)
		);
	}

	private class_4184 method_20557() {
		return this.field_4614.field_1773.method_19418();
	}
}
