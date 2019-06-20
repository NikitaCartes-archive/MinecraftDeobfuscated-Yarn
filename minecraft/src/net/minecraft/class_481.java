package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_481 extends class_485<class_481.class_483> {
	private static final class_2960 field_2893 = new class_2960("textures/gui/container/creative_inventory/tabs.png");
	private static final class_1277 field_2895 = new class_1277(45);
	private static int field_2896 = class_1761.field_7931.method_7741();
	private float field_2890;
	private boolean field_2892;
	private class_342 field_2894;
	private List<class_1735> field_2886;
	private class_1735 field_2889;
	private class_478 field_2891;
	private boolean field_2888;
	private boolean field_2887;
	private final Map<class_2960, class_3494<class_1792>> field_16201 = Maps.<class_2960, class_3494<class_1792>>newTreeMap();

	public class_481(class_1657 arg) {
		super(new class_481.class_483(arg), arg.field_7514, new class_2585(""));
		arg.field_7512 = this.field_2797;
		this.passEvents = true;
		this.field_2779 = 136;
		this.field_2792 = 195;
	}

	@Override
	public void tick() {
		if (!this.minecraft.field_1761.method_2914()) {
			this.minecraft.method_1507(new class_490(this.minecraft.field_1724));
		} else if (this.field_2894 != null) {
			this.field_2894.method_1865();
		}
	}

	@Override
	protected void method_2383(@Nullable class_1735 arg, int i, int j, class_1713 arg2) {
		if (this.method_2470(arg)) {
			this.field_2894.method_1872();
			this.field_2894.method_1884(0);
		}

		boolean bl = arg2 == class_1713.field_7794;
		arg2 = i == -999 && arg2 == class_1713.field_7790 ? class_1713.field_7795 : arg2;
		if (arg == null && field_2896 != class_1761.field_7918.method_7741() && arg2 != class_1713.field_7789) {
			class_1661 lv3 = this.minecraft.field_1724.field_7514;
			if (!lv3.method_7399().method_7960() && this.field_2887) {
				if (j == 0) {
					this.minecraft.field_1724.method_7328(lv3.method_7399(), true);
					this.minecraft.field_1761.method_2915(lv3.method_7399());
					lv3.method_7396(class_1799.field_8037);
				}

				if (j == 1) {
					class_1799 lv2 = lv3.method_7399().method_7971(1);
					this.minecraft.field_1724.method_7328(lv2, true);
					this.minecraft.field_1761.method_2915(lv2);
				}
			}
		} else {
			if (arg != null && !arg.method_7674(this.minecraft.field_1724)) {
				return;
			}

			if (arg == this.field_2889 && bl) {
				for (int k = 0; k < this.minecraft.field_1724.field_7498.method_7602().size(); k++) {
					this.minecraft.field_1761.method_2909(class_1799.field_8037, k);
				}
			} else if (field_2896 == class_1761.field_7918.method_7741()) {
				if (arg == this.field_2889) {
					this.minecraft.field_1724.field_7514.method_7396(class_1799.field_8037);
				} else if (arg2 == class_1713.field_7795 && arg != null && arg.method_7681()) {
					class_1799 lv = arg.method_7671(j == 0 ? 1 : arg.method_7677().method_7914());
					class_1799 lv2 = arg.method_7677();
					this.minecraft.field_1724.method_7328(lv, true);
					this.minecraft.field_1761.method_2915(lv);
					this.minecraft.field_1761.method_2909(lv2, ((class_481.class_484)arg).field_2898.field_7874);
				} else if (arg2 == class_1713.field_7795 && !this.minecraft.field_1724.field_7514.method_7399().method_7960()) {
					this.minecraft.field_1724.method_7328(this.minecraft.field_1724.field_7514.method_7399(), true);
					this.minecraft.field_1761.method_2915(this.minecraft.field_1724.field_7514.method_7399());
					this.minecraft.field_1724.field_7514.method_7396(class_1799.field_8037);
				} else {
					this.minecraft.field_1724.field_7498.method_7593(arg == null ? i : ((class_481.class_484)arg).field_2898.field_7874, j, arg2, this.minecraft.field_1724);
					this.minecraft.field_1724.field_7498.method_7623();
				}
			} else if (arg2 != class_1713.field_7789 && arg.field_7871 == field_2895) {
				class_1661 lv3 = this.minecraft.field_1724.field_7514;
				class_1799 lv2 = lv3.method_7399();
				class_1799 lv4 = arg.method_7677();
				if (arg2 == class_1713.field_7791) {
					if (!lv4.method_7960() && j >= 0 && j < 9) {
						class_1799 lv5 = lv4.method_7972();
						lv5.method_7939(lv5.method_7914());
						this.minecraft.field_1724.field_7514.method_5447(j, lv5);
						this.minecraft.field_1724.field_7498.method_7623();
					}

					return;
				}

				if (arg2 == class_1713.field_7796) {
					if (lv3.method_7399().method_7960() && arg.method_7681()) {
						class_1799 lv5 = arg.method_7677().method_7972();
						lv5.method_7939(lv5.method_7914());
						lv3.method_7396(lv5);
					}

					return;
				}

				if (arg2 == class_1713.field_7795) {
					if (!lv4.method_7960()) {
						class_1799 lv5 = lv4.method_7972();
						lv5.method_7939(j == 0 ? 1 : lv5.method_7914());
						this.minecraft.field_1724.method_7328(lv5, true);
						this.minecraft.field_1761.method_2915(lv5);
					}

					return;
				}

				if (!lv2.method_7960() && !lv4.method_7960() && lv2.method_7962(lv4) && class_1799.method_7975(lv2, lv4)) {
					if (j == 0) {
						if (bl) {
							lv2.method_7939(lv2.method_7914());
						} else if (lv2.method_7947() < lv2.method_7914()) {
							lv2.method_7933(1);
						}
					} else {
						lv2.method_7934(1);
					}
				} else if (!lv4.method_7960() && lv2.method_7960()) {
					lv3.method_7396(lv4.method_7972());
					lv2 = lv3.method_7399();
					if (bl) {
						lv2.method_7939(lv2.method_7914());
					}
				} else if (j == 0) {
					lv3.method_7396(class_1799.field_8037);
				} else {
					lv3.method_7399().method_7934(1);
				}
			} else if (this.field_2797 != null) {
				class_1799 lv = arg == null ? class_1799.field_8037 : this.field_2797.method_7611(arg.field_7874).method_7677();
				this.field_2797.method_7593(arg == null ? i : arg.field_7874, j, arg2, this.minecraft.field_1724);
				if (class_1703.method_7594(j) == 2) {
					for (int l = 0; l < 9; l++) {
						this.minecraft.field_1761.method_2909(this.field_2797.method_7611(45 + l).method_7677(), 36 + l);
					}
				} else if (arg != null) {
					class_1799 lv2x = this.field_2797.method_7611(arg.field_7874).method_7677();
					this.minecraft.field_1761.method_2909(lv2x, arg.field_7874 - this.field_2797.field_7761.size() + 9 + 36);
					int m = 45 + j;
					if (arg2 == class_1713.field_7791) {
						this.minecraft.field_1761.method_2909(lv, m - this.field_2797.field_7761.size() + 9 + 36);
					} else if (arg2 == class_1713.field_7795 && !lv.method_7960()) {
						class_1799 lv5 = lv.method_7972();
						lv5.method_7939(j == 0 ? 1 : lv5.method_7914());
						this.minecraft.field_1724.method_7328(lv5, true);
						this.minecraft.field_1761.method_2915(lv5);
					}

					this.minecraft.field_1724.field_7498.method_7623();
				}
			}
		}
	}

	private boolean method_2470(@Nullable class_1735 arg) {
		return arg != null && arg.field_7871 == field_2895;
	}

	@Override
	protected void method_2476() {
		int i = this.field_2776;
		super.method_2476();
		if (this.field_2894 != null && this.field_2776 != i) {
			this.field_2894.method_16872(this.field_2776 + 82);
		}
	}

	@Override
	protected void init() {
		if (this.minecraft.field_1761.method_2914()) {
			super.init();
			this.minecraft.field_1774.method_1462(true);
			this.field_2894 = new class_342(this.font, this.field_2776 + 82, this.field_2800 + 6, 80, 9, class_1074.method_4662("itemGroup.search"));
			this.field_2894.method_1880(50);
			this.field_2894.method_1858(false);
			this.field_2894.method_1862(false);
			this.field_2894.method_1868(16777215);
			this.children.add(this.field_2894);
			int i = field_2896;
			field_2896 = -1;
			this.method_2466(class_1761.field_7921[i]);
			this.minecraft.field_1724.field_7498.method_7603(this.field_2891);
			this.field_2891 = new class_478(this.minecraft);
			this.minecraft.field_1724.field_7498.method_7596(this.field_2891);
		} else {
			this.minecraft.method_1507(new class_490(this.minecraft.field_1724));
		}
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2894.method_1882();
		this.init(arg, i, j);
		this.field_2894.method_1852(string);
		if (!this.field_2894.method_1882().isEmpty()) {
			this.method_2464();
		}
	}

	@Override
	public void removed() {
		super.removed();
		if (this.minecraft.field_1724 != null && this.minecraft.field_1724.field_7514 != null) {
			this.minecraft.field_1724.field_7498.method_7603(this.field_2891);
		}

		this.minecraft.field_1774.method_1462(false);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_2888) {
			return false;
		} else if (field_2896 != class_1761.field_7915.method_7741()) {
			return false;
		} else {
			String string = this.field_2894.method_1882();
			if (this.field_2894.charTyped(c, i)) {
				if (!Objects.equals(string, this.field_2894.method_1882())) {
					this.method_2464();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		this.field_2888 = false;
		if (field_2896 != class_1761.field_7915.method_7741()) {
			if (this.minecraft.field_1690.field_1890.method_1417(i, j)) {
				this.field_2888 = true;
				this.method_2466(class_1761.field_7915);
				return true;
			} else {
				return super.keyPressed(i, j, k);
			}
		} else {
			boolean bl = !this.method_2470(this.field_2787) || this.field_2787 != null && this.field_2787.method_7681();
			if (bl && this.method_2384(i, j)) {
				this.field_2888 = true;
				return true;
			} else {
				String string = this.field_2894.method_1882();
				if (this.field_2894.keyPressed(i, j, k)) {
					if (!Objects.equals(string, this.field_2894.method_1882())) {
						this.method_2464();
					}

					return true;
				} else {
					return this.field_2894.isFocused() && this.field_2894.method_1885() && i != 256 ? true : super.keyPressed(i, j, k);
				}
			}
		}
	}

	@Override
	public boolean method_16803(int i, int j, int k) {
		this.field_2888 = false;
		return super.method_16803(i, j, k);
	}

	private void method_2464() {
		this.field_2797.field_2897.clear();
		this.field_16201.clear();
		String string = this.field_2894.method_1882();
		if (string.isEmpty()) {
			for (class_1792 lv : class_2378.field_11142) {
				lv.method_7850(class_1761.field_7915, this.field_2797.field_2897);
			}
		} else {
			class_1129<class_1799> lv2;
			if (string.startsWith("#")) {
				string = string.substring(1);
				lv2 = this.minecraft.method_1484(class_1124.field_5494);
				this.method_15871(string);
			} else {
				lv2 = this.minecraft.method_1484(class_1124.field_5495);
			}

			this.field_2797.field_2897.addAll(lv2.method_4810(string.toLowerCase(Locale.ROOT)));
		}

		this.field_2890 = 0.0F;
		this.field_2797.method_2473(0.0F);
	}

	private void method_15871(String string) {
		int i = string.indexOf(58);
		Predicate<class_2960> predicate;
		if (i == -1) {
			predicate = arg -> arg.method_12832().contains(string);
		} else {
			String string2 = string.substring(0, i).trim();
			String string3 = string.substring(i + 1).trim();
			predicate = arg -> arg.method_12836().contains(string2) && arg.method_12832().contains(string3);
		}

		class_3503<class_1792> lv = class_3489.method_15106();
		lv.method_15189().stream().filter(predicate).forEach(arg2 -> {
			class_3494 var10000 = (class_3494)this.field_16201.put(arg2, lv.method_15193(arg2));
		});
	}

	@Override
	protected void method_2388(int i, int j) {
		class_1761 lv = class_1761.field_7921[field_2896];
		if (lv.method_7754()) {
			GlStateManager.disableBlend();
			this.font.method_1729(class_1074.method_4662(lv.method_7737()), 8.0F, 6.0F, 4210752);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			double f = d - (double)this.field_2776;
			double g = e - (double)this.field_2800;

			for (class_1761 lv : class_1761.field_7921) {
				if (this.method_2463(lv, f, g)) {
					return true;
				}
			}

			if (field_2896 != class_1761.field_7918.method_7741() && this.method_2467(d, e)) {
				this.field_2892 = this.method_2465();
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0) {
			double f = d - (double)this.field_2776;
			double g = e - (double)this.field_2800;
			this.field_2892 = false;

			for (class_1761 lv : class_1761.field_7921) {
				if (this.method_2463(lv, f, g)) {
					this.method_2466(lv);
					return true;
				}
			}
		}

		return super.mouseReleased(d, e, i);
	}

	private boolean method_2465() {
		return field_2896 != class_1761.field_7918.method_7741() && class_1761.field_7921[field_2896].method_7756() && this.field_2797.method_2474();
	}

	private void method_2466(class_1761 arg) {
		int i = field_2896;
		field_2896 = arg.method_7741();
		this.field_2793.clear();
		this.field_2797.field_2897.clear();
		if (arg == class_1761.field_7925) {
			class_302 lv = this.minecraft.method_1571();

			for (int j = 0; j < 9; j++) {
				class_748 lv2 = lv.method_1410(j);
				if (lv2.isEmpty()) {
					for (int k = 0; k < 9; k++) {
						if (k == j) {
							class_1799 lv3 = new class_1799(class_1802.field_8407);
							lv3.method_7911("CustomCreativeLock");
							String string = this.minecraft.field_1690.field_1852[j].method_16007();
							String string2 = this.minecraft.field_1690.field_1879.method_16007();
							lv3.method_7977(new class_2588("inventory.hotbarInfo", string2, string));
							this.field_2797.field_2897.add(lv3);
						} else {
							this.field_2797.field_2897.add(class_1799.field_8037);
						}
					}
				} else {
					this.field_2797.field_2897.addAll(lv2);
				}
			}
		} else if (arg != class_1761.field_7915) {
			arg.method_7738(this.field_2797.field_2897);
		}

		if (arg == class_1761.field_7918) {
			class_1703 lv4 = this.minecraft.field_1724.field_7498;
			if (this.field_2886 == null) {
				this.field_2886 = ImmutableList.copyOf(this.field_2797.field_7761);
			}

			this.field_2797.field_7761.clear();

			for (int jx = 0; jx < lv4.field_7761.size(); jx++) {
				class_1735 lv5 = new class_481.class_484((class_1735)lv4.field_7761.get(jx), jx);
				this.field_2797.field_7761.add(lv5);
				if (jx >= 5 && jx < 9) {
					int kx = jx - 5;
					int l = kx / 2;
					int m = kx % 2;
					lv5.field_7873 = 54 + l * 54;
					lv5.field_7872 = 6 + m * 27;
				} else if (jx >= 0 && jx < 5) {
					lv5.field_7873 = -2000;
					lv5.field_7872 = -2000;
				} else if (jx == 45) {
					lv5.field_7873 = 35;
					lv5.field_7872 = 20;
				} else if (jx < lv4.field_7761.size()) {
					int kx = jx - 9;
					int l = kx % 9;
					int m = kx / 9;
					lv5.field_7873 = 9 + l * 18;
					if (jx >= 36) {
						lv5.field_7872 = 112;
					} else {
						lv5.field_7872 = 54 + m * 18;
					}
				}
			}

			this.field_2889 = new class_1735(field_2895, 0, 173, 112);
			this.field_2797.field_7761.add(this.field_2889);
		} else if (i == class_1761.field_7918.method_7741()) {
			this.field_2797.field_7761.clear();
			this.field_2797.field_7761.addAll(this.field_2886);
			this.field_2886 = null;
		}

		if (this.field_2894 != null) {
			if (arg == class_1761.field_7915) {
				this.field_2894.method_1862(true);
				this.field_2894.method_1856(false);
				this.field_2894.method_1876(true);
				if (i != arg.method_7741()) {
					this.field_2894.method_1852("");
				}

				this.method_2464();
			} else {
				this.field_2894.method_1862(false);
				this.field_2894.method_1856(true);
				this.field_2894.method_1876(false);
				this.field_2894.method_1852("");
			}
		}

		this.field_2890 = 0.0F;
		this.field_2797.method_2473(0.0F);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if (!this.method_2465()) {
			return false;
		} else {
			int i = (this.field_2797.field_2897.size() + 9 - 1) / 9 - 5;
			this.field_2890 = (float)((double)this.field_2890 - f / (double)i);
			this.field_2890 = class_3532.method_15363(this.field_2890, 0.0F, 1.0F);
			this.field_2797.method_2473(this.field_2890);
			return true;
		}
	}

	@Override
	protected boolean method_2381(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
		this.field_2887 = bl && !this.method_2463(class_1761.field_7921[field_2896], d, e);
		return this.field_2887;
	}

	protected boolean method_2467(double d, double e) {
		int i = this.field_2776;
		int j = this.field_2800;
		int k = i + 175;
		int l = j + 18;
		int m = k + 14;
		int n = l + 112;
		return d >= (double)k && e >= (double)l && d < (double)m && e < (double)n;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.field_2892) {
			int j = this.field_2800 + 18;
			int k = j + 112;
			this.field_2890 = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.field_2890 = class_3532.method_15363(this.field_2890, 0.0F, 1.0F);
			this.field_2797.method_2473(this.field_2890);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);

		for (class_1761 lv : class_1761.field_7921) {
			if (this.method_2471(lv, i, j)) {
				break;
			}
		}

		if (this.field_2889 != null
			&& field_2896 == class_1761.field_7918.method_7741()
			&& this.method_2378(this.field_2889.field_7873, this.field_2889.field_7872, 16, 16, (double)i, (double)j)) {
			this.renderTooltip(class_1074.method_4662("inventory.binSlot"), i, j);
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		this.method_2380(i, j);
	}

	@Override
	protected void renderTooltip(class_1799 arg, int i, int j) {
		if (field_2896 == class_1761.field_7915.method_7741()) {
			List<class_2561> list = arg.method_7950(
				this.minecraft.field_1724, this.minecraft.field_1690.field_1827 ? class_1836.class_1837.field_8935 : class_1836.class_1837.field_8934
			);
			List<String> list2 = Lists.<String>newArrayListWithCapacity(list.size());

			for (class_2561 lv : list) {
				list2.add(lv.method_10863());
			}

			class_1792 lv2 = arg.method_7909();
			class_1761 lv3 = lv2.method_7859();
			if (lv3 == null && lv2 == class_1802.field_8598) {
				Map<class_1887, Integer> map = class_1890.method_8222(arg);
				if (map.size() == 1) {
					class_1887 lv4 = (class_1887)map.keySet().iterator().next();

					for (class_1761 lv5 : class_1761.field_7921) {
						if (lv5.method_7740(lv4.field_9083)) {
							lv3 = lv5;
							break;
						}
					}
				}
			}

			this.field_16201.forEach((arg2, arg3) -> {
				if (arg3.method_15141(lv2)) {
					list2.add(1, "" + class_124.field_1067 + class_124.field_1064 + "#" + arg2);
				}
			});
			if (lv3 != null) {
				list2.add(1, "" + class_124.field_1067 + class_124.field_1078 + class_1074.method_4662(lv3.method_7737()));
			}

			for (int k = 0; k < list2.size(); k++) {
				if (k == 0) {
					list2.set(k, arg.method_7932().field_8908 + (String)list2.get(k));
				} else {
					list2.set(k, class_124.field_1080 + (String)list2.get(k));
				}
			}

			this.renderTooltip(list2, i, j);
		} else {
			super.renderTooltip(arg, i, j);
		}
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_308.method_1453();
		class_1761 lv = class_1761.field_7921[field_2896];

		for (class_1761 lv2 : class_1761.field_7921) {
			this.minecraft.method_1531().method_4618(field_2893);
			if (lv2.method_7741() != field_2896) {
				this.method_2468(lv2);
			}
		}

		this.minecraft.method_1531().method_4618(new class_2960("textures/gui/container/creative_inventory/tab_" + lv.method_7742()));
		this.blit(this.field_2776, this.field_2800, 0, 0, this.field_2792, this.field_2779);
		this.field_2894.render(i, j, f);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.field_2776 + 175;
		int l = this.field_2800 + 18;
		int m = l + 112;
		this.minecraft.method_1531().method_4618(field_2893);
		if (lv.method_7756()) {
			this.blit(k, l + (int)((float)(m - l - 17) * this.field_2890), 232 + (this.method_2465() ? 0 : 12), 0, 12, 15);
		}

		this.method_2468(lv);
		if (lv == class_1761.field_7918) {
			class_490.method_2486(
				this.field_2776 + 88, this.field_2800 + 45, 20, (float)(this.field_2776 + 88 - i), (float)(this.field_2800 + 45 - 30 - j), this.minecraft.field_1724
			);
		}
	}

	protected boolean method_2463(class_1761 arg, double d, double e) {
		int i = arg.method_7743();
		int j = 28 * i;
		int k = 0;
		if (arg.method_7752()) {
			j = this.field_2792 - 28 * (6 - i) + 2;
		} else if (i > 0) {
			j += i;
		}

		if (arg.method_7755()) {
			k -= 32;
		} else {
			k += this.field_2779;
		}

		return d >= (double)j && d <= (double)(j + 28) && e >= (double)k && e <= (double)(k + 32);
	}

	protected boolean method_2471(class_1761 arg, int i, int j) {
		int k = arg.method_7743();
		int l = 28 * k;
		int m = 0;
		if (arg.method_7752()) {
			l = this.field_2792 - 28 * (6 - k) + 2;
		} else if (k > 0) {
			l += k;
		}

		if (arg.method_7755()) {
			m -= 32;
		} else {
			m += this.field_2779;
		}

		if (this.method_2378(l + 3, m + 3, 23, 27, (double)i, (double)j)) {
			this.renderTooltip(class_1074.method_4662(arg.method_7737()), i, j);
			return true;
		} else {
			return false;
		}
	}

	protected void method_2468(class_1761 arg) {
		boolean bl = arg.method_7741() == field_2896;
		boolean bl2 = arg.method_7755();
		int i = arg.method_7743();
		int j = i * 28;
		int k = 0;
		int l = this.field_2776 + 28 * i;
		int m = this.field_2800;
		int n = 32;
		if (bl) {
			k += 32;
		}

		if (arg.method_7752()) {
			l = this.field_2776 + this.field_2792 - 28 * (6 - i);
		} else if (i > 0) {
			l += i;
		}

		if (bl2) {
			m -= 28;
		} else {
			k += 64;
			m += this.field_2779 - 4;
		}

		GlStateManager.disableLighting();
		this.blit(l, m, j, k, 28, 32);
		this.blitOffset = 100;
		this.itemRenderer.field_4730 = 100.0F;
		l += 6;
		m += 8 + (bl2 ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		class_1799 lv = arg.method_7747();
		this.itemRenderer.method_4023(lv, l, m);
		this.itemRenderer.method_4025(this.font, lv, l, m);
		GlStateManager.disableLighting();
		this.itemRenderer.field_4730 = 0.0F;
		this.blitOffset = 0;
	}

	public int method_2469() {
		return field_2896;
	}

	public static void method_2462(class_310 arg, int i, boolean bl, boolean bl2) {
		class_746 lv = arg.field_1724;
		class_302 lv2 = arg.method_1571();
		class_748 lv3 = lv2.method_1410(i);
		if (bl) {
			for (int j = 0; j < class_1661.method_7368(); j++) {
				class_1799 lv4 = lv3.get(j).method_7972();
				lv.field_7514.method_5447(j, lv4);
				arg.field_1761.method_2909(lv4, 36 + j);
			}

			lv.field_7498.method_7623();
		} else if (bl2) {
			for (int j = 0; j < class_1661.method_7368(); j++) {
				lv3.set(j, lv.field_7514.method_5438(j).method_7972());
			}

			String string = arg.field_1690.field_1852[i].method_16007();
			String string2 = arg.field_1690.field_1874.method_16007();
			arg.field_1705.method_1758(new class_2588("inventory.hotbarSaved", string2, string), false);
			lv2.method_1409();
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_482 extends class_1735 {
		public class_482(class_1263 arg, int i, int j, int k) {
			super(arg, i, j, k);
		}

		@Override
		public boolean method_7674(class_1657 arg) {
			return super.method_7674(arg) && this.method_7681() ? this.method_7677().method_7941("CustomCreativeLock") == null : !this.method_7681();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_483 extends class_1703 {
		public final class_2371<class_1799> field_2897 = class_2371.method_10211();

		public class_483(class_1657 arg) {
			super(null, 0);
			class_1661 lv = arg.field_7514;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 9; j++) {
					this.method_7621(new class_481.class_482(class_481.field_2895, i * 9 + j, 9 + j * 18, 18 + i * 18));
				}
			}

			for (int i = 0; i < 9; i++) {
				this.method_7621(new class_1735(lv, i, 9 + i * 18, 112));
			}

			this.method_2473(0.0F);
		}

		@Override
		public boolean method_7597(class_1657 arg) {
			return true;
		}

		public void method_2473(float f) {
			int i = (this.field_2897.size() + 9 - 1) / 9 - 5;
			int j = (int)((double)(f * (float)i) + 0.5);
			if (j < 0) {
				j = 0;
			}

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					int m = l + (k + j) * 9;
					if (m >= 0 && m < this.field_2897.size()) {
						class_481.field_2895.method_5447(l + k * 9, this.field_2897.get(m));
					} else {
						class_481.field_2895.method_5447(l + k * 9, class_1799.field_8037);
					}
				}
			}
		}

		public boolean method_2474() {
			return this.field_2897.size() > 45;
		}

		@Override
		public class_1799 method_7601(class_1657 arg, int i) {
			if (i >= this.field_7761.size() - 9 && i < this.field_7761.size()) {
				class_1735 lv = (class_1735)this.field_7761.get(i);
				if (lv != null && lv.method_7681()) {
					lv.method_7673(class_1799.field_8037);
				}
			}

			return class_1799.field_8037;
		}

		@Override
		public boolean method_7613(class_1799 arg, class_1735 arg2) {
			return arg2.field_7871 != class_481.field_2895;
		}

		@Override
		public boolean method_7615(class_1735 arg) {
			return arg.field_7871 != class_481.field_2895;
		}
	}

	@Environment(EnvType.CLIENT)
	class class_484 extends class_1735 {
		private final class_1735 field_2898;

		public class_484(class_1735 arg2, int i) {
			super(arg2.field_7871, i, 0, 0);
			this.field_2898 = arg2;
		}

		@Override
		public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
			this.field_2898.method_7667(arg, arg2);
			return arg2;
		}

		@Override
		public boolean method_7680(class_1799 arg) {
			return this.field_2898.method_7680(arg);
		}

		@Override
		public class_1799 method_7677() {
			return this.field_2898.method_7677();
		}

		@Override
		public boolean method_7681() {
			return this.field_2898.method_7681();
		}

		@Override
		public void method_7673(class_1799 arg) {
			this.field_2898.method_7673(arg);
		}

		@Override
		public void method_7668() {
			this.field_2898.method_7668();
		}

		@Override
		public int method_7675() {
			return this.field_2898.method_7675();
		}

		@Override
		public int method_7676(class_1799 arg) {
			return this.field_2898.method_7676(arg);
		}

		@Nullable
		@Override
		public String method_7679() {
			return this.field_2898.method_7679();
		}

		@Override
		public class_1799 method_7671(int i) {
			return this.field_2898.method_7671(i);
		}

		@Override
		public boolean method_7682() {
			return this.field_2898.method_7682();
		}

		@Override
		public boolean method_7674(class_1657 arg) {
			return this.field_2898.method_7674(arg);
		}
	}
}
