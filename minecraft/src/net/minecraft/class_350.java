package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_350<E extends class_350.class_351<E>> extends class_362 implements class_4068 {
	protected final class_310 field_19081;
	protected final int field_19082;
	private final List<E> field_2142 = new class_350.class_352();
	protected int field_19083;
	protected int field_19084;
	protected int field_19085;
	protected int field_19086;
	protected int field_19087;
	protected int field_19088;
	protected boolean field_19089 = true;
	protected int field_19090 = -2;
	private double field_19094;
	protected boolean field_19091 = true;
	protected boolean field_19092;
	protected int field_19093;
	private boolean field_19095;
	private E field_19096;

	public class_350(class_310 arg, int i, int j, int k, int l, int m) {
		this.field_19081 = arg;
		this.field_19083 = i;
		this.field_19084 = j;
		this.field_19085 = k;
		this.field_19086 = l;
		this.field_19082 = m;
		this.field_19088 = 0;
		this.field_19087 = i;
	}

	protected void method_20063(boolean bl, int i) {
		this.field_19092 = bl;
		this.field_19093 = i;
		if (!bl) {
			this.field_19093 = 0;
		}
	}

	public int method_20053() {
		return 220;
	}

	@Nullable
	public E method_20064() {
		return this.field_19096;
	}

	public void method_20062(@Nullable E arg) {
		this.field_19096 = arg;
	}

	@Nullable
	public E method_20068() {
		return (E)super.getFocused();
	}

	@Override
	public final List<E> children() {
		return this.field_2142;
	}

	protected final void method_1902() {
		this.field_2142.clear();
	}

	private E method_1900(int i) {
		return (E)this.children().get(i);
	}

	protected final int method_1901(E arg) {
		this.field_2142.add(arg);
		return this.field_2142.size() - 1;
	}

	protected final int method_20073() {
		return this.children().size();
	}

	@Override
	public void onFocusChanged(boolean bl, boolean bl2) {
		if (bl2 && this.method_20064() == null && this.method_20073() > 0) {
			this.method_20069(1);
		}
	}

	protected boolean method_20057(int i) {
		return Objects.equals(this.method_20064(), this.children().get(i));
	}

	@Nullable
	protected final E method_20055(double d, double e) {
		int i = this.method_20053() / 2;
		int j = this.field_19088 + this.field_19083 / 2;
		int k = j - i;
		int l = j + i;
		int m = class_3532.method_15357(e - (double)this.field_19085) - this.field_19093 + (int)this.method_20077() - 4;
		int n = m / this.field_19082;
		return (E)(d < (double)this.method_20078() && d >= (double)k && d <= (double)l && n >= 0 && m >= 0 && n < this.method_20073() ? this.children().get(n) : null);
	}

	public void method_20059(int i, int j, int k, int l) {
		this.field_19083 = i;
		this.field_19084 = j;
		this.field_19085 = k;
		this.field_19086 = l;
		this.field_19088 = 0;
		this.field_19087 = i;
	}

	public void method_20065(int i) {
		this.field_19088 = i;
		this.field_19087 = i + this.field_19083;
	}

	protected int method_20074() {
		return this.method_20073() * this.field_19082 + this.field_19093;
	}

	protected void method_20058(int i, int j) {
	}

	protected void method_20061(int i, int j, class_289 arg) {
	}

	protected void method_20076() {
	}

	protected void method_20066(int i, int j) {
	}

	@Override
	public void render(int i, int j, float f) {
		this.method_20076();
		int k = this.method_20078();
		int l = k + 6;
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.field_19081.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float g = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)this.field_19086, 0.0)
			.method_1312((double)((float)this.field_19088 / 32.0F), (double)((float)(this.field_19086 + (int)this.method_20077()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19086, 0.0)
			.method_1312((double)((float)this.field_19087 / 32.0F), (double)((float)(this.field_19086 + (int)this.method_20077()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19085, 0.0)
			.method_1312((double)((float)this.field_19087 / 32.0F), (double)((float)(this.field_19085 + (int)this.method_20077()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv2.method_1315((double)this.field_19088, (double)this.field_19085, 0.0)
			.method_1312((double)((float)this.field_19088 / 32.0F), (double)((float)(this.field_19085 + (int)this.method_20077()) / 32.0F))
			.method_1323(32, 32, 32, 255)
			.method_1344();
		lv.method_1350();
		int m = this.method_20079();
		int n = this.field_19085 + 4 - (int)this.method_20077();
		if (this.field_19092) {
			this.method_20061(m, n, lv);
		}

		this.method_20060(m, n, i, j, f);
		GlStateManager.disableDepthTest();
		this.method_20067(0, this.field_19085, 255, 255);
		this.method_20067(this.field_19086, this.field_19084, 255, 255);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
		);
		GlStateManager.disableAlphaTest();
		GlStateManager.shadeModel(7425);
		GlStateManager.disableTexture();
		int o = 4;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)(this.field_19085 + 4), 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
		lv2.method_1315((double)this.field_19087, (double)(this.field_19085 + 4), 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19085, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
		lv2.method_1315((double)this.field_19088, (double)this.field_19085, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
		lv.method_1350();
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)this.field_19086, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
		lv2.method_1315((double)this.field_19087, (double)this.field_19086, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
		lv2.method_1315((double)this.field_19087, (double)(this.field_19086 - 4), 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
		lv2.method_1315((double)this.field_19088, (double)(this.field_19086 - 4), 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
		lv.method_1350();
		int p = this.method_20081();
		if (p > 0) {
			int q = (int)((float)((this.field_19086 - this.field_19085) * (this.field_19086 - this.field_19085)) / (float)this.method_20074());
			q = class_3532.method_15340(q, 32, this.field_19086 - this.field_19085 - 8);
			int r = (int)this.method_20077() * (this.field_19086 - this.field_19085 - q) / p + this.field_19085;
			if (r < this.field_19085) {
				r = this.field_19085;
			}

			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)k, (double)this.field_19086, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)l, (double)this.field_19086, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)l, (double)this.field_19085, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)k, (double)this.field_19085, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv.method_1350();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)k, (double)(r + q), 0.0).method_1312(0.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
			lv2.method_1315((double)l, (double)(r + q), 0.0).method_1312(1.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
			lv2.method_1315((double)l, (double)r, 0.0).method_1312(1.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
			lv2.method_1315((double)k, (double)r, 0.0).method_1312(0.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
			lv.method_1350();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)k, (double)(r + q - 1), 0.0).method_1312(0.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
			lv2.method_1315((double)(l - 1), (double)(r + q - 1), 0.0).method_1312(1.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
			lv2.method_1315((double)(l - 1), (double)r, 0.0).method_1312(1.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
			lv2.method_1315((double)k, (double)r, 0.0).method_1312(0.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
			lv.method_1350();
		}

		this.method_20066(i, j);
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7424);
		GlStateManager.enableAlphaTest();
		GlStateManager.disableBlend();
	}

	protected void method_20070(E arg) {
		this.method_20054((double)(this.children().indexOf(arg) * this.field_19082 + this.field_19082 / 2 - (this.field_19086 - this.field_19085) / 2));
	}

	protected void method_20072(E arg) {
		int i = this.method_20071(this.children().indexOf(arg));
		int j = i - this.field_19085 - 4 - this.field_19082;
		if (j < 0) {
			this.method_20075(j);
		}

		int k = this.field_19086 - i - this.field_19082 - this.field_19082;
		if (k < 0) {
			this.method_20075(-k);
		}
	}

	private void method_20075(int i) {
		this.method_20054(this.method_20077() + (double)i);
		this.field_19090 = -2;
	}

	public double method_20077() {
		return this.field_19094;
	}

	public void method_20054(double d) {
		this.field_19094 = class_3532.method_15350(d, 0.0, (double)this.method_20081());
	}

	private int method_20081() {
		return Math.max(0, this.method_20074() - (this.field_19086 - this.field_19085 - 4));
	}

	protected void method_20056(double d, double e, int i) {
		this.field_19095 = i == 0 && d >= (double)this.method_20078() && d < (double)(this.method_20078() + 6);
	}

	protected int method_20078() {
		return this.field_19083 / 2 + 124;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.method_20056(d, e, i);
		if (!this.isMouseOver(d, e)) {
			return false;
		} else {
			E lv = this.method_20055(d, e);
			if (lv != null) {
				if (lv.mouseClicked(d, e, i)) {
					if (this.children().contains(lv)) {
						this.method_20084(lv);
					}

					this.setDragging(true);
					return true;
				}
			} else if (i == 0) {
				this.method_20058(
					(int)(d - (double)(this.field_19088 + this.field_19083 / 2 - this.method_20053() / 2)), (int)(e - (double)this.field_19085) + (int)this.method_20077() - 4
				);
				return true;
			}

			return this.field_19095;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (this.method_20068() != null) {
			this.method_20068().mouseReleased(d, e, i);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (super.mouseDragged(d, e, i, f, g)) {
			return true;
		} else if (i == 0 && this.field_19095) {
			if (e < (double)this.field_19085) {
				this.method_20054(0.0);
			} else if (e > (double)this.field_19086) {
				this.method_20054((double)this.method_20081());
			} else {
				double h = (double)Math.max(1, this.method_20081());
				int j = this.field_19086 - this.field_19085;
				int k = class_3532.method_15340((int)((float)(j * j) / (float)this.method_20074()), 32, j - 8);
				double l = Math.max(1.0, h / (double)(j - k));
				this.method_20054(this.method_20077() + g * l);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		this.method_20054(this.method_20077() - f * (double)this.field_19082 / 2.0);
		return true;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 264) {
			this.method_20069(1);
			return true;
		} else if (i == 265) {
			this.method_20069(-1);
			return true;
		} else {
			return false;
		}
	}

	protected void method_20069(int i) {
		if (!this.children().isEmpty()) {
			int j = this.children().indexOf(this.method_20064());
			int k = class_3532.method_15340(j + i, 0, this.method_20073() - 1);
			E lv = (E)this.children().get(k);
			this.method_20062(lv);
			this.method_20072(lv);
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return e >= (double)this.field_19085 && e <= (double)this.field_19086 && d >= (double)this.field_19088 && d <= (double)this.field_19087;
	}

	protected void method_20060(int i, int j, int k, int l, float f) {
		int m = this.method_20073();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.field_19082 + this.field_19093;
			int p = this.field_19082 - 4;
			E lv3 = this.method_1900(n);
			if (o > this.field_19086 || o + p < this.field_19085) {
				lv3.method_1904(f);
			}

			int q = this.method_20053();
			if (this.field_19091 && this.method_20057(n)) {
				int r = this.field_19088 + this.field_19083 / 2 - q / 2;
				int s = this.field_19088 + this.field_19083 / 2 + q / 2;
				GlStateManager.disableTexture();
				float g = this.method_20080() ? 1.0F : 0.5F;
				GlStateManager.color4f(g, g, g, 1.0F);
				lv2.method_1328(7, class_290.field_1592);
				lv2.method_1315((double)r, (double)(o + p + 2), 0.0).method_1344();
				lv2.method_1315((double)s, (double)(o + p + 2), 0.0).method_1344();
				lv2.method_1315((double)s, (double)(o - 2), 0.0).method_1344();
				lv2.method_1315((double)r, (double)(o - 2), 0.0).method_1344();
				lv.method_1350();
				GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				lv2.method_1328(7, class_290.field_1592);
				lv2.method_1315((double)(r + 1), (double)(o + p + 1), 0.0).method_1344();
				lv2.method_1315((double)(s - 1), (double)(o + p + 1), 0.0).method_1344();
				lv2.method_1315((double)(s - 1), (double)(o - 1), 0.0).method_1344();
				lv2.method_1315((double)(r + 1), (double)(o - 1), 0.0).method_1344();
				lv.method_1350();
				GlStateManager.enableTexture();
			}

			int r = this.method_20071(n);
			int s = this.method_20079();
			lv3.method_1903(n, r, s, q, p, k, l, this.isMouseOver((double)k, (double)l) && Objects.equals(this.method_20055((double)k, (double)l), lv3), f);
		}
	}

	protected int method_20079() {
		return this.field_19088 + this.field_19083 / 2 - this.method_20053() / 2 + 2;
	}

	protected int method_20071(int i) {
		return this.field_19085 + 4 - (int)this.method_20077() + i * this.field_19082 + this.field_19093;
	}

	protected boolean method_20080() {
		return false;
	}

	protected void method_20067(int i, int j, int k, int l) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.field_19081.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_19088, (double)j, 0.0).method_1312(0.0, (double)((float)j / 32.0F)).method_1323(64, 64, 64, l).method_1344();
		lv2.method_1315((double)(this.field_19088 + this.field_19083), (double)j, 0.0)
			.method_1312((double)((float)this.field_19083 / 32.0F), (double)((float)j / 32.0F))
			.method_1323(64, 64, 64, l)
			.method_1344();
		lv2.method_1315((double)(this.field_19088 + this.field_19083), (double)i, 0.0)
			.method_1312((double)((float)this.field_19083 / 32.0F), (double)((float)i / 32.0F))
			.method_1323(64, 64, 64, k)
			.method_1344();
		lv2.method_1315((double)this.field_19088, (double)i, 0.0).method_1312(0.0, (double)((float)i / 32.0F)).method_1323(64, 64, 64, k).method_1344();
		lv.method_1350();
	}

	@Override
	public boolean method_20087(boolean bl) {
		if (super.method_20087(bl)) {
			E lv = this.method_20068();
			if (this.children().contains(lv)) {
				this.method_20072(lv);
			}

			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_351<E extends class_350.class_351<E>> implements class_364 {
		protected class_350<E> field_2144;

		protected void method_1904(float f) {
		}

		public abstract void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

		@Override
		public boolean isMouseOver(double d, double e) {
			return Objects.equals(this.field_2144.method_20055(d, e), this);
		}

		@Override
		public boolean isPartOfFocusCycle() {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	class class_352 extends AbstractList<E> {
		private final List<E> field_2146 = Lists.<E>newArrayList();

		private class_352() {
		}

		public E method_1912(int i) {
			return (E)this.field_2146.get(i);
		}

		public int size() {
			return this.field_2146.size();
		}

		public E method_1909(int i, E arg) {
			E lv = (E)this.field_2146.set(i, arg);
			arg.field_2144 = class_350.this;
			return lv;
		}

		public void method_1910(int i, E arg) {
			this.field_2146.add(i, arg);
			arg.field_2144 = class_350.this;
		}

		public E method_1911(int i) {
			return (E)this.field_2146.remove(i);
		}
	}
}
