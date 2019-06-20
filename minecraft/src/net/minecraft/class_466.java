package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_466 extends class_465<class_1704> {
	private static final class_2960 field_2808 = new class_2960("textures/gui/container/beacon.png");
	private class_466.class_468 field_2804;
	private boolean field_2805;
	private class_1291 field_17412;
	private class_1291 field_17413;

	public class_466(class_1704 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
		this.field_2792 = 230;
		this.field_2779 = 219;
		arg.method_7596(new class_1712() {
			@Override
			public void method_7634(class_1703 arg, class_2371<class_1799> arg2) {
			}

			@Override
			public void method_7635(class_1703 arg, int i, class_1799 arg2) {
			}

			@Override
			public void method_7633(class_1703 arg, int i, int j) {
				class_466.this.field_17412 = arg.method_17374();
				class_466.this.field_17413 = arg.method_17375();
				class_466.this.field_2805 = true;
			}
		});
	}

	@Override
	protected void init() {
		super.init();
		this.field_2804 = this.addButton(new class_466.class_468(this.field_2776 + 164, this.field_2800 + 107));
		this.addButton(new class_466.class_467(this.field_2776 + 190, this.field_2800 + 107));
		this.field_2805 = true;
		this.field_2804.active = false;
	}

	@Override
	public void tick() {
		super.tick();
		int i = this.field_2797.method_17373();
		if (this.field_2805 && i >= 0) {
			this.field_2805 = false;

			for (int j = 0; j <= 2; j++) {
				int k = class_2580.field_11801[j].length;
				int l = k * 22 + (k - 1) * 2;

				for (int m = 0; m < k; m++) {
					class_1291 lv = class_2580.field_11801[j][m];
					class_466.class_469 lv2 = new class_466.class_469(this.field_2776 + 76 + m * 24 - l / 2, this.field_2800 + 22 + j * 25, lv, true);
					this.addButton(lv2);
					if (j >= i) {
						lv2.active = false;
					} else if (lv == this.field_17412) {
						lv2.method_2401(true);
					}
				}
			}

			int j = 3;
			int k = class_2580.field_11801[3].length + 1;
			int l = k * 22 + (k - 1) * 2;

			for (int mx = 0; mx < k - 1; mx++) {
				class_1291 lv = class_2580.field_11801[3][mx];
				class_466.class_469 lv2 = new class_466.class_469(this.field_2776 + 167 + mx * 24 - l / 2, this.field_2800 + 47, lv, false);
				this.addButton(lv2);
				if (3 >= i) {
					lv2.active = false;
				} else if (lv == this.field_17413) {
					lv2.method_2401(true);
				}
			}

			if (this.field_17412 != null) {
				class_466.class_469 lv3 = new class_466.class_469(this.field_2776 + 167 + (k - 1) * 24 - l / 2, this.field_2800 + 47, this.field_17412, false);
				this.addButton(lv3);
				if (3 >= i) {
					lv3.active = false;
				} else if (this.field_17412 == this.field_17413) {
					lv3.method_2401(true);
				}
			}
		}

		this.field_2804.active = this.field_2797.method_17376() && this.field_17412 != null;
	}

	@Override
	protected void method_2388(int i, int j) {
		class_308.method_1450();
		this.drawCenteredString(this.font, class_1074.method_4662("block.minecraft.beacon.primary"), 62, 10, 14737632);
		this.drawCenteredString(this.font, class_1074.method_4662("block.minecraft.beacon.secondary"), 169, 10, 14737632);

		for (class_339 lv : this.buttons) {
			if (lv.isHovered()) {
				lv.renderToolTip(i - this.field_2776, j - this.field_2800);
				break;
			}
		}

		class_308.method_1453();
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2808);
		int k = (this.width - this.field_2792) / 2;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		this.itemRenderer.field_4730 = 100.0F;
		this.itemRenderer.method_4023(new class_1799(class_1802.field_8687), k + 42, l + 109);
		this.itemRenderer.method_4023(new class_1799(class_1802.field_8477), k + 42 + 22, l + 109);
		this.itemRenderer.method_4023(new class_1799(class_1802.field_8695), k + 42 + 44, l + 109);
		this.itemRenderer.method_4023(new class_1799(class_1802.field_8620), k + 42 + 66, l + 109);
		this.itemRenderer.field_4730 = 0.0F;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		super.render(i, j, f);
		this.method_2380(i, j);
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_4072 extends class_466.class_470 {
		private final int field_18224;
		private final int field_18225;

		protected class_4072(int i, int j, int k, int l) {
			super(i, j);
			this.field_18224 = k;
			this.field_18225 = l;
		}

		@Override
		protected void method_18641() {
			this.blit(this.field_23658 + 2, this.field_23659 + 2, this.field_18224, this.field_18225, 18, 18);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_467 extends class_466.class_4072 {
		public class_467(int i, int j) {
			super(i, j, 112, 220);
		}

		@Override
		public void onPress() {
			class_466.this.minecraft.field_1724.field_3944.method_2883(new class_2815(class_466.this.minecraft.field_1724.field_7512.field_7763));
			class_466.this.minecraft.method_1507(null);
		}

		@Override
		public void renderToolTip(int i, int j) {
			class_466.this.renderTooltip(class_1074.method_4662("gui.cancel"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_468 extends class_466.class_4072 {
		public class_468(int i, int j) {
			super(i, j, 90, 220);
		}

		@Override
		public void onPress() {
			class_466.this.minecraft
				.method_1562()
				.method_2883(new class_2866(class_1291.method_5554(class_466.this.field_17412), class_1291.method_5554(class_466.this.field_17413)));
			class_466.this.minecraft.field_1724.field_3944.method_2883(new class_2815(class_466.this.minecraft.field_1724.field_7512.field_7763));
			class_466.this.minecraft.method_1507(null);
		}

		@Override
		public void renderToolTip(int i, int j) {
			class_466.this.renderTooltip(class_1074.method_4662("gui.done"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_469 extends class_466.class_470 {
		private final class_1291 field_2813;
		private final class_1058 field_18223;
		private final boolean field_17416;

		public class_469(int i, int j, class_1291 arg2, boolean bl) {
			super(i, j);
			this.field_2813 = arg2;
			this.field_18223 = class_310.method_1551().method_18505().method_18663(arg2);
			this.field_17416 = bl;
		}

		@Override
		public void onPress() {
			if (!this.method_2402()) {
				if (this.field_17416) {
					class_466.this.field_17412 = this.field_2813;
				} else {
					class_466.this.field_17413 = this.field_2813;
				}

				class_466.this.buttons.clear();
				class_466.this.children.clear();
				class_466.this.init();
				class_466.this.tick();
			}
		}

		@Override
		public void renderToolTip(int i, int j) {
			String string = class_1074.method_4662(this.field_2813.method_5567());
			if (!this.field_17416 && this.field_2813 != class_1294.field_5924) {
				string = string + " II";
			}

			class_466.this.renderTooltip(string, i, j);
		}

		@Override
		protected void method_18641() {
			class_310.method_1551().method_1531().method_4618(class_1059.field_18229);
			blit(this.field_23658 + 2, this.field_23659 + 2, this.blitOffset, 18, 18, this.field_18223);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_470 extends class_4264 {
		private boolean field_2815;

		protected class_470(int i, int j) {
			super(i, j, 22, 22, "");
		}

		@Override
		public void renderButton(int i, int j, float f) {
			class_310.method_1551().method_1531().method_4618(class_466.field_2808);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = 219;
			int l = 0;
			if (!this.active) {
				l += this.width * 2;
			} else if (this.field_2815) {
				l += this.width * 1;
			} else if (this.isHovered()) {
				l += this.width * 3;
			}

			this.blit(this.field_23658, this.field_23659, l, 219, this.width, this.height);
			this.method_18641();
		}

		protected abstract void method_18641();

		public boolean method_2402() {
			return this.field_2815;
		}

		public void method_2401(boolean bl) {
			this.field_2815 = bl;
		}
	}
}
