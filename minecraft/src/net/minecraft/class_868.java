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
	private class_4184 field_4618;
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
	public void method_3715(long l) {
		if (!this.field_4616.isEmpty()) {
			this.field_4618 = this.field_4614.field_1773.method_19418();
			this.field_4621 = this.field_4618.method_19326().field_1352;
			this.field_4620 = this.field_4618.method_19326().field_1351;
			this.field_4619 = this.field_4618.method_19326().field_1350;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
			GlStateManager.disableTexture();
			GlStateManager.lineWidth(6.0F);
			this.method_19698();
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	private void method_19698() {
		long l = class_156.method_658();

		for (Integer integer : this.field_4616.keySet()) {
			class_11 lv = (class_11)this.field_4616.get(integer);
			float f = (Float)this.field_4617.get(integer);
			this.method_3868(lv);
			class_9 lv2 = lv.method_48();
			if (!(this.method_3867(lv2) > 40.0F)) {
				class_863.method_19695(
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
						float g = i == lv.method_39() ? 1.0F : 0.0F;
						float h = i == lv.method_39() ? 0.0F : 1.0F;
						class_863.method_19695(
							new class_238(
									(double)((float)lv3.field_40 + 0.5F - f),
									(double)((float)lv3.field_39 + 0.01F * (float)i),
									(double)((float)lv3.field_38 + 0.5F - f),
									(double)((float)lv3.field_40 + 0.5F + f),
									(double)((float)lv3.field_39 + 0.25F + 0.01F * (float)i),
									(double)((float)lv3.field_38 + 0.5F + f)
								)
								.method_989(-this.field_4621, -this.field_4620, -this.field_4619),
							g,
							0.0F,
							h,
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
					class_863.method_3714(String.format("%s", lv3.field_41), (double)lv3.field_40 + 0.5, (double)lv3.field_39 + 0.75, (double)lv3.field_38 + 0.5, -65536);
					class_863.method_3714(
						String.format(Locale.ROOT, "%.2f", lv3.field_43), (double)lv3.field_40 + 0.5, (double)lv3.field_39 + 0.25, (double)lv3.field_38 + 0.5, -65536
					);
				}
			}

			for (class_9 lv3x : lv.method_43()) {
				if (!(this.method_3867(lv3x) > 40.0F)) {
					class_863.method_3714(
						String.format("%s", lv3x.field_41), (double)lv3x.field_40 + 0.5, (double)lv3x.field_39 + 0.75, (double)lv3x.field_38 + 0.5, -16776961
					);
					class_863.method_3714(
						String.format(Locale.ROOT, "%.2f", lv3x.field_43), (double)lv3x.field_40 + 0.5, (double)lv3x.field_39 + 0.25, (double)lv3x.field_38 + 0.5, -16776961
					);
				}
			}

			for (int j = 0; j < lv.method_38(); j++) {
				class_9 lv2 = lv.method_40(j);
				if (!(this.method_3867(lv2) > 40.0F)) {
					class_863.method_3714(String.format("%s", lv2.field_41), (double)lv2.field_40 + 0.5, (double)lv2.field_39 + 0.75, (double)lv2.field_38 + 0.5, -1);
					class_863.method_3714(
						String.format(Locale.ROOT, "%.2f", lv2.field_43), (double)lv2.field_40 + 0.5, (double)lv2.field_39 + 0.25, (double)lv2.field_38 + 0.5, -1
					);
				}
			}
		}

		for (Integer integer2 : (Integer[])this.field_4615.keySet().toArray(new Integer[0])) {
			if (l - (Long)this.field_4615.get(integer2) > 20000L) {
				this.field_4616.remove(integer2);
				this.field_4615.remove(integer2);
			}
		}
	}

	public void method_3868(class_11 arg) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(3, class_290.field_1576);

		for (int i = 0; i < arg.method_38(); i++) {
			class_9 lv3 = arg.method_40(i);
			if (!(this.method_3867(lv3) > 40.0F)) {
				float f = (float)i / (float)arg.method_38() * 0.33F;
				int j = i == 0 ? 0 : class_3532.method_15369(f, 0.9F, 0.9F);
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
			Math.abs((double)arg.field_40 - this.field_4618.method_19326().field_1352)
				+ Math.abs((double)arg.field_39 - this.field_4618.method_19326().field_1351)
				+ Math.abs((double)arg.field_38 - this.field_4618.method_19326().field_1350)
		);
	}
}
