package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_465<T extends class_1703> extends class_437 implements class_3936<T> {
	public static final class_2960 field_2801 = new class_2960("textures/gui/container/inventory.png");
	protected int field_2792 = 176;
	protected int field_2779 = 166;
	protected final T field_2797;
	protected final class_1661 field_17410;
	protected final class_2561 field_17411;
	protected int field_2776;
	protected int field_2800;
	protected class_1735 field_2787;
	private class_1735 field_2777;
	private boolean field_2789;
	private class_1799 field_2782 = class_1799.field_8037;
	private int field_2784;
	private int field_2796;
	private class_1735 field_2802;
	private long field_2795;
	private class_1799 field_2785 = class_1799.field_8037;
	private class_1735 field_2780;
	private long field_2781;
	protected final Set<class_1735> field_2793 = Sets.<class_1735>newHashSet();
	protected boolean field_2794;
	private int field_2790;
	private int field_2778;
	private boolean field_2798;
	private int field_2803;
	private long field_2788;
	private class_1735 field_2799;
	private int field_2786;
	private boolean field_2783;
	private class_1799 field_2791 = class_1799.field_8037;

	public class_465(T arg, class_1661 arg2, class_2561 arg3) {
		this.field_2797 = arg;
		this.field_17410 = arg2;
		this.field_17411 = arg3;
		this.field_2798 = true;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2776 = (this.field_2561 - this.field_2792) / 2;
		this.field_2800 = (this.field_2559 - this.field_2779) / 2;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		int k = this.field_2776;
		int l = this.field_2800;
		this.method_2389(f, i, j);
		GlStateManager.disableRescaleNormal();
		class_308.method_1450();
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
		super.method_2214(i, j, f);
		class_308.method_1453();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)k, (float)l, 0.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		this.field_2787 = null;
		int m = 240;
		int n = 240;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (int o = 0; o < this.field_2797.field_7761.size(); o++) {
			class_1735 lv = (class_1735)this.field_2797.field_7761.get(o);
			if (lv.method_7682()) {
				this.method_2385(lv);
			}

			if (this.method_2387(lv, (double)i, (double)j) && lv.method_7682()) {
				this.field_2787 = lv;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				int p = lv.field_7873;
				int q = lv.field_7872;
				GlStateManager.colorMask(true, true, true, false);
				this.method_1782(p, q, p + 16, q + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}

		class_308.method_1450();
		this.method_2388(i, j);
		class_308.method_1453();
		class_1661 lv2 = this.field_2563.field_1724.field_7514;
		class_1799 lv3 = this.field_2782.method_7960() ? lv2.method_7399() : this.field_2782;
		if (!lv3.method_7960()) {
			int p = 8;
			int q = this.field_2782.method_7960() ? 8 : 16;
			String string = null;
			if (!this.field_2782.method_7960() && this.field_2789) {
				lv3 = lv3.method_7972();
				lv3.method_7939(class_3532.method_15386((float)lv3.method_7947() / 2.0F));
			} else if (this.field_2794 && this.field_2793.size() > 1) {
				lv3 = lv3.method_7972();
				lv3.method_7939(this.field_2803);
				if (lv3.method_7960()) {
					string = "" + class_124.field_1054 + "0";
				}
			}

			this.method_2382(lv3, i - k - 8, j - l - q, string);
		}

		if (!this.field_2785.method_7960()) {
			float g = (float)(class_156.method_658() - this.field_2795) / 100.0F;
			if (g >= 1.0F) {
				g = 1.0F;
				this.field_2785 = class_1799.field_8037;
			}

			int q = this.field_2802.field_7873 - this.field_2784;
			int r = this.field_2802.field_7872 - this.field_2796;
			int s = this.field_2784 + (int)((float)q * g);
			int t = this.field_2796 + (int)((float)r * g);
			this.method_2382(this.field_2785, s, t, null);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepthTest();
		class_308.method_1452();
	}

	protected void method_2380(int i, int j) {
		if (this.field_2563.field_1724.field_7514.method_7399().method_7960() && this.field_2787 != null && this.field_2787.method_7681()) {
			this.method_2218(this.field_2787.method_7677(), i, j);
		}
	}

	private void method_2382(class_1799 arg, int i, int j, String string) {
		GlStateManager.translatef(0.0F, 0.0F, 32.0F);
		this.field_2050 = 200.0F;
		this.field_2560.field_4730 = 200.0F;
		this.field_2560.method_4023(arg, i, j);
		this.field_2560.method_4022(this.field_2554, arg, i, j - (this.field_2782.method_7960() ? 0 : 8), string);
		this.field_2050 = 0.0F;
		this.field_2560.field_4730 = 0.0F;
	}

	protected void method_2388(int i, int j) {
	}

	protected abstract void method_2389(float f, int i, int j);

	private void method_2385(class_1735 arg) {
		int i = arg.field_7873;
		int j = arg.field_7872;
		class_1799 lv = arg.method_7677();
		boolean bl = false;
		boolean bl2 = arg == this.field_2777 && !this.field_2782.method_7960() && !this.field_2789;
		class_1799 lv2 = this.field_2563.field_1724.field_7514.method_7399();
		String string = null;
		if (arg == this.field_2777 && !this.field_2782.method_7960() && this.field_2789 && !lv.method_7960()) {
			lv = lv.method_7972();
			lv.method_7939(lv.method_7947() / 2);
		} else if (this.field_2794 && this.field_2793.contains(arg) && !lv2.method_7960()) {
			if (this.field_2793.size() == 1) {
				return;
			}

			if (class_1703.method_7592(arg, lv2, true) && this.field_2797.method_7615(arg)) {
				lv = lv2.method_7972();
				bl = true;
				class_1703.method_7617(this.field_2793, this.field_2790, lv, arg.method_7677().method_7960() ? 0 : arg.method_7677().method_7947());
				int k = Math.min(lv.method_7914(), arg.method_7676(lv));
				if (lv.method_7947() > k) {
					string = class_124.field_1054.toString() + k;
					lv.method_7939(k);
				}
			} else {
				this.field_2793.remove(arg);
				this.method_2379();
			}
		}

		this.field_2050 = 100.0F;
		this.field_2560.field_4730 = 100.0F;
		if (lv.method_7960() && arg.method_7682()) {
			String string2 = arg.method_7679();
			if (string2 != null) {
				class_1058 lv3 = this.field_2563.method_1549().method_4607(string2);
				GlStateManager.disableLighting();
				this.field_2563.method_1531().method_4618(class_1059.field_5275);
				this.method_1790(i, j, lv3, 16, 16);
				GlStateManager.enableLighting();
				bl2 = true;
			}
		}

		if (!bl2) {
			if (bl) {
				method_1785(i, j, i + 16, j + 16, -2130706433);
			}

			GlStateManager.enableDepthTest();
			this.field_2560.method_4026(this.field_2563.field_1724, lv, i, j);
			this.field_2560.method_4022(this.field_2554, lv, i, j, string);
		}

		this.field_2560.field_4730 = 0.0F;
		this.field_2050 = 0.0F;
	}

	private void method_2379() {
		class_1799 lv = this.field_2563.field_1724.field_7514.method_7399();
		if (!lv.method_7960() && this.field_2794) {
			if (this.field_2790 == 2) {
				this.field_2803 = lv.method_7914();
			} else {
				this.field_2803 = lv.method_7947();

				for (class_1735 lv2 : this.field_2793) {
					class_1799 lv3 = lv.method_7972();
					class_1799 lv4 = lv2.method_7677();
					int i = lv4.method_7960() ? 0 : lv4.method_7947();
					class_1703.method_7617(this.field_2793, this.field_2790, lv3, i);
					int j = Math.min(lv3.method_7914(), lv2.method_7676(lv3));
					if (lv3.method_7947() > j) {
						lv3.method_7939(j);
					}

					this.field_2803 = this.field_2803 - (lv3.method_7947() - i);
				}
			}
		}
	}

	private class_1735 method_2386(double d, double e) {
		for (int i = 0; i < this.field_2797.field_7761.size(); i++) {
			class_1735 lv = (class_1735)this.field_2797.field_7761.get(i);
			if (this.method_2387(lv, d, e) && lv.method_7682()) {
				return lv;
			}
		}

		return null;
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (super.method_16807(d, e, i)) {
			return true;
		} else {
			boolean bl = this.field_2563.field_1690.field_1871.method_1433(i);
			class_1735 lv = this.method_2386(d, e);
			long l = class_156.method_658();
			this.field_2783 = this.field_2799 == lv && l - this.field_2788 < 250L && this.field_2786 == i;
			this.field_2798 = false;
			if (i == 0 || i == 1 || bl) {
				int j = this.field_2776;
				int k = this.field_2800;
				boolean bl2 = this.method_2381(d, e, j, k, i);
				int m = -1;
				if (lv != null) {
					m = lv.field_7874;
				}

				if (bl2) {
					m = -999;
				}

				if (this.field_2563.field_1690.field_1854 && bl2 && this.field_2563.field_1724.field_7514.method_7399().method_7960()) {
					this.field_2563.method_1507(null);
					return true;
				}

				if (m != -1) {
					if (this.field_2563.field_1690.field_1854) {
						if (lv != null && lv.method_7681()) {
							this.field_2777 = lv;
							this.field_2782 = class_1799.field_8037;
							this.field_2789 = i == 1;
						} else {
							this.field_2777 = null;
						}
					} else if (!this.field_2794) {
						if (this.field_2563.field_1724.field_7514.method_7399().method_7960()) {
							if (this.field_2563.field_1690.field_1871.method_1433(i)) {
								this.method_2383(lv, m, i, class_1713.field_7796);
							} else {
								boolean bl3 = m != -999
									&& (
										class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 340)
											|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 344)
									);
								class_1713 lv2 = class_1713.field_7790;
								if (bl3) {
									this.field_2791 = lv != null && lv.method_7681() ? lv.method_7677().method_7972() : class_1799.field_8037;
									lv2 = class_1713.field_7794;
								} else if (m == -999) {
									lv2 = class_1713.field_7795;
								}

								this.method_2383(lv, m, i, lv2);
							}

							this.field_2798 = true;
						} else {
							this.field_2794 = true;
							this.field_2778 = i;
							this.field_2793.clear();
							if (i == 0) {
								this.field_2790 = 0;
							} else if (i == 1) {
								this.field_2790 = 1;
							} else if (this.field_2563.field_1690.field_1871.method_1433(i)) {
								this.field_2790 = 2;
							}
						}
					}
				}
			}

			this.field_2799 = lv;
			this.field_2788 = l;
			this.field_2786 = i;
			return true;
		}
	}

	protected boolean method_2381(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		class_1735 lv = this.method_2386(d, e);
		class_1799 lv2 = this.field_2563.field_1724.field_7514.method_7399();
		if (this.field_2777 != null && this.field_2563.field_1690.field_1854) {
			if (i == 0 || i == 1) {
				if (this.field_2782.method_7960()) {
					if (lv != this.field_2777 && !this.field_2777.method_7677().method_7960()) {
						this.field_2782 = this.field_2777.method_7677().method_7972();
					}
				} else if (this.field_2782.method_7947() > 1 && lv != null && class_1703.method_7592(lv, this.field_2782, false)) {
					long l = class_156.method_658();
					if (this.field_2780 == lv) {
						if (l - this.field_2781 > 500L) {
							this.method_2383(this.field_2777, this.field_2777.field_7874, 0, class_1713.field_7790);
							this.method_2383(lv, lv.field_7874, 1, class_1713.field_7790);
							this.method_2383(this.field_2777, this.field_2777.field_7874, 0, class_1713.field_7790);
							this.field_2781 = l + 750L;
							this.field_2782.method_7934(1);
						}
					} else {
						this.field_2780 = lv;
						this.field_2781 = l;
					}
				}
			}
		} else if (this.field_2794
			&& lv != null
			&& !lv2.method_7960()
			&& (lv2.method_7947() > this.field_2793.size() || this.field_2790 == 2)
			&& class_1703.method_7592(lv, lv2, true)
			&& lv.method_7680(lv2)
			&& this.field_2797.method_7615(lv)) {
			this.field_2793.add(lv);
			this.method_2379();
		}

		return true;
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		class_1735 lv = this.method_2386(d, e);
		int j = this.field_2776;
		int k = this.field_2800;
		boolean bl = this.method_2381(d, e, j, k, i);
		int l = -1;
		if (lv != null) {
			l = lv.field_7874;
		}

		if (bl) {
			l = -999;
		}

		if (this.field_2783 && lv != null && i == 0 && this.field_2797.method_7613(class_1799.field_8037, lv)) {
			if (method_2223()) {
				if (!this.field_2791.method_7960()) {
					for (class_1735 lv2 : this.field_2797.field_7761) {
						if (lv2 != null
							&& lv2.method_7674(this.field_2563.field_1724)
							&& lv2.method_7681()
							&& lv2.field_7871 == lv.field_7871
							&& class_1703.method_7592(lv2, this.field_2791, true)) {
							this.method_2383(lv2, lv2.field_7874, i, class_1713.field_7794);
						}
					}
				}
			} else {
				this.method_2383(lv, l, i, class_1713.field_7793);
			}

			this.field_2783 = false;
			this.field_2788 = 0L;
		} else {
			if (this.field_2794 && this.field_2778 != i) {
				this.field_2794 = false;
				this.field_2793.clear();
				this.field_2798 = true;
				return true;
			}

			if (this.field_2798) {
				this.field_2798 = false;
				return true;
			}

			if (this.field_2777 != null && this.field_2563.field_1690.field_1854) {
				if (i == 0 || i == 1) {
					if (this.field_2782.method_7960() && lv != this.field_2777) {
						this.field_2782 = this.field_2777.method_7677();
					}

					boolean bl2 = class_1703.method_7592(lv, this.field_2782, false);
					if (l != -1 && !this.field_2782.method_7960() && bl2) {
						this.method_2383(this.field_2777, this.field_2777.field_7874, i, class_1713.field_7790);
						this.method_2383(lv, l, 0, class_1713.field_7790);
						if (this.field_2563.field_1724.field_7514.method_7399().method_7960()) {
							this.field_2785 = class_1799.field_8037;
						} else {
							this.method_2383(this.field_2777, this.field_2777.field_7874, i, class_1713.field_7790);
							this.field_2784 = class_3532.method_15357(d - (double)j);
							this.field_2796 = class_3532.method_15357(e - (double)k);
							this.field_2802 = this.field_2777;
							this.field_2785 = this.field_2782;
							this.field_2795 = class_156.method_658();
						}
					} else if (!this.field_2782.method_7960()) {
						this.field_2784 = class_3532.method_15357(d - (double)j);
						this.field_2796 = class_3532.method_15357(e - (double)k);
						this.field_2802 = this.field_2777;
						this.field_2785 = this.field_2782;
						this.field_2795 = class_156.method_658();
					}

					this.field_2782 = class_1799.field_8037;
					this.field_2777 = null;
				}
			} else if (this.field_2794 && !this.field_2793.isEmpty()) {
				this.method_2383(null, -999, class_1703.method_7591(0, this.field_2790), class_1713.field_7789);

				for (class_1735 lv2x : this.field_2793) {
					this.method_2383(lv2x, lv2x.field_7874, class_1703.method_7591(1, this.field_2790), class_1713.field_7789);
				}

				this.method_2383(null, -999, class_1703.method_7591(2, this.field_2790), class_1713.field_7789);
			} else if (!this.field_2563.field_1724.field_7514.method_7399().method_7960()) {
				if (this.field_2563.field_1690.field_1871.method_1433(i)) {
					this.method_2383(lv, l, i, class_1713.field_7796);
				} else {
					boolean bl2 = l != -999
						&& (
							class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 340)
								|| class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 344)
						);
					if (bl2) {
						this.field_2791 = lv != null && lv.method_7681() ? lv.method_7677().method_7972() : class_1799.field_8037;
					}

					this.method_2383(lv, l, i, bl2 ? class_1713.field_7794 : class_1713.field_7790);
				}
			}
		}

		if (this.field_2563.field_1724.field_7514.method_7399().method_7960()) {
			this.field_2788 = 0L;
		}

		this.field_2794 = false;
		return true;
	}

	private boolean method_2387(class_1735 arg, double d, double e) {
		return this.method_2378(arg.field_7873, arg.field_7872, 16, 16, d, e);
	}

	protected boolean method_2378(int i, int j, int k, int l, double d, double e) {
		int m = this.field_2776;
		int n = this.field_2800;
		d -= (double)m;
		e -= (double)n;
		return d >= (double)(i - 1) && d < (double)(i + k + 1) && e >= (double)(j - 1) && e < (double)(j + l + 1);
	}

	protected void method_2383(class_1735 arg, int i, int j, class_1713 arg2) {
		if (arg != null) {
			i = arg.field_7874;
		}

		this.field_2563.field_1761.method_2906(this.field_2797.field_7763, i, j, arg2, this.field_2563.field_1724);
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (super.method_16805(i, j, k)) {
			return true;
		} else {
			if (i == 256 || this.field_2563.field_1690.field_1822.method_1417(i, j)) {
				this.field_2563.field_1724.method_7346();
			}

			this.method_2384(i, j);
			if (this.field_2787 != null && this.field_2787.method_7681()) {
				if (this.field_2563.field_1690.field_1871.method_1417(i, j)) {
					this.method_2383(this.field_2787, this.field_2787.field_7874, 0, class_1713.field_7796);
				} else if (this.field_2563.field_1690.field_1869.method_1417(i, j)) {
					this.method_2383(this.field_2787, this.field_2787.field_7874, method_2238() ? 1 : 0, class_1713.field_7795);
				}
			}

			return true;
		}
	}

	protected boolean method_2384(int i, int j) {
		if (this.field_2563.field_1724.field_7514.method_7399().method_7960() && this.field_2787 != null) {
			for (int k = 0; k < 9; k++) {
				if (this.field_2563.field_1690.field_1852[k].method_1417(i, j)) {
					this.method_2383(this.field_2787, this.field_2787.field_7874, k, class_1713.field_7791);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void method_2234() {
		if (this.field_2563.field_1724 != null) {
			this.field_2797.method_7595(this.field_2563.field_1724);
		}
	}

	@Override
	public boolean method_2222() {
		return false;
	}

	@Override
	public void method_2225() {
		super.method_2225();
		if (!this.field_2563.field_1724.method_5805() || this.field_2563.field_1724.field_5988) {
			this.field_2563.field_1724.method_7346();
		}
	}

	@Override
	public T method_17577() {
		return this.field_2797;
	}
}
