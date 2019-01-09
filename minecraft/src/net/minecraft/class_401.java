package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;

@Environment(EnvType.CLIENT)
public class class_401 extends class_358 {
	private final RealmsSimpleScrolledSelectionList field_2343;

	public class_401(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(class_310.method_1551(), i, j, k, l, m);
		this.field_2343 = realmsSimpleScrolledSelectionList;
	}

	@Override
	protected int method_1947() {
		return this.field_2343.getItemCount();
	}

	@Override
	protected boolean method_1937(int i, int j, double d, double e) {
		return this.field_2343.selectItem(i, j, d, e);
	}

	@Override
	protected boolean method_1955(int i) {
		return this.field_2343.isSelectedItem(i);
	}

	@Override
	protected void method_1936() {
		this.field_2343.renderBackground();
	}

	@Override
	protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
		this.field_2343.renderItem(i, j, k, l, m, n);
	}

	public int method_2093() {
		return this.field_2168;
	}

	@Override
	protected int method_1928() {
		return this.field_2343.getMaxPosition();
	}

	@Override
	protected int method_1948() {
		return this.field_2343.getScrollbarPosition();
	}

	@Override
	public void method_1930(int i, int j, float f) {
		if (this.field_2172) {
			this.method_1936();
			int k = this.method_1948();
			int l = k + 6;
			this.method_1934();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			int m = this.field_2180 + this.field_2168 / 2 - this.method_1932() / 2 + 2;
			int n = this.field_2166 + 4 - (int)this.field_2175;
			if (this.field_2170) {
				this.method_1940(m, n, lv);
			}

			this.method_1950(m, n, i, j, f);
			GlStateManager.disableDepthTest();
			this.method_1954(0, this.field_2166, 255, 255);
			this.method_1954(this.field_2165, this.field_2167, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5146, GlStateManager.class_1027.field_5078
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = this.method_1931();
			if (o > 0) {
				int p = (this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166) / this.method_1928();
				p = class_3532.method_15340(p, 32, this.field_2165 - this.field_2166 - 8);
				int q = (int)this.field_2175 * (this.field_2165 - this.field_2166 - p) / o + this.field_2166;
				if (q < this.field_2166) {
					q = this.field_2166;
				}

				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)this.field_2165, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)l, (double)this.field_2165, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)l, (double)this.field_2166, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)k, (double)this.field_2166, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv.method_1350();
				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)(q + p), 0.0).method_1312(0.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)l, (double)(q + p), 0.0).method_1312(1.0, 1.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)l, (double)q, 0.0).method_1312(1.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
				lv2.method_1315((double)k, (double)q, 0.0).method_1312(0.0, 0.0).method_1323(128, 128, 128, 255).method_1344();
				lv.method_1350();
				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)(q + p - 1), 0.0).method_1312(0.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
				lv2.method_1315((double)(l - 1), (double)(q + p - 1), 0.0).method_1312(1.0, 1.0).method_1323(192, 192, 192, 255).method_1344();
				lv2.method_1315((double)(l - 1), (double)q, 0.0).method_1312(1.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
				lv2.method_1315((double)k, (double)q, 0.0).method_1312(0.0, 0.0).method_1323(192, 192, 192, 255).method_1344();
				lv.method_1350();
			}

			this.method_1942(i, j);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public boolean method_16802(double d) {
		return this.field_2343.mouseScrolled(d) ? true : super.method_16802(d);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		return this.field_2343.mouseClicked(d, e, i) ? true : super.method_16807(d, e, i);
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		return this.field_2343.mouseReleased(d, e, i);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		return this.field_2343.mouseDragged(d, e, i, f, g);
	}
}
