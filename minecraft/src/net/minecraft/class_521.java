package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_521 extends class_4280<class_521.class_4271> {
	private static final class_2960 field_19125 = new class_2960("textures/gui/resource_packs.png");
	private static final class_2561 field_19126 = new class_2588("resourcePack.incompatible");
	private static final class_2561 field_19127 = new class_2588("resourcePack.incompatible.confirm.title");
	protected final class_310 field_3166;
	private final class_2561 field_18978;

	public class_521(class_310 arg, int i, int j, class_2561 arg2) {
		super(arg, i, j, 32, j - 55 + 4, 36);
		this.field_3166 = arg;
		this.centerListVertically = false;
		this.setRenderHeader(true, (int)(9.0F * 1.5F));
		this.field_18978 = arg2;
	}

	@Override
	protected void renderHeader(int i, int j, class_289 arg) {
		class_2561 lv = new class_2585("").method_10852(this.field_18978).method_10856(class_124.field_1073, class_124.field_1067);
		this.field_3166
			.field_1772
			.method_1729(
				lv.method_10863(),
				(float)(i + this.width / 2 - this.field_3166.field_1772.method_1727(lv.method_10863()) / 2),
				(float)Math.min(this.field_19085 + 3, j),
				16777215
			);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.field_19087 - 6;
	}

	public void method_2690(class_521.class_4271 arg) {
		this.addEntry(arg);
		arg.field_19130 = this;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4271 extends class_4280.class_4281<class_521.class_4271> {
		private class_521 field_19130;
		protected final class_310 field_19128;
		protected final class_519 field_19129;
		private final class_1075 field_19131;

		public class_4271(class_521 arg, class_519 arg2, class_1075 arg3) {
			this.field_19129 = arg2;
			this.field_19128 = class_310.method_1551();
			this.field_19131 = arg3;
			this.field_19130 = arg;
		}

		public void method_20145(class_523 arg) {
			this.method_20150().method_14466().method_14468(arg.children(), this, class_521.class_4271::method_20150, true);
			this.field_19130 = arg;
		}

		protected void method_20144() {
			this.field_19131.method_4664(this.field_19128.method_1531());
		}

		protected class_3281 method_20147() {
			return this.field_19131.method_14460();
		}

		protected String method_20148() {
			return this.field_19131.method_14459().method_10863();
		}

		protected String method_20149() {
			return this.field_19131.method_14457().method_10863();
		}

		public class_1075 method_20150() {
			return this.field_19131;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			class_3281 lv = this.method_20147();
			if (!lv.method_14437()) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				class_332.fill(k - 1, j - 1, k + l - 9, j + m + 1, -8978432);
			}

			this.method_20144();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			class_332.blit(k, j, 0.0F, 0.0F, 32, 32, 32, 32);
			String string = this.method_20149();
			String string2 = this.method_20148();
			if (this.method_20151() && (this.field_19128.field_1690.field_1854 || bl)) {
				this.field_19128.method_1531().method_4618(class_521.field_19125);
				class_332.fill(k, j, k + 32, j + 32, -1601138544);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				int p = n - k;
				int q = o - j;
				if (!lv.method_14437()) {
					string = class_521.field_19126.method_10863();
					string2 = lv.method_14439().method_10863();
				}

				if (this.method_20152()) {
					if (p < 32) {
						class_332.blit(k, j, 0.0F, 32.0F, 32, 32, 256, 256);
					} else {
						class_332.blit(k, j, 0.0F, 0.0F, 32, 32, 256, 256);
					}
				} else {
					if (this.method_20153()) {
						if (p < 16) {
							class_332.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 256);
						} else {
							class_332.blit(k, j, 32.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.method_20154()) {
						if (p < 32 && p > 16 && q < 16) {
							class_332.blit(k, j, 96.0F, 32.0F, 32, 32, 256, 256);
						} else {
							class_332.blit(k, j, 96.0F, 0.0F, 32, 32, 256, 256);
						}
					}

					if (this.method_20155()) {
						if (p < 32 && p > 16 && q > 16) {
							class_332.blit(k, j, 64.0F, 32.0F, 32, 32, 256, 256);
						} else {
							class_332.blit(k, j, 64.0F, 0.0F, 32, 32, 256, 256);
						}
					}
				}
			}

			int px = this.field_19128.field_1772.method_1727(string);
			if (px > 157) {
				string = this.field_19128.field_1772.method_1714(string, 157 - this.field_19128.field_1772.method_1727("...")) + "...";
			}

			this.field_19128.field_1772.method_1720(string, (float)(k + 32 + 2), (float)(j + 1), 16777215);
			List<String> list = this.field_19128.field_1772.method_1728(string2, 157);

			for (int r = 0; r < 2 && r < list.size(); r++) {
				this.field_19128.field_1772.method_1720((String)list.get(r), (float)(k + 32 + 2), (float)(j + 12 + 10 * r), 8421504);
			}
		}

		protected boolean method_20151() {
			return !this.field_19131.method_14465() || !this.field_19131.method_14464();
		}

		protected boolean method_20152() {
			return !this.field_19129.method_2669(this);
		}

		protected boolean method_20153() {
			return this.field_19129.method_2669(this) && !this.field_19131.method_14464();
		}

		protected boolean method_20154() {
			List<class_521.class_4271> list = this.field_19130.children();
			int i = list.indexOf(this);
			return i > 0 && !((class_521.class_4271)list.get(i - 1)).field_19131.method_14465();
		}

		protected boolean method_20155() {
			List<class_521.class_4271> list = this.field_19130.children();
			int i = list.indexOf(this);
			return i >= 0 && i < list.size() - 1 && !((class_521.class_4271)list.get(i + 1)).field_19131.method_14465();
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			double f = d - (double)this.field_19130.getRowLeft();
			double g = e - (double)this.field_19130.getRowTop(this.field_19130.children().indexOf(this));
			if (this.method_20151() && f <= 32.0) {
				if (this.method_20152()) {
					this.method_20156().method_2660();
					class_3281 lv = this.method_20147();
					if (lv.method_14437()) {
						this.method_20156().method_2674(this);
					} else {
						class_2561 lv2 = lv.method_14438();
						this.field_19128.method_1507(new class_410(bl -> {
							this.field_19128.method_1507(this.method_20156());
							if (bl) {
								this.method_20156().method_2674(this);
							}
						}, class_521.field_19127, lv2));
					}

					return true;
				}

				if (f < 16.0 && this.method_20153()) {
					this.method_20156().method_2663(this);
					return true;
				}

				if (f > 16.0 && g < 16.0 && this.method_20154()) {
					List<class_521.class_4271> list = this.field_19130.children();
					int j = list.indexOf(this);
					list.remove(this);
					list.add(j - 1, this);
					this.method_20156().method_2660();
					return true;
				}

				if (f > 16.0 && g > 16.0 && this.method_20155()) {
					List<class_521.class_4271> list = this.field_19130.children();
					int j = list.indexOf(this);
					list.remove(this);
					list.add(j + 1, this);
					this.method_20156().method_2660();
					return true;
				}
			}

			return false;
		}

		public class_519 method_20156() {
			return this.field_19129;
		}
	}
}
