package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_358 extends class_362 implements class_4068 {
	protected static final int NO_DRAG = -1;
	protected static final int DRAG_OUTSIDE = -2;
	protected final class_310 minecraft;
	protected int width;
	protected int height;
	protected int field_2166;
	protected int field_2165;
	protected int field_2181;
	protected int field_2180;
	protected final int itemHeight;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	protected double field_2175;
	protected boolean visible = true;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;

	public class_358(class_310 arg, int i, int j, int k, int l, int m) {
		this.minecraft = arg;
		this.width = i;
		this.height = j;
		this.field_2166 = k;
		this.field_2165 = l;
		this.itemHeight = m;
		this.field_2180 = 0;
		this.field_2181 = i;
	}

	public void updateSize(int i, int j, int k, int l) {
		this.width = i;
		this.height = j;
		this.field_2166 = k;
		this.field_2165 = l;
		this.field_2180 = 0;
		this.field_2181 = i;
	}

	public void setRenderSelection(boolean bl) {
		this.renderSelection = bl;
	}

	protected void setRenderHeader(boolean bl, int i) {
		this.renderHeader = bl;
		this.headerHeight = i;
		if (!bl) {
			this.headerHeight = 0;
		}
	}

	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	public boolean isVisible() {
		return this.visible;
	}

	protected abstract int getItemCount();

	@Override
	public List<? extends class_364> children() {
		return Collections.emptyList();
	}

	protected boolean selectItem(int i, int j, double d, double e) {
		return true;
	}

	protected abstract boolean isSelectedItem(int i);

	protected int getMaxPosition() {
		return this.getItemCount() * this.itemHeight + this.headerHeight;
	}

	protected abstract void renderBackground();

	protected void updateItemPosition(int i, int j, int k, float f) {
	}

	protected abstract void renderItem(int i, int j, int k, int l, int m, int n, float f);

	protected void renderHeader(int i, int j, class_289 arg) {
	}

	protected void clickedHeader(int i, int j) {
	}

	protected void renderDecorations(int i, int j) {
	}

	public int getItemAtPosition(double d, double e) {
		int i = this.field_2180 + this.width / 2 - this.getRowWidth() / 2;
		int j = this.field_2180 + this.width / 2 + this.getRowWidth() / 2;
		int k = class_3532.method_15357(e - (double)this.field_2166) - this.headerHeight + (int)this.field_2175 - 4;
		int l = k / this.itemHeight;
		return d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount() ? l : -1;
	}

	protected void capYPosition() {
		this.field_2175 = class_3532.method_15350(this.field_2175, 0.0, (double)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.field_2165 - this.field_2166 - 4));
	}

	public void centerScrollOn(int i) {
		this.field_2175 = (double)(i * this.itemHeight + this.itemHeight / 2 - (this.field_2165 - this.field_2166) / 2);
		this.capYPosition();
	}

	public int getScroll() {
		return (int)this.field_2175;
	}

	public boolean isMouseInList(double d, double e) {
		return e >= (double)this.field_2166 && e <= (double)this.field_2165 && d >= (double)this.field_2180 && d <= (double)this.field_2181;
	}

	public int getScrollBottom() {
		return (int)this.field_2175 - this.height - this.headerHeight;
	}

	public void scroll(int i) {
		this.field_2175 += (double)i;
		this.capYPosition();
		this.yDrag = -2;
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
			this.minecraft.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float g = 32.0F;
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)this.field_2180, (double)this.field_2165, 0.0)
				.method_1312((double)((float)this.field_2180 / 32.0F), (double)((float)(this.field_2165 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2165, 0.0)
				.method_1312((double)((float)this.field_2181 / 32.0F), (double)((float)(this.field_2165 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2166, 0.0)
				.method_1312((double)((float)this.field_2181 / 32.0F), (double)((float)(this.field_2166 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv2.method_1315((double)this.field_2180, (double)this.field_2166, 0.0)
				.method_1312((double)((float)this.field_2180 / 32.0F), (double)((float)(this.field_2166 + (int)this.field_2175) / 32.0F))
				.method_1323(32, 32, 32, 255)
				.method_1344();
			lv.method_1350();
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
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = 4;
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)this.field_2180, (double)(this.field_2166 + 4), 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
			lv2.method_1315((double)this.field_2181, (double)(this.field_2166 + 4), 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 0).method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2166, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)this.field_2180, (double)this.field_2166, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
			lv.method_1350();
			lv2.method_1328(7, class_290.field_1575);
			lv2.method_1315((double)this.field_2180, (double)this.field_2165, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)this.field_2181, (double)this.field_2165, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
			lv2.method_1315((double)this.field_2181, (double)(this.field_2165 - 4), 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
			lv2.method_1315((double)this.field_2180, (double)(this.field_2165 - 4), 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 0).method_1344();
			lv.method_1350();
			int p = this.getMaxScroll();
			if (p > 0) {
				int q = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.getMaxPosition());
				q = class_3532.method_15340(q, 32, this.field_2165 - this.field_2166 - 8);
				int r = (int)this.field_2175 * (this.field_2165 - this.field_2166 - q) / p + this.field_2166;
				if (r < this.field_2166) {
					r = this.field_2166;
				}

				lv2.method_1328(7, class_290.field_1575);
				lv2.method_1315((double)k, (double)this.field_2165, 0.0).method_1312(0.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)l, (double)this.field_2165, 0.0).method_1312(1.0, 1.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)l, (double)this.field_2166, 0.0).method_1312(1.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
				lv2.method_1315((double)k, (double)this.field_2166, 0.0).method_1312(0.0, 0.0).method_1323(0, 0, 0, 255).method_1344();
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

			this.renderDecorations(i, j);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	protected void updateScrollingState(double d, double e, int i) {
		this.scrolling = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.updateScrollingState(d, e, i);
		if (this.isVisible() && this.isMouseInList(d, e)) {
			int j = this.getItemAtPosition(d, e);
			if (j == -1 && i == 0) {
				this.clickedHeader(
					(int)(d - (double)(this.field_2180 + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.field_2166) + (int)this.field_2175 - 4
				);
				return true;
			} else if (j != -1 && this.selectItem(j, i, d, e)) {
				if (this.children().size() > j) {
					this.method_20084((class_364)this.children().get(j));
				}

				this.setDragging(true);
				return true;
			} else {
				return this.scrolling;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(d, e, i);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (super.mouseDragged(d, e, i, f, g)) {
			return true;
		} else if (this.isVisible() && i == 0 && this.scrolling) {
			if (e < (double)this.field_2166) {
				this.field_2175 = 0.0;
			} else if (e > (double)this.field_2165) {
				this.field_2175 = (double)this.getMaxScroll();
			} else {
				double h = (double)this.getMaxScroll();
				if (h < 1.0) {
					h = 1.0;
				}

				int j = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.getMaxPosition());
				j = class_3532.method_15340(j, 32, this.field_2165 - this.field_2166 - 8);
				double k = h / (double)(this.field_2165 - this.field_2166 - j);
				if (k < 1.0) {
					k = 1.0;
				}

				this.field_2175 += g * k;
				this.capYPosition();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if (!this.isVisible()) {
			return false;
		} else {
			this.field_2175 = this.field_2175 - f * (double)this.itemHeight / 2.0;
			return true;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (!this.isVisible()) {
			return false;
		} else if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i == 264) {
			this.moveSelection(1);
			return true;
		} else if (i == 265) {
			this.moveSelection(-1);
			return true;
		} else {
			return false;
		}
	}

	protected void moveSelection(int i) {
	}

	@Override
	public boolean charTyped(char c, int i) {
		return !this.isVisible() ? false : super.charTyped(c, i);
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return this.isMouseInList(d, e);
	}

	public int getRowWidth() {
		return 220;
	}

	protected void renderList(int i, int j, int k, int l, float f) {
		int m = this.getItemCount();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.itemHeight + this.headerHeight;
			int p = this.itemHeight - 4;
			if (o > this.field_2165 || o + p < this.field_2166) {
				this.updateItemPosition(n, i, o, f);
			}

			if (this.renderSelection && this.isSelectedItem(n)) {
				int q = this.field_2180 + this.width / 2 - this.getRowWidth() / 2;
				int r = this.field_2180 + this.width / 2 + this.getRowWidth() / 2;
				GlStateManager.disableTexture();
				float g = this.isFocused() ? 1.0F : 0.5F;
				GlStateManager.color4f(g, g, g, 1.0F);
				lv2.method_1328(7, class_290.field_1592);
				lv2.method_1315((double)q, (double)(o + p + 2), 0.0).method_1344();
				lv2.method_1315((double)r, (double)(o + p + 2), 0.0).method_1344();
				lv2.method_1315((double)r, (double)(o - 2), 0.0).method_1344();
				lv2.method_1315((double)q, (double)(o - 2), 0.0).method_1344();
				lv.method_1350();
				GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				lv2.method_1328(7, class_290.field_1592);
				lv2.method_1315((double)(q + 1), (double)(o + p + 1), 0.0).method_1344();
				lv2.method_1315((double)(r - 1), (double)(o + p + 1), 0.0).method_1344();
				lv2.method_1315((double)(r - 1), (double)(o - 1), 0.0).method_1344();
				lv2.method_1315((double)(q + 1), (double)(o - 1), 0.0).method_1344();
				lv.method_1350();
				GlStateManager.enableTexture();
			}

			this.renderItem(n, i, o, p, k, l, f);
		}
	}

	protected boolean isFocused() {
		return false;
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	protected void renderHoleBackground(int i, int j, int k, int l) {
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		this.minecraft.method_1531().method_4618(class_332.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		lv2.method_1328(7, class_290.field_1575);
		lv2.method_1315((double)this.field_2180, (double)j, 0.0).method_1312(0.0, (double)((float)j / 32.0F)).method_1323(64, 64, 64, l).method_1344();
		lv2.method_1315((double)(this.field_2180 + this.width), (double)j, 0.0)
			.method_1312((double)((float)this.width / 32.0F), (double)((float)j / 32.0F))
			.method_1323(64, 64, 64, l)
			.method_1344();
		lv2.method_1315((double)(this.field_2180 + this.width), (double)i, 0.0)
			.method_1312((double)((float)this.width / 32.0F), (double)((float)i / 32.0F))
			.method_1323(64, 64, 64, k)
			.method_1344();
		lv2.method_1315((double)this.field_2180, (double)i, 0.0).method_1312(0.0, (double)((float)i / 32.0F)).method_1323(64, 64, 64, k).method_1344();
		lv.method_1350();
	}

	public void setLeftPos(int i) {
		this.field_2180 = i;
		this.field_2181 = i + this.width;
	}

	public int getItemHeight() {
		return this.itemHeight;
	}
}
