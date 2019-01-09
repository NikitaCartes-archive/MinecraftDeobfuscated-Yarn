package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_507 extends class_332 implements class_364, class_515, class_2952<class_1856> {
	protected static final class_2960 field_3097 = new class_2960("textures/gui/recipe_book.png");
	private int field_3102;
	private int field_3101;
	private int field_3100;
	protected final class_505 field_3092 = new class_505();
	private final List<class_512> field_3094 = Lists.<class_512>newArrayList();
	private class_512 field_3098;
	protected class_361 field_3088;
	protected class_1729<?> field_3095;
	protected class_310 field_3091;
	private class_342 field_3089;
	private String field_3099 = "";
	protected class_299 field_3096;
	protected final class_513 field_3086 = new class_513();
	protected final class_1662 field_3090 = new class_1662();
	private int field_3093;
	private boolean field_3087;

	public void method_2597(int i, int j, class_310 arg, boolean bl, class_1729<?> arg2) {
		this.field_3091 = arg;
		this.field_3101 = i;
		this.field_3100 = j;
		this.field_3095 = arg2;
		arg.field_1724.field_7512 = arg2;
		this.field_3096 = arg.field_1724.method_3130();
		this.field_3093 = arg.field_1724.field_7514.method_7364();
		if (this.method_2605()) {
			this.method_2579(bl);
		}

		arg.field_1774.method_1462(true);
	}

	public void method_2579(boolean bl) {
		this.field_3102 = bl ? 0 : 86;
		int i = (this.field_3101 - 147) / 2 - this.field_3102;
		int j = (this.field_3100 - 166) / 2;
		this.field_3090.method_7409();
		this.field_3091.field_1724.field_7514.method_7387(this.field_3090);
		this.field_3095.method_7654(this.field_3090);
		String string = this.field_3089 != null ? this.field_3089.method_1882() : "";
		this.field_3089 = new class_342(0, this.field_3091.field_1772, i + 25, j + 14, 80, 9 + 5);
		this.field_3089.method_1880(50);
		this.field_3089.method_1858(false);
		this.field_3089.method_1862(true);
		this.field_3089.method_1868(16777215);
		this.field_3089.method_1852(string);
		this.field_3086.method_2636(this.field_3091, i, j);
		this.field_3086.method_2630(this);
		this.field_3088 = new class_361(0, i + 110, j + 12, 26, 16, this.field_3096.method_14880(this.field_3095));
		this.method_2585();
		this.field_3094.clear();

		for (class_314 lv : class_299.method_1395(this.field_3095)) {
			this.field_3094.add(new class_512(0, lv));
		}

		if (this.field_3098 != null) {
			this.field_3098 = (class_512)this.field_3094.stream().filter(arg -> arg.method_2623().equals(this.field_3098.method_2623())).findFirst().orElse(null);
		}

		if (this.field_3098 == null) {
			this.field_3098 = (class_512)this.field_3094.get(0);
		}

		this.field_3098.method_1964(true);
		this.method_2603(false);
		this.method_2606();
	}

	protected void method_2585() {
		this.field_3088.method_1962(152, 41, 28, 18, field_3097);
	}

	public void method_2607() {
		this.field_3089 = null;
		this.field_3098 = null;
		this.field_3091.field_1774.method_1462(false);
	}

	public int method_2595(boolean bl, int i, int j) {
		int k;
		if (this.method_2605() && !bl) {
			k = 177 + (i - j - 200) / 2;
		} else {
			k = (i - j) / 2;
		}

		return k;
	}

	public void method_2591() {
		this.method_2593(!this.method_2605());
	}

	public boolean method_2605() {
		return this.field_3096.method_14887();
	}

	protected void method_2593(boolean bl) {
		this.field_3096.method_14884(bl);
		if (!bl) {
			this.field_3086.method_2638();
		}

		this.method_2588();
	}

	public void method_2600(@Nullable class_1735 arg) {
		if (arg != null && arg.field_7874 < this.field_3095.method_7658()) {
			this.field_3092.method_2571();
			if (this.method_2605()) {
				this.method_2587();
			}
		}
	}

	private void method_2603(boolean bl) {
		List<class_516> list = this.field_3096.method_1396(this.field_3098.method_2623());
		list.forEach(arg -> arg.method_2649(this.field_3090, this.field_3095.method_7653(), this.field_3095.method_7656(), this.field_3096));
		List<class_516> list2 = Lists.<class_516>newArrayList(list);
		list2.removeIf(arg -> !arg.method_2652());
		list2.removeIf(arg -> !arg.method_2657());
		String string = this.field_3089.method_1882();
		if (!string.isEmpty()) {
			ObjectSet<class_516> objectSet = new ObjectLinkedOpenHashSet<>(
				this.field_3091.method_1484(class_1124.field_5496).method_4810(string.toLowerCase(Locale.ROOT))
			);
			list2.removeIf(arg -> !objectSet.contains(arg));
		}

		if (this.field_3096.method_14880(this.field_3095)) {
			list2.removeIf(arg -> !arg.method_2655());
		}

		this.field_3086.method_2627(list2, bl);
	}

	private void method_2606() {
		int i = (this.field_3101 - 147) / 2 - this.field_3102 - 30;
		int j = (this.field_3100 - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (class_512 lv : this.field_3094) {
			class_314 lv2 = lv.method_2623();
			if (lv2 == class_314.field_1809 || lv2 == class_314.field_1804) {
				lv.field_2076 = true;
				lv.method_1963(i, j + 27 * l++);
			} else if (lv.method_2624(this.field_3096)) {
				lv.method_1963(i, j + 27 * l++);
				lv.method_2622(this.field_3091);
			}
		}
	}

	public void method_2590() {
		if (this.method_2605()) {
			if (this.field_3093 != this.field_3091.field_1724.field_7514.method_7364()) {
				this.method_2587();
				this.field_3093 = this.field_3091.field_1724.field_7514.method_7364();
			}
		}
	}

	private void method_2587() {
		this.field_3090.method_7409();
		this.field_3091.field_1724.field_7514.method_7387(this.field_3090);
		this.field_3095.method_7654(this.field_3090);
		this.method_2603(false);
	}

	public void method_2578(int i, int j, float f) {
		if (this.method_2605()) {
			class_308.method_1453();
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 100.0F);
			this.field_3091.method_1531().method_4618(field_3097);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = (this.field_3101 - 147) / 2 - this.field_3102;
			int l = (this.field_3100 - 166) / 2;
			this.method_1788(k, l, 1, 1, 147, 166);
			this.field_3089.method_1857(i, j, f);
			class_308.method_1450();

			for (class_512 lv : this.field_3094) {
				lv.method_1824(i, j, f);
			}

			this.field_3088.method_1824(i, j, f);
			this.field_3086.method_2634(k, l, i, j, f);
			GlStateManager.popMatrix();
		}
	}

	public void method_2601(int i, int j, int k, int l) {
		if (this.method_2605()) {
			this.field_3086.method_2628(k, l);
			if (this.field_3088.method_1828()) {
				String string = this.method_2599();
				if (this.field_3091.field_1755 != null) {
					this.field_3091.field_1755.method_2215(string, k, l);
				}
			}

			this.method_2602(i, j, k, l);
		}
	}

	protected String method_2599() {
		return class_1074.method_4662(this.field_3088.method_1965() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
	}

	private void method_2602(int i, int j, int k, int l) {
		class_1799 lv = null;

		for (int m = 0; m < this.field_3092.method_2572(); m++) {
			class_505.class_506 lv2 = this.field_3092.method_2570(m);
			int n = lv2.method_2574() + i;
			int o = lv2.method_2575() + j;
			if (k >= n && l >= o && k < n + 16 && l < o + 16) {
				lv = lv2.method_2573();
			}
		}

		if (lv != null && this.field_3091.field_1755 != null) {
			this.field_3091.field_1755.method_2211(this.field_3091.field_1755.method_2239(lv), k, l);
		}
	}

	public void method_2581(int i, int j, boolean bl, float f) {
		this.field_3092.method_2567(this.field_3091, i, j, bl, f);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (this.method_2605() && !this.field_3091.field_1724.method_7325()) {
			if (this.field_3086.method_2632(d, e, i, (this.field_3101 - 147) / 2 - this.field_3102, (this.field_3100 - 166) / 2, 147, 166)) {
				class_1860 lv = this.field_3086.method_2631();
				class_516 lv2 = this.field_3086.method_2635();
				if (lv != null && lv2 != null) {
					if (!lv2.method_2653(lv) && this.field_3092.method_2566() == lv) {
						return false;
					}

					this.field_3092.method_2571();
					this.field_3091.field_1761.method_2912(this.field_3091.field_1724.field_7512.field_7763, lv, class_437.method_2223());
					if (!this.method_2604()) {
						this.method_2593(false);
					}
				}

				return true;
			} else if (this.field_3089.method_16807(d, e, i)) {
				return true;
			} else if (this.field_3088.method_16807(d, e, i)) {
				boolean bl = this.method_2589();
				this.field_3088.method_1964(bl);
				this.method_2588();
				this.method_2603(false);
				return true;
			} else {
				for (class_512 lv3 : this.field_3094) {
					if (lv3.method_16807(d, e, i)) {
						if (this.field_3098 != lv3) {
							this.field_3098.method_1964(false);
							this.field_3098 = lv3;
							this.field_3098.method_1964(true);
							this.method_2603(true);
						}

						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	protected boolean method_2589() {
		boolean bl = !this.field_3096.method_14890();
		this.field_3096.method_14889(bl);
		return bl;
	}

	public boolean method_2598(double d, double e, int i, int j, int k, int l, int m) {
		if (!this.method_2605()) {
			return true;
		} else {
			boolean bl = d < (double)i || e < (double)j || d >= (double)(i + k) || e >= (double)(j + l);
			boolean bl2 = (double)(i - 147) < d && d < (double)i && (double)j < e && e < (double)(j + l);
			return bl && !bl2 && !this.field_3098.method_1828();
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		this.field_3087 = false;
		if (!this.method_2605() || this.field_3091.field_1724.method_7325()) {
			return false;
		} else if (i == 256 && !this.method_2604()) {
			this.method_2593(false);
			return true;
		} else if (this.field_3089.method_16805(i, j, k)) {
			this.method_2586();
			return true;
		} else if (this.field_3091.field_1690.field_1890.method_1417(i, j) && !this.field_3089.method_1871()) {
			this.field_3087 = true;
			this.field_3089.method_1876(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_16803(int i, int j, int k) {
		this.field_3087 = false;
		return class_364.super.method_16803(i, j, k);
	}

	@Override
	public boolean method_16806(char c, int i) {
		if (this.field_3087) {
			return false;
		} else if (!this.method_2605() || this.field_3091.field_1724.method_7325()) {
			return false;
		} else if (this.field_3089.method_16806(c, i)) {
			this.method_2586();
			return true;
		} else {
			return class_364.super.method_16806(c, i);
		}
	}

	private void method_2586() {
		String string = this.field_3089.method_1882().toLowerCase(Locale.ROOT);
		this.method_2576(string);
		if (!string.equals(this.field_3099)) {
			this.method_2603(false);
			this.field_3099 = string;
		}
	}

	private void method_2576(String string) {
		if ("excitedze".equals(string)) {
			class_1076 lv = this.field_3091.method_1526();
			class_1077 lv2 = lv.method_4668("en_pt");
			if (lv.method_4669().method_4673(lv2) == 0) {
				return;
			}

			lv.method_4667(lv2);
			this.field_3091.field_1690.field_1883 = lv2.getCode();
			this.field_3091.method_1521();
			this.field_3091.field_1772.method_1719(lv.method_4666());
			this.field_3091.field_1690.method_1640();
		}
	}

	private boolean method_2604() {
		return this.field_3102 == 86;
	}

	public void method_2592() {
		this.method_2606();
		if (this.method_2605()) {
			this.method_2603(false);
		}
	}

	@Override
	public void method_2646(List<class_1860> list) {
		for (class_1860 lv : list) {
			this.field_3091.field_1724.method_3141(lv);
		}
	}

	public void method_2596(class_1860 arg, List<class_1735> list) {
		class_1799 lv = arg.method_8110();
		this.field_3092.method_2565(arg);
		this.field_3092.method_2569(class_1856.method_8101(lv), ((class_1735)list.get(0)).field_7873, ((class_1735)list.get(0)).field_7872);
		this.method_12816(this.field_3095.method_7653(), this.field_3095.method_7656(), this.field_3095.method_7655(), arg, arg.method_8117().iterator(), 0);
	}

	@Override
	public void method_12815(Iterator<class_1856> iterator, int i, int j, int k, int l) {
		class_1856 lv = (class_1856)iterator.next();
		if (!lv.method_8103()) {
			class_1735 lv2 = (class_1735)this.field_3095.field_7761.get(i);
			this.field_3092.method_2569(lv, lv2.field_7873, lv2.field_7872);
		}
	}

	protected void method_2588() {
		if (this.field_3091.method_1562() != null) {
			this.field_3091
				.method_1562()
				.method_2883(
					new class_2853(
						this.field_3096.method_14887(),
						this.field_3096.method_14890(),
						this.field_3096.method_14891(),
						this.field_3096.method_14892(),
						this.field_3096.method_17317(),
						this.field_3096.method_17319()
					)
				);
		}
	}
}
