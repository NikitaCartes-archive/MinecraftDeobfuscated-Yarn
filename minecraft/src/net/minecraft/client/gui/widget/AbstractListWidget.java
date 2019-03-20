package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.ScreenComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractListWidget extends ScreenComponent implements Drawable {
	protected static final int NO_DRAG = -1;
	protected static final int DRAG_OUTSIDE = -2;
	protected final MinecraftClient client;
	protected int width;
	protected int height;
	protected int y;
	protected int bottom;
	protected int right;
	protected int x;
	protected final int entryHeight;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	protected double scrollY;
	protected boolean visible = true;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;

	public AbstractListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		this.client = minecraftClient;
		this.width = i;
		this.height = j;
		this.y = k;
		this.bottom = l;
		this.entryHeight = m;
		this.x = 0;
		this.right = i;
	}

	public void updateSize(int i, int j, int k, int l) {
		this.width = i;
		this.height = j;
		this.y = k;
		this.bottom = l;
		this.x = 0;
		this.right = i;
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

	protected abstract int getEntryCount();

	@Override
	public List<? extends InputListener> getInputListeners() {
		return Collections.emptyList();
	}

	protected boolean selectEntry(int i, int j, double d, double e) {
		return true;
	}

	protected abstract boolean isSelectedEntry(int i);

	protected int getMaxScrollPosition() {
		return this.getEntryCount() * this.entryHeight + this.headerHeight;
	}

	protected abstract void drawBackground();

	protected void updateItemPosition(int i, int j, int k, float f) {
	}

	protected abstract void drawEntry(int i, int j, int k, int l, int m, int n, float f);

	protected void renderHeader(int i, int j, Tessellator tessellator) {
	}

	protected void clickedHeader(int i, int j) {
	}

	protected void renderDecorations(int i, int j) {
	}

	public int getSelectedEntry(double d, double e) {
		int i = this.x + this.width / 2 - this.getEntryWidth() / 2;
		int j = this.x + this.width / 2 + this.getEntryWidth() / 2;
		int k = MathHelper.floor(e - (double)this.y) - this.headerHeight + (int)this.scrollY - 4;
		int l = k / this.entryHeight;
		return d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount() ? l : -1;
	}

	protected void clampScrollY() {
		this.scrollY = MathHelper.clamp(this.scrollY, 0.0, (double)this.getMaxScrollY());
	}

	public int getMaxScrollY() {
		return Math.max(0, this.getMaxScrollPosition() - (this.bottom - this.y - 4));
	}

	public void centerScrollOn(int i) {
		this.scrollY = (double)(i * this.entryHeight + this.entryHeight / 2 - (this.bottom - this.y) / 2);
		this.clampScrollY();
	}

	public int getScrollY() {
		return (int)this.scrollY;
	}

	public boolean isSelected(double d, double e) {
		return e >= (double)this.y && e <= (double)this.bottom && d >= (double)this.x && d <= (double)this.right;
	}

	public int getScrollBottom() {
		return (int)this.scrollY - this.height - this.headerHeight;
	}

	public void scroll(int i) {
		this.scrollY += (double)i;
		this.clampScrollY();
		this.yDrag = -2;
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.visible) {
			this.drawBackground();
			int k = this.getScrollbarPosition();
			int l = k + 6;
			this.clampScrollY();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BG);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float g = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.x, (double)this.bottom, 0.0)
				.texture((double)((float)this.x / 32.0F), (double)((float)(this.bottom + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0)
				.texture((double)((float)this.right / 32.0F), (double)((float)(this.bottom + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.y, 0.0)
				.texture((double)((float)this.right / 32.0F), (double)((float)(this.y + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.x, (double)this.y, 0.0)
				.texture((double)((float)this.x / 32.0F), (double)((float)(this.y + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int m = this.x + this.width / 2 - this.getEntryWidth() / 2 + 2;
			int n = this.y + 4 - (int)this.scrollY;
			if (this.renderHeader) {
				this.renderHeader(m, n, tessellator);
			}

			this.drawEntries(m, n, i, j, f);
			GlStateManager.disableDepthTest();
			this.renderCoverBackground(0, this.y, 255, 255);
			this.renderCoverBackground(this.bottom, this.height, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.x, (double)(this.y + 4), 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)(this.y + 4), 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)this.y, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.x, (double)this.y, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.x, (double)this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)(this.bottom - 4), 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.x, (double)(this.bottom - 4), 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
			int p = this.getMaxScrollY();
			if (p > 0) {
				int q = (int)((float)((this.bottom - this.y) * (this.bottom - this.y)) / (float)this.getMaxScrollPosition());
				q = MathHelper.clamp(q, 32, this.bottom - this.y - 8);
				int r = (int)this.scrollY * (this.bottom - this.y - q) / p + this.y;
				if (r < this.y) {
					r = this.y;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.y, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.y, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(r + q), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)(r + q), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)r, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)k, (double)r, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(r + q - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)(r + q - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)r, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)k, (double)r, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
				tessellator.draw();
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
		if (this.isVisible() && this.isSelected(d, e)) {
			int j = this.getSelectedEntry(d, e);
			if (j == -1 && i == 0) {
				this.clickedHeader((int)(d - (double)(this.x + this.width / 2 - this.getEntryWidth() / 2)), (int)(e - (double)this.y) + (int)this.scrollY - 4);
				return true;
			} else if (j != -1 && this.selectEntry(j, i, d, e)) {
				if (this.getInputListeners().size() > j) {
					this.focusOn((InputListener)this.getInputListeners().get(j));
				}

				this.setActive(true);
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
			if (e < (double)this.y) {
				this.scrollY = 0.0;
			} else if (e > (double)this.bottom) {
				this.scrollY = (double)this.getMaxScrollY();
			} else {
				double h = (double)this.getMaxScrollY();
				if (h < 1.0) {
					h = 1.0;
				}

				int j = (int)((float)((this.bottom - this.y) * (this.bottom - this.y)) / (float)this.getMaxScrollPosition());
				j = MathHelper.clamp(j, 32, this.bottom - this.y - 8);
				double k = h / (double)(this.bottom - this.y - j);
				if (k < 1.0) {
					k = 1.0;
				}

				this.scrollY += g * k;
				this.clampScrollY();
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
			this.scrollY = this.scrollY - f * (double)this.entryHeight / 2.0;
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
		return this.isSelected(d, e);
	}

	public int getEntryWidth() {
		return 220;
	}

	protected void drawEntries(int i, int j, int k, int l, float f) {
		int m = this.getEntryCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.entryHeight + this.headerHeight;
			int p = this.entryHeight - 4;
			if (o > this.bottom || o + p < this.y) {
				this.updateItemPosition(n, i, o, f);
			}

			if (this.renderSelection && this.isSelectedEntry(n)) {
				int q = this.x + this.width / 2 - this.getEntryWidth() / 2;
				int r = this.x + this.width / 2 + this.getEntryWidth() / 2;
				GlStateManager.disableTexture();
				float g = this.isFocused() ? 1.0F : 0.5F;
				GlStateManager.color4f(g, g, g, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)q, (double)(o + p + 2), 0.0).next();
				bufferBuilder.vertex((double)r, (double)(o + p + 2), 0.0).next();
				bufferBuilder.vertex((double)r, (double)(o - 2), 0.0).next();
				bufferBuilder.vertex((double)q, (double)(o - 2), 0.0).next();
				tessellator.draw();
				GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)(q + 1), (double)(o + p + 1), 0.0).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o + p + 1), 0.0).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o - 1), 0.0).next();
				bufferBuilder.vertex((double)(q + 1), (double)(o - 1), 0.0).next();
				tessellator.draw();
				GlStateManager.enableTexture();
			}

			this.drawEntry(n, i, o, p, k, l, f);
		}
	}

	protected boolean isFocused() {
		return false;
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	protected void renderCoverBackground(int i, int j, int k, int l) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BG);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.x, (double)j, 0.0).texture(0.0, (double)((float)j / 32.0F)).color(64, 64, 64, l).next();
		bufferBuilder.vertex((double)(this.x + this.width), (double)j, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)j / 32.0F))
			.color(64, 64, 64, l)
			.next();
		bufferBuilder.vertex((double)(this.x + this.width), (double)i, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)i / 32.0F))
			.color(64, 64, 64, k)
			.next();
		bufferBuilder.vertex((double)this.x, (double)i, 0.0).texture(0.0, (double)((float)i / 32.0F)).color(64, 64, 64, k).next();
		tessellator.draw();
	}

	public void setX(int i) {
		this.x = i;
		this.right = i + this.width;
	}

	public int getEntryHeight() {
		return this.entryHeight;
	}
}
