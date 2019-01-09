package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_520 extends class_350.class_351<class_520> {
	private static final class_2960 field_3160 = new class_2960("textures/gui/resource_packs.png");
	private static final class_2561 field_3162 = new class_2588("resourcePack.incompatible");
	private static final class_2561 field_3163 = new class_2588("resourcePack.incompatible.confirm.title");
	protected final class_310 field_3165;
	protected final class_519 field_3164;
	private final class_1075 field_3161;

	public class_520(class_519 arg, class_1075 arg2) {
		this.field_3164 = arg;
		this.field_3165 = class_310.method_1551();
		this.field_3161 = arg2;
	}

	public void method_2686(class_523 arg) {
		this.method_2681().method_14466().method_14468(arg.method_1968(), this, class_520::method_2681, true);
	}

	protected void method_2684() {
		this.field_3161.method_4664(this.field_3165.method_1531());
	}

	protected class_3281 method_2677() {
		return this.field_3161.method_14460();
	}

	protected String method_2679() {
		return this.field_3161.method_14459().method_10863();
	}

	protected String method_2678() {
		return this.field_3161.method_14457().method_10863();
	}

	public class_1075 method_2681() {
		return this.field_3161;
	}

	@Override
	public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1906();
		int n = this.method_1907();
		class_3281 lv = this.method_2677();
		if (!lv.method_14437()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			class_332.method_1785(n - 1, m - 1, n + i - 9, m + j + 1, -8978432);
		}

		this.method_2684();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_332.method_1781(n, m, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String string = this.method_2678();
		String string2 = this.method_2679();
		if (this.method_2687() && (this.field_3165.field_1690.field_1854 || bl)) {
			this.field_3165.method_1531().method_4618(field_3160);
			class_332.method_1785(n, m, n + 32, m + 32, -1601138544);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int o = k - n;
			int p = l - m;
			if (!lv.method_14437()) {
				string = field_3162.method_10863();
				string2 = lv.method_14439().method_10863();
			}

			if (this.method_2688()) {
				if (o < 32) {
					class_332.method_1781(n, m, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					class_332.method_1781(n, m, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			} else {
				if (this.method_2685()) {
					if (o < 16) {
						class_332.method_1781(n, m, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						class_332.method_1781(n, m, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.method_2682()) {
					if (o < 32 && o > 16 && p < 16) {
						class_332.method_1781(n, m, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						class_332.method_1781(n, m, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}

				if (this.method_2683()) {
					if (o < 32 && o > 16 && p > 16) {
						class_332.method_1781(n, m, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
					} else {
						class_332.method_1781(n, m, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
					}
				}
			}
		}

		int ox = this.field_3165.field_1772.method_1727(string);
		if (ox > 157) {
			string = this.field_3165.field_1772.method_1714(string, 157 - this.field_3165.field_1772.method_1727("...")) + "...";
		}

		this.field_3165.field_1772.method_1720(string, (float)(n + 32 + 2), (float)(m + 1), 16777215);
		List<String> list = this.field_3165.field_1772.method_1728(string2, 157);

		for (int q = 0; q < 2 && q < list.size(); q++) {
			this.field_3165.field_1772.method_1720((String)list.get(q), (float)(n + 32 + 2), (float)(m + 12 + 10 * q), 8421504);
		}
	}

	protected boolean method_2687() {
		return !this.field_3161.method_14465() || !this.field_3161.method_14464();
	}

	protected boolean method_2688() {
		return !this.field_3164.method_2669(this);
	}

	protected boolean method_2685() {
		return this.field_3164.method_2669(this) && !this.field_3161.method_14464();
	}

	protected boolean method_2682() {
		List<class_520> list = this.method_1905().method_1968();
		int i = list.indexOf(this);
		return i > 0 && !((class_520)list.get(i - 1)).field_3161.method_14465();
	}

	protected boolean method_2683() {
		List<class_520> list = this.method_1905().method_1968();
		int i = list.indexOf(this);
		return i >= 0 && i < list.size() - 1 && !((class_520)list.get(i + 1)).field_3161.method_14465();
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		double f = d - (double)this.method_1907();
		double g = e - (double)this.method_1906();
		if (this.method_2687() && f <= 32.0) {
			if (this.method_2688()) {
				this.method_2680().method_2660();
				class_3281 lv = this.method_2677();
				if (lv.method_14437()) {
					this.method_2680().method_2674(this);
				} else {
					String string = field_3163.method_10863();
					String string2 = lv.method_14438().method_10863();
					this.field_3165.method_1507(new class_410((bl, ix) -> {
						this.field_3165.method_1507(this.method_2680());
						if (bl) {
							this.method_2680().method_2674(this);
						}
					}, string, string2, 0));
				}

				return true;
			}

			if (f < 16.0 && this.method_2685()) {
				this.method_2680().method_2663(this);
				return true;
			}

			if (f > 16.0 && g < 16.0 && this.method_2682()) {
				List<class_520> list = this.method_1905().method_1968();
				int j = list.indexOf(this);
				list.remove(this);
				list.add(j - 1, this);
				this.method_2680().method_2660();
				return true;
			}

			if (f > 16.0 && g > 16.0 && this.method_2683()) {
				List<class_520> list = this.method_1905().method_1968();
				int j = list.indexOf(this);
				list.remove(this);
				list.add(j + 1, this);
				this.method_2680().method_2660();
				return true;
			}
		}

		return false;
	}

	public class_519 method_2680() {
		return this.field_3164;
	}
}
