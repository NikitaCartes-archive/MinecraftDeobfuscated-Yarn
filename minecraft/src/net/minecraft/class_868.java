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
	private class_1657 field_4618;
	private double field_4621;
	private double field_4620;
	private double field_4619;

	public class_868(class_310 arg) {
		this.field_4614 = arg;
	}

	public void method_3869(int i, class_11 arg, float f) {
		this.field_4616.put(i, arg);
		this.field_4615.put(i, class_156.method_658());
		this.field_4617.put(i, f);
	}

	@Override
	public void method_3715(float f, long l) {
		if (!this.field_4616.isEmpty()) {
			long m = class_156.method_658();
			this.field_4618 = this.field_4614.field_1724;
			this.field_4621 = class_3532.method_16436((double)f, this.field_4618.field_6038, this.field_4618.field_5987);
			this.field_4620 = class_3532.method_16436((double)f, this.field_4618.field_5971, this.field_4618.field_6010);
			this.field_4619 = class_3532.method_16436((double)f, this.field_4618.field_5989, this.field_4618.field_6035);
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
			GlStateManager.disableTexture();
			GlStateManager.lineWidth(6.0F);

			for (Integer integer : this.field_4616.keySet()) {
				class_11 lv = (class_11)this.field_4616.get(integer);
				float g = (Float)this.field_4617.get(integer);
				this.method_3868(f, lv);
				class_9 lv2 = lv.method_48();
				if (!(this.method_3867(lv2) > 40.0F)) {
					class_761.method_3261(
						new class_238(
								(double)((float)lv2.field_40 + 0.25F),
								(double)((float)lv2.field_39 + 0.25F),
								(double)lv2.field_38 + 0.25,
								(double)((float)lv2.field_40 + 0.75F),
								(double)((float)lv2.field_39 + 0.75F),
								(double)((float)lv2.field_38 + 0.75F)
							)
							.method_989(-this.field_4621, -this.field_4620, -this.field_4619),
						0.0F,
						1.0F,
						0.0F,
						0.5F
					);

					for (int i = 0; i < lv.method_38(); i++) {
						class_9 lv3 = lv.method_40(i);
						if (!(this.method_3867(lv3) > 40.0F)) {
							float h = i == lv.method_39() ? 1.0F : 0.0F;
							float j = i == lv.method_39() ? 0.0F : 1.0F;
							class_761.method_3261(
								new class_238(
										(double)((float)lv3.field_40 + 0.5F - g),
										(double)((float)lv3.field_39 + 0.01F * (float)i),
										(double)((float)lv3.field_38 + 0.5F - g),
										(double)((float)lv3.field_40 + 0.5F + g),
										(double)((float)lv3.field_39 + 0.25F + 0.01F * (float)i),
										(double)((float)lv3.field_38 + 0.5F + g)
									)
									.method_989(-this.field_4621, -this.field_4620, -this.field_4619),
								h,
								0.0F,
								j,
								0.5F
							);
						}
					}
				}
			}

			for (Integer integerx : this.field_4616.keySet()) {
				class_11 lv = (class_11)this.field_4616.get(integerx);

				for (class_9 lv3 : lv.method_37()) {
					if (!(this.method_3867(lv3) > 40.0F)) {
						class_863.method_3714(String.format("%s", lv3.field_41), (double)lv3.field_40 + 0.5, (double)lv3.field_39 + 0.75, (double)lv3.field_38 + 0.5, f, -65536);
						class_863.method_3714(
							String.format(Locale.ROOT, "%.2f", lv3.field_43), (double)lv3.field_40 + 0.5, (double)lv3.field_39 + 0.25, (double)lv3.field_38 + 0.5, f, -65536
						);
					}
				}

				for (class_9 lv3x : lv.method_43()) {
					if (!(this.method_3867(lv3x) > 40.0F)) {
						class_863.method_3714(
							String.format("%s", lv3x.field_41), (double)lv3x.field_40 + 0.5, (double)lv3x.field_39 + 0.75, (double)lv3x.field_38 + 0.5, f, -16776961
						);
						class_863.method_3714(
							String.format(Locale.ROOT, "%.2f", lv3x.field_43), (double)lv3x.field_40 + 0.5, (double)lv3x.field_39 + 0.25, (double)lv3x.field_38 + 0.5, f, -16776961
						);
					}
				}

				for (int k = 0; k < lv.method_38(); k++) {
					class_9 lv2 = lv.method_40(k);
					if (!(this.method_3867(lv2) > 40.0F)) {
						class_863.method_3714(String.format("%s", lv2.field_41), (double)lv2.field_40 + 0.5, (double)lv2.field_39 + 0.75, (double)lv2.field_38 + 0.5, f, -1);
						class_863.method_3714(
							String.format(Locale.ROOT, "%.2f", lv2.field_43), (double)lv2.field_40 + 0.5, (double)lv2.field_39 + 0.25, (double)lv2.field_38 + 0.5, f, -1
						);
					}
				}
			}

			for (Integer integer2 : (Integer[])this.field_4615.keySet().toArray(new Integer[0])) {
				if (m - (Long)this.field_4615.get(integer2) > 20000L) {
					this.field_4616.remove(integer2);
					this.field_4615.remove(integer2);
				}
			}

			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	public void method_3868(float f, class_11 arg) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(3, class_290.field_1576);

		for (int i = 0; i < arg.method_38(); i++) {
			class_9 lv3 = arg.method_40(i);
			if (!(this.method_3867(lv3) > 40.0F)) {
				float g = (float)i / (float)arg.method_38() * 0.33F;
				int j = i == 0 ? 0 : class_3532.method_15369(g, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				lv2.method_1315((double)lv3.field_40 - this.field_4621 + 0.5, (double)lv3.field_39 - this.field_4620 + 0.5, (double)lv3.field_38 - this.field_4619 + 0.5)
					.method_1323(k, l, m, 255)
					.method_1344();
			}
		}

		lv.method_1350();
	}

	private float method_3867(class_9 arg) {
		return (float)(
			Math.abs((double)arg.field_40 - this.field_4618.field_5987)
				+ Math.abs((double)arg.field_39 - this.field_4618.field_6010)
				+ Math.abs((double)arg.field_38 - this.field_4618.field_6035)
		);
	}
}
