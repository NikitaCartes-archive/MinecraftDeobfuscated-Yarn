package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_492 extends class_465<class_1728> {
	private static final class_2960 field_2950 = new class_2960("textures/gui/container/villager2.png");
	private int field_19161;
	private final class_492.class_493[] field_19162 = new class_492.class_493[7];
	private int field_19163;
	private boolean field_19164;

	public class_492(class_1728 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
		this.field_2792 = 276;
	}

	private void method_2496() {
		this.field_2797.method_7650(this.field_19161);
		this.field_2797.method_20215(this.field_19161);
		this.minecraft.method_1562().method_2883(new class_2863(this.field_19161));
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.field_2792) / 2;
		int j = (this.height - this.field_2779) / 2;
		int k = j + 16 + 2;

		for (int l = 0; l < 7; l++) {
			this.field_19162[l] = this.addButton(new class_492.class_493(i + 5, k, l, arg -> {
				if (arg instanceof class_492.class_493) {
					this.field_19161 = ((class_492.class_493)arg).method_20228() + this.field_19163;
					this.method_2496();
				}
			}));
			k += 20;
		}
	}

	@Override
	protected void method_2388(int i, int j) {
		int k = this.field_2797.method_19258();
		int l = this.field_2779 - 94;
		if (k > 0 && k <= 5 && this.field_2797.method_19259()) {
			String string = this.title.method_10863();
			String string2 = "- " + class_1074.method_4662("merchant.level." + k);
			int m = this.font.method_1727(string);
			int n = this.font.method_1727(string2);
			int o = m + n + 3;
			int p = 49 + this.field_2792 / 2 - o / 2;
			this.font.method_1729(string, (float)p, 6.0F, 4210752);
			this.font.method_1729(this.field_17410.method_5476().method_10863(), 107.0F, (float)l, 4210752);
			this.font.method_1729(string2, (float)(p + m + 3), 6.0F, 4210752);
		} else {
			String string = this.title.method_10863();
			this.font.method_1729(string, (float)(49 + this.field_2792 / 2 - this.font.method_1727(string) / 2), 6.0F, 4210752);
			this.font.method_1729(this.field_17410.method_5476().method_10863(), 107.0F, (float)l, 4210752);
		}

		String string = class_1074.method_4662("merchant.trades");
		int q = this.font.method_1727(string);
		this.font.method_1729(string, (float)(5 - q / 2 + 48), 6.0F, 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2950);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		blit(k, l, this.blitOffset, 0.0F, 0.0F, this.field_2792, this.field_2779, 256, 512);
		class_1916 lv = this.field_2797.method_17438();
		if (!lv.isEmpty()) {
			int m = this.field_19161;
			if (m < 0 || m >= lv.size()) {
				return;
			}

			class_1914 lv2 = (class_1914)lv.get(m);
			if (lv2.method_8255()) {
				this.minecraft.method_1531().method_4618(field_2950);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				blit(this.field_2776 + 83 + 99, this.field_2800 + 35, this.blitOffset, 311.0F, 0.0F, 28, 21, 256, 512);
			}
		}
	}

	private void method_19413(int i, int j, class_1914 arg) {
		this.minecraft.method_1531().method_4618(field_2950);
		int k = this.field_2797.method_19258();
		int l = this.field_2797.method_19254();
		if (k < 5) {
			blit(i + 136, j + 16, this.blitOffset, 0.0F, 186.0F, 102, 5, 256, 512);
			int m = class_3850.method_19194(k);
			if (l >= m && class_3850.method_19196(k)) {
				int n = 100;
				float f = (float)(100 / (class_3850.method_19195(k) - m));
				int o = class_3532.method_15375(f * (float)(l - m));
				blit(i + 136, j + 16, this.blitOffset, 0.0F, 191.0F, o + 1, 5, 256, 512);
				int p = this.field_2797.method_19256();
				if (p > 0) {
					int q = Math.min(class_3532.method_15375((float)p * f), 100 - o);
					blit(i + 136 + o + 1, j + 16 + 1, this.blitOffset, 2.0F, 182.0F, q, 3, 256, 512);
				}
			}
		}
	}

	private void method_20221(int i, int j, class_1916 arg) {
		class_308.method_1450();
		int k = arg.size() + 1 - 7;
		if (k > 1) {
			int l = 139 - (27 + (k - 1) * 139 / k);
			int m = 1 + l / k + 139 / k;
			int n = 113;
			int o = Math.min(113, this.field_19163 * m);
			if (this.field_19163 == k - 1) {
				o = 113;
			}

			blit(i + 94, j + 18 + o, this.blitOffset, 0.0F, 199.0F, 6, 27, 256, 512);
		} else {
			blit(i + 94, j + 18, this.blitOffset, 6.0F, 199.0F, 6, 27, 256, 512);
		}

		class_308.method_1453();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		class_1916 lv = this.field_2797.method_17438();
		if (!lv.isEmpty()) {
			int k = (this.width - this.field_2792) / 2;
			int l = (this.height - this.field_2779) / 2;
			int m = l + 16 + 1;
			int n = k + 5 + 5;
			GlStateManager.pushMatrix();
			class_308.method_1453();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.minecraft.method_1531().method_4618(field_2950);
			this.method_20221(k, l, lv);
			int o = 0;

			for (class_1914 lv2 : lv) {
				if (!this.method_20220(lv.size()) || o >= this.field_19163 && o < 7 + this.field_19163) {
					class_1799 lv3 = lv2.method_8246();
					class_1799 lv4 = lv2.method_19272();
					class_1799 lv5 = lv2.method_8247();
					class_1799 lv6 = lv2.method_8250();
					this.itemRenderer.field_4730 = 100.0F;
					int p = m + 2;
					this.method_20222(lv4, lv3, n, p);
					if (!lv5.method_7960()) {
						this.itemRenderer.method_4023(lv5, k + 5 + 35, p);
						this.itemRenderer.method_4025(this.font, lv5, k + 5 + 35, p);
					}

					this.method_20223(lv2, k, p);
					this.itemRenderer.method_4023(lv6, k + 5 + 68, p);
					this.itemRenderer.method_4025(this.font, lv6, k + 5 + 68, p);
					this.itemRenderer.field_4730 = 0.0F;
					m += 20;
					o++;
				} else {
					o++;
				}
			}

			int q = this.field_19161;
			class_1914 lv2x = (class_1914)lv.get(q);
			GlStateManager.disableLighting();
			if (this.field_2797.method_19259()) {
				this.method_19413(k, l, lv2x);
			}

			if (lv2x.method_8255() && this.method_2378(186, 35, 22, 21, (double)i, (double)j) && this.field_2797.method_20701()) {
				this.renderTooltip(class_1074.method_4662("merchant.deprecated"), i, j);
			}

			for (class_492.class_493 lv7 : this.field_19162) {
				if (lv7.isHovered()) {
					lv7.renderToolTip(i, j);
				}

				lv7.visible = lv7.field_19165 < this.field_2797.method_17438().size();
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			class_308.method_1452();
		}

		this.method_2380(i, j);
	}

	private void method_20223(class_1914 arg, int i, int j) {
		class_308.method_1450();
		GlStateManager.enableBlend();
		this.minecraft.method_1531().method_4618(field_2950);
		if (arg.method_8255()) {
			blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 25.0F, 171.0F, 10, 9, 256, 512);
		} else {
			blit(i + 5 + 35 + 20, j + 3, this.blitOffset, 15.0F, 171.0F, 10, 9, 256, 512);
		}

		class_308.method_1453();
	}

	private void method_20222(class_1799 arg, class_1799 arg2, int i, int j) {
		this.itemRenderer.method_4023(arg, i, j);
		if (arg2.method_7947() == arg.method_7947()) {
			this.itemRenderer.method_4025(this.font, arg, i, j);
		} else {
			this.itemRenderer.method_4022(this.font, arg2, i, j, arg2.method_7947() == 1 ? "1" : null);
			this.itemRenderer.method_4022(this.font, arg, i + 14, j, arg.method_7947() == 1 ? "1" : null);
			this.minecraft.method_1531().method_4618(field_2950);
			this.blitOffset += 300;
			class_308.method_1450();
			blit(i + 7, j + 12, this.blitOffset, 0.0F, 176.0F, 9, 2, 256, 512);
			class_308.method_1453();
			this.blitOffset -= 300;
		}
	}

	private boolean method_20220(int i) {
		return i > 7;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		int i = this.field_2797.method_17438().size();
		if (this.method_20220(i)) {
			int j = i - 7;
			this.field_19163 = (int)((double)this.field_19163 - f);
			this.field_19163 = class_3532.method_15340(this.field_19163, 0, j);
		}

		return true;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		int j = this.field_2797.method_17438().size();
		if (this.field_19164) {
			int k = this.field_2800 + 18;
			int l = k + 139;
			int m = j - 7;
			float h = ((float)e - (float)k - 13.5F) / ((float)(l - k) - 27.0F);
			h = h * (float)m + 0.5F;
			this.field_19163 = class_3532.method_15340((int)h, 0, m);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.field_19164 = false;
		int j = (this.width - this.field_2792) / 2;
		int k = (this.height - this.field_2779) / 2;
		if (this.method_20220(this.field_2797.method_17438().size())
			&& d > (double)(j + 94)
			&& d < (double)(j + 94 + 6)
			&& e > (double)(k + 18)
			&& e <= (double)(k + 18 + 139 + 1)) {
			this.field_19164 = true;
		}

		return super.mouseClicked(d, e, i);
	}

	@Environment(EnvType.CLIENT)
	class class_493 extends class_4185 {
		final int field_19165;

		public class_493(int i, int j, int k, class_4185.class_4241 arg2) {
			super(i, j, 89, 20, "", arg2);
			this.field_19165 = k;
			this.visible = false;
		}

		public int method_20228() {
			return this.field_19165;
		}

		@Override
		public void renderToolTip(int i, int j) {
			if (this.isHovered && class_492.this.field_2797.method_17438().size() > this.field_19165 + class_492.this.field_19163) {
				if (i < this.field_23658 + 20) {
					class_1799 lv = ((class_1914)class_492.this.field_2797.method_17438().get(this.field_19165 + class_492.this.field_19163)).method_19272();
					class_492.this.renderTooltip(lv, i, j);
				} else if (i < this.field_23658 + 50 && i > this.field_23658 + 30) {
					class_1799 lv = ((class_1914)class_492.this.field_2797.method_17438().get(this.field_19165 + class_492.this.field_19163)).method_8247();
					if (!lv.method_7960()) {
						class_492.this.renderTooltip(lv, i, j);
					}
				} else if (i > this.field_23658 + 65) {
					class_1799 lv = ((class_1914)class_492.this.field_2797.method_17438().get(this.field_19165 + class_492.this.field_19163)).method_8250();
					class_492.this.renderTooltip(lv, i, j);
				}
			}
		}
	}
}
