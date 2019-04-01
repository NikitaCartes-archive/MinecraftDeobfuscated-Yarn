package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_486 extends class_465<class_1718> {
	private static final class_2960 field_2910 = new class_2960("textures/gui/container/enchanting_table.png");
	private static final class_2960 field_2901 = new class_2960("textures/entity/enchanting_table_book.png");
	private static final class_557 field_2908 = new class_557();
	private final Random field_2911 = new Random();
	public int field_2915;
	public float field_2912;
	public float field_2914;
	public float field_2909;
	public float field_2906;
	public float field_2905;
	public float field_2904;
	private class_1799 field_2913 = class_1799.field_8037;

	public class_486(class_1718 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.font.method_1729(this.title.method_10863(), 12.0F, 5.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		this.method_2478();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = (this.width - this.field_2792) / 2;
		int k = (this.height - this.field_2779) / 2;

		for (int l = 0; l < 3; l++) {
			double f = d - (double)(j + 60);
			double g = e - (double)(k + 14 + 19 * l);
			if (f >= 0.0 && g >= 0.0 && f < 108.0 && g < 19.0 && this.field_2797.method_7604(this.minecraft.field_1724, l)) {
				this.minecraft.field_1761.method_2900(this.field_2797.field_7763, l);
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2910);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		int m = (int)this.minecraft.field_1704.method_4495();
		GlStateManager.viewport((this.width - 320) / 2 * m, (this.height - 240) / 2 * m, 320 * m, 240 * m);
		GlStateManager.translatef(-0.34F, 0.23F, 0.0F);
		GlStateManager.multMatrix(class_1159.method_4929(90.0, 1.3333334F, 9.0F, 80.0F));
		float g = 1.0F;
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		class_308.method_1452();
		GlStateManager.translatef(0.0F, 3.3F, -16.0F);
		GlStateManager.scalef(1.0F, 1.0F, 1.0F);
		float h = 5.0F;
		GlStateManager.scalef(5.0F, 5.0F, 5.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2901);
		GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
		float n = class_3532.method_16439(f, this.field_2904, this.field_2905);
		GlStateManager.translatef((1.0F - n) * 0.2F, (1.0F - n) * 0.1F, (1.0F - n) * 0.25F);
		GlStateManager.rotatef(-(1.0F - n) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
		float o = class_3532.method_16439(f, this.field_2914, this.field_2912) + 0.25F;
		float p = class_3532.method_16439(f, this.field_2914, this.field_2912) + 0.75F;
		o = (o - (float)class_3532.method_15365((double)o)) * 1.6F - 0.3F;
		p = (p - (float)class_3532.method_15365((double)p)) * 1.6F - 0.3F;
		if (o < 0.0F) {
			o = 0.0F;
		}

		if (p < 0.0F) {
			p = 0.0F;
		}

		if (o > 1.0F) {
			o = 1.0F;
		}

		if (p > 1.0F) {
			p = 1.0F;
		}

		GlStateManager.enableRescaleNormal();
		field_2908.method_17072(0.0F, o, p, n, 0.0F, 0.0625F);
		GlStateManager.disableRescaleNormal();
		class_308.method_1450();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.minecraft.field_1704.method_4489(), this.minecraft.field_1704.method_4506());
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		class_308.method_1450();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_487.method_2481().method_2480((long)this.field_2797.method_17413());
		int q = this.field_2797.method_7638();

		for (int r = 0; r < 3; r++) {
			int s = k + 60;
			int t = s + 20;
			this.blitOffset = 0.0F;
			this.minecraft.method_1531().method_4618(field_2910);
			int u = this.field_2797.field_7808[r];
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (u == 0) {
				this.blit(s, l + 14 + 19 * r, 0, 185, 108, 19);
			} else {
				String string = "" + u;
				int v = 86 - this.font.method_1727(string);
				String string2 = class_487.method_2481().method_2479(this.font, v);
				class_327 lv = this.minecraft.method_1568().method_2019(class_310.field_1749);
				int w = 6839882;
				if ((q < r + 1 || this.minecraft.field_1724.field_7520 < u) && !this.minecraft.field_1724.field_7503.field_7477) {
					this.blit(s, l + 14 + 19 * r, 0, 185, 108, 19);
					this.blit(s + 1, l + 15 + 19 * r, 16 * r, 239, 16, 16);
					lv.method_1712(string2, t, l + 16 + 19 * r, v, (w & 16711422) >> 1);
					w = 4226832;
				} else {
					int x = i - (k + 60);
					int y = j - (l + 14 + 19 * r);
					if (x >= 0 && y >= 0 && x < 108 && y < 19) {
						this.blit(s, l + 14 + 19 * r, 0, 204, 108, 19);
						w = 16777088;
					} else {
						this.blit(s, l + 14 + 19 * r, 0, 166, 108, 19);
					}

					this.blit(s + 1, l + 15 + 19 * r, 16 * r, 223, 16, 16);
					lv.method_1712(string2, t, l + 16 + 19 * r, v, w);
					w = 8453920;
				}

				lv = this.minecraft.field_1772;
				lv.method_1720(string, (float)(t + 86 - lv.method_1727(string)), (float)(l + 16 + 19 * r + 7), w);
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		f = this.minecraft.method_1488();
		this.renderBackground();
		super.render(i, j, f);
		this.method_2380(i, j);
		boolean bl = this.minecraft.field_1724.field_7503.field_7477;
		int k = this.field_2797.method_7638();

		for (int l = 0; l < 3; l++) {
			int m = this.field_2797.field_7808[l];
			class_1887 lv = class_1887.method_8191(this.field_2797.field_7812[l]);
			int n = this.field_2797.field_7810[l];
			int o = l + 1;
			if (this.method_2378(60, 14 + 19 * l, 108, 17, (double)i, (double)j) && m > 0 && n >= 0 && lv != null) {
				List<String> list = Lists.<String>newArrayList();
				list.add("" + class_124.field_1068 + class_124.field_1056 + class_1074.method_4662("container.enchant.clue", lv.method_8179(n).method_10863()));
				if (!bl) {
					list.add("");
					if (this.minecraft.field_1724.field_7520 < m) {
						list.add(class_124.field_1061 + class_1074.method_4662("container.enchant.level.requirement", this.field_2797.field_7808[l]));
					} else {
						String string;
						if (o == 1) {
							string = class_1074.method_4662("container.enchant.lapis.one");
						} else {
							string = class_1074.method_4662("container.enchant.lapis.many", o);
						}

						class_124 lv2 = k >= o ? class_124.field_1080 : class_124.field_1061;
						list.add(lv2 + "" + string);
						if (o == 1) {
							string = class_1074.method_4662("container.enchant.level.one");
						} else {
							string = class_1074.method_4662("container.enchant.level.many", o);
						}

						list.add(class_124.field_1080 + "" + string);
					}
				}

				this.renderTooltip(list, i, j);
				break;
			}
		}
	}

	public void method_2478() {
		class_1799 lv = this.field_2797.method_7611(0).method_7677();
		if (!class_1799.method_7973(lv, this.field_2913)) {
			this.field_2913 = lv;

			do {
				this.field_2909 = this.field_2909 + (float)(this.field_2911.nextInt(4) - this.field_2911.nextInt(4));
			} while (this.field_2912 <= this.field_2909 + 1.0F && this.field_2912 >= this.field_2909 - 1.0F);
		}

		this.field_2915++;
		this.field_2914 = this.field_2912;
		this.field_2904 = this.field_2905;
		boolean bl = false;

		for (int i = 0; i < 3; i++) {
			if (this.field_2797.field_7808[i] != 0) {
				bl = true;
			}
		}

		if (bl) {
			this.field_2905 += 0.2F;
		} else {
			this.field_2905 -= 0.2F;
		}

		this.field_2905 = class_3532.method_15363(this.field_2905, 0.0F, 1.0F);
		float f = (this.field_2909 - this.field_2912) * 0.4F;
		float g = 0.2F;
		f = class_3532.method_15363(f, -0.2F, 0.2F);
		this.field_2906 = this.field_2906 + (f - this.field_2906) * 0.9F;
		this.field_2912 = this.field_2912 + this.field_2906;
	}
}
