package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_358;

@Environment(EnvType.CLIENT)
public class RealmsSimpleScrolledSelectionListProxy extends class_358 {
	private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;

	public RealmsSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(class_310.method_1551(), i, j, k, l, m);
		this.realmsSimpleScrolledSelectionList = realmsSimpleScrolledSelectionList;
	}

	@Override
	public int getItemCount() {
		return this.realmsSimpleScrolledSelectionList.getItemCount();
	}

	@Override
	public boolean selectItem(int i, int j, double d, double e) {
		return this.realmsSimpleScrolledSelectionList.selectItem(i, j, d, e);
	}

	@Override
	public boolean isSelectedItem(int i) {
		return this.realmsSimpleScrolledSelectionList.isSelectedItem(i);
	}

	@Override
	public void renderBackground() {
		this.realmsSimpleScrolledSelectionList.renderBackground();
	}

	@Override
	public void renderItem(int i, int j, int k, int l, int m, int n, float f) {
		this.realmsSimpleScrolledSelectionList.renderItem(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxPosition() {
		return this.realmsSimpleScrolledSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsSimpleScrolledSelectionList.getScrollbarPosition();
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.visible) {
			this.renderBackground();
			int k = this.getScrollbarPosition();
			int l = k + 6;
			this.capYPosition();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			int m = this.field_2180 + this.width / 2 - this.getRowWidth() / 2 + 2;
			int n = this.field_2166 + 4 - (int)this.field_2175;
			if (this.renderHeader) {
				this.renderHeader(m, n, lv);
			}

			this.renderList(m, n, i, j, f);
			GlStateManager.disableDepthTest();
			this.renderHoleBackground(0, this.field_2166, 255, 255);
			this.renderHoleBackground(this.field_2165, this.height, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_5119.SRC_ALPHA, GlStateManager.class_5118.ONE_MINUS_SRC_ALPHA, GlStateManager.class_5119.ZERO, GlStateManager.class_5118.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = this.getMaxScroll();
			if (o > 0) {
				int p = (this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166) / this.getMaxPosition();
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

			this.renderDecorations(i, j);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.realmsSimpleScrolledSelectionList.mouseScrolled(d, e, f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsSimpleScrolledSelectionList.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.realmsSimpleScrolledSelectionList.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.realmsSimpleScrolledSelectionList.mouseDragged(d, e, i, f, g);
	}
}
