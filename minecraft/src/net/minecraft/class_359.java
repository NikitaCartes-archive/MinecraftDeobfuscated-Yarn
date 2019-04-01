package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_359 extends class_332 implements class_1145 {
	private final class_310 field_2182;
	private final List<class_359.class_360> field_2183 = Lists.<class_359.class_360>newArrayList();
	private boolean field_2184;

	public class_359(class_310 arg) {
		this.field_2182 = arg;
	}

	public void method_1957() {
		if (!this.field_2184 && this.field_2182.field_1690.field_1818) {
			this.field_2182.method_1483().method_4878(this);
			this.field_2184 = true;
		} else if (this.field_2184 && !this.field_2182.field_1690.field_1818) {
			this.field_2182.method_1483().method_4866(this);
			this.field_2184 = false;
		}

		if (this.field_2184 && !this.field_2183.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			class_243 lv = new class_243(
				this.field_2182.field_1724.field_5987,
				this.field_2182.field_1724.field_6010 + (double)this.field_2182.field_1724.method_5751(),
				this.field_2182.field_1724.field_6035
			);
			class_243 lv2 = new class_243(0.0, 0.0, -1.0)
				.method_1037(-this.field_2182.field_1724.field_5965 * (float) (Math.PI / 180.0))
				.method_1024(-this.field_2182.field_1724.field_6031 * (float) (Math.PI / 180.0));
			class_243 lv3 = new class_243(0.0, 1.0, 0.0)
				.method_1037(-this.field_2182.field_1724.field_5965 * (float) (Math.PI / 180.0))
				.method_1024(-this.field_2182.field_1724.field_6031 * (float) (Math.PI / 180.0));
			class_243 lv4 = lv2.method_1036(lv3);
			int i = 0;
			int j = 0;
			Iterator<class_359.class_360> iterator = this.field_2183.iterator();

			while (iterator.hasNext()) {
				class_359.class_360 lv5 = (class_359.class_360)iterator.next();
				if (lv5.method_1961() + 3000L <= class_156.method_658()) {
					iterator.remove();
				} else {
					j = Math.max(j, this.field_2182.field_1772.method_1727(lv5.method_1960()));
				}
			}

			j += this.field_2182.field_1772.method_1727("<")
				+ this.field_2182.field_1772.method_1727(" ")
				+ this.field_2182.field_1772.method_1727(">")
				+ this.field_2182.field_1772.method_1727(" ");

			for (class_359.class_360 lv5 : this.field_2183) {
				int k = 255;
				String string = lv5.method_1960();
				class_243 lv6 = lv5.method_1959().method_1020(lv).method_1029();
				double d = -lv4.method_1026(lv6);
				double e = -lv2.method_1026(lv6);
				boolean bl = e > 0.5;
				int l = j / 2;
				int m = 9;
				int n = m / 2;
				float f = 1.0F;
				int o = this.field_2182.field_1772.method_1727(string);
				int p = class_3532.method_15357(class_3532.method_15390(255.0, 75.0, (double)((float)(class_156.method_658() - lv5.method_1961()) / 3000.0F)));
				int q = p << 16 | p << 8 | p;
				GlStateManager.pushMatrix();
				GlStateManager.translatef(
					(float)this.field_2182.field_1704.method_4486() - (float)l * 1.0F - 2.0F,
					(float)(this.field_2182.field_1704.method_4502() - 30) - (float)(i * (m + 1)) * 1.0F,
					0.0F
				);
				GlStateManager.scalef(1.0F, 1.0F, 1.0F);
				fill(-l - 1, -n - 1, l + 1, n + 1, this.field_2182.field_1690.method_19345(0.8F));
				GlStateManager.enableBlend();
				if (!bl) {
					if (d > 0.0) {
						this.field_2182.field_1772.method_1729(">", (float)(l - this.field_2182.field_1772.method_1727(">")), (float)(-n), q + -16777216);
					} else if (d < 0.0) {
						this.field_2182.field_1772.method_1729("<", (float)(-l), (float)(-n), q + -16777216);
					}
				}

				this.field_2182.field_1772.method_1729(string, (float)(-o / 2), (float)(-n), q + -16777216);
				GlStateManager.popMatrix();
				i++;
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void method_4884(class_1113 arg, class_1146 arg2) {
		if (arg2.method_4886() != null) {
			String string = arg2.method_4886().method_10863();
			if (!this.field_2183.isEmpty()) {
				for (class_359.class_360 lv : this.field_2183) {
					if (lv.method_1960().equals(string)) {
						lv.method_1958(new class_243((double)arg.method_4784(), (double)arg.method_4779(), (double)arg.method_4778()));
						return;
					}
				}
			}

			this.field_2183.add(new class_359.class_360(string, new class_243((double)arg.method_4784(), (double)arg.method_4779(), (double)arg.method_4778())));
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_360 {
		private final String field_2188;
		private long field_2185;
		private class_243 field_2186;

		public class_360(String string, class_243 arg2) {
			this.field_2188 = string;
			this.field_2186 = arg2;
			this.field_2185 = class_156.method_658();
		}

		public String method_1960() {
			return this.field_2188;
		}

		public long method_1961() {
			return this.field_2185;
		}

		public class_243 method_1959() {
			return this.field_2186;
		}

		public void method_1958(class_243 arg) {
			this.field_2186 = arg;
			this.field_2185 = class_156.method_658();
		}
	}
}
