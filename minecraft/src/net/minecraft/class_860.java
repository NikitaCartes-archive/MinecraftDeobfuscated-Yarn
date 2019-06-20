package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_860 implements class_863.class_864 {
	private final class_310 field_4509;
	private double field_4510 = Double.MIN_VALUE;
	private final int field_4511 = 12;
	@Nullable
	private class_860.class_861 field_4512;

	public class_860(class_310 arg) {
		this.field_4509 = arg;
	}

	@Override
	public void method_3715(long l) {
		double d = (double)class_156.method_648();
		if (d - this.field_4510 > 3.0E9) {
			this.field_4510 = d;
			class_1132 lv = this.field_4509.method_1576();
			if (lv != null) {
				this.field_4512 = new class_860.class_861(lv);
			} else {
				this.field_4512 = null;
			}
		}

		if (this.field_4512 != null) {
			GlStateManager.disableFog();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ONE, GlStateManager.class_5118.ZERO
			);
			GlStateManager.lineWidth(2.0F);
			GlStateManager.disableTexture();
			GlStateManager.depthMask(false);
			Map<class_1923, String> map = (Map<class_1923, String>)this.field_4512.field_4514.getNow(null);
			double e = this.field_4509.field_1773.method_19418().method_19326().field_1351 * 0.85;

			for (Entry<class_1923, String> entry : this.field_4512.field_4515.entrySet()) {
				class_1923 lv2 = (class_1923)entry.getKey();
				String string = (String)entry.getValue();
				if (map != null) {
					string = string + (String)map.get(lv2);
				}

				String[] strings = string.split("\n");
				int i = 0;

				for (String string2 : strings) {
					class_863.method_19429(string2, (double)((lv2.field_9181 << 4) + 8), e + (double)i, (double)((lv2.field_9180 << 4) + 8), -1, 0.15F);
					i -= 2;
				}
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.enableFog();
		}
	}

	@Environment(EnvType.CLIENT)
	final class class_861 {
		private final Map<class_1923, String> field_4515;
		private final CompletableFuture<Map<class_1923, String>> field_4514;

		private class_861(class_1132 arg2) {
			class_638 lv = class_860.this.field_4509.field_1687;
			class_2874 lv2 = class_860.this.field_4509.field_1687.field_9247.method_12460();
			class_3218 lv3;
			if (arg2.method_3847(lv2) != null) {
				lv3 = arg2.method_3847(lv2);
			} else {
				lv3 = null;
			}

			class_4184 lv4 = class_860.this.field_4509.field_1773.method_19418();
			int i = (int)lv4.method_19326().field_1352 >> 4;
			int j = (int)lv4.method_19326().field_1350 >> 4;
			Builder<class_1923, String> builder = ImmutableMap.builder();
			class_631 lv5 = lv.method_2935();

			for (int k = i - 12; k <= i + 12; k++) {
				for (int l = j - 12; l <= j + 12; l++) {
					class_1923 lv6 = new class_1923(k, l);
					String string = "";
					class_2818 lv7 = lv5.method_12126(k, l, false);
					string = string + "Client: ";
					if (lv7 == null) {
						string = string + "0n/a\n";
					} else {
						string = string + (lv7.method_12223() ? " E" : "");
						string = string + "\n";
					}

					builder.put(lv6, string);
				}
			}

			this.field_4515 = builder.build();
			this.field_4514 = arg2.method_5385(() -> {
				Builder<class_1923, String> builderx = ImmutableMap.builder();
				class_3215 lvx = lv3.method_14178();

				for (int kx = i - 12; kx <= i + 12; kx++) {
					for (int lx = j - 12; lx <= j + 12; lx++) {
						class_1923 lv2x = new class_1923(kx, lx);
						builderx.put(lv2x, "Server: " + lvx.method_17294(lv2x));
					}
				}

				return builderx.build();
			});
		}
	}
}
