package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ListWidget extends AbstractParentElement implements Drawable {
	protected static final int NO_DRAG = -1;
	protected static final int DRAG_OUTSIDE = -2;
	protected final MinecraftClient minecraft;
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected final int itemHeight;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	protected double scroll;
	protected boolean visible = true;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;

	public ListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		this.minecraft = minecraftClient;
		this.width = i;
		this.height = j;
		this.top = k;
		this.bottom = l;
		this.itemHeight = m;
		this.left = 0;
		this.right = i;
	}

	public void updateSize(int i, int j, int k, int l) {
		this.width = i;
		this.height = j;
		this.top = k;
		this.bottom = l;
		this.left = 0;
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

	protected abstract int getItemCount();

	@Override
	public List<? extends Element> children() {
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

	protected void renderHeader(int i, int j, Tessellator tessellator) {
	}

	protected void clickedHeader(int i, int j) {
	}

	protected void renderDecorations(int i, int j) {
	}

	public int getItemAtPosition(double d, double e) {
		int i = this.left + this.width / 2 - this.getRowWidth() / 2;
		int j = this.left + this.width / 2 + this.getRowWidth() / 2;
		int k = MathHelper.floor(e - (double)this.top) - this.headerHeight + (int)this.scroll - 4;
		int l = k / this.itemHeight;
		return d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount() ? l : -1;
	}

	protected void capYPosition() {
		this.scroll = MathHelper.clamp(this.scroll, 0.0, (double)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	public void centerScrollOn(int i) {
		this.scroll = (double)(i * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2);
		this.capYPosition();
	}

	public int getScroll() {
		return (int)this.scroll;
	}

	public boolean isMouseInList(double d, double e) {
		return e >= (double)this.top && e <= (double)this.bottom && d >= (double)this.left && d <= (double)this.right;
	}

	public int getScrollBottom() {
		return (int)this.scroll - this.height - this.headerHeight;
	}

	public void scroll(int i) {
		this.scroll += (double)i;
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
			RenderSystem.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float g = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0)
				.texture((float)this.left / 32.0F, (float)(this.bottom + (int)this.scroll) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0)
				.texture((float)this.right / 32.0F, (float)(this.bottom + (int)this.scroll) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.top, 0.0)
				.texture((float)this.right / 32.0F, (float)(this.top + (int)this.scroll) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.left, (double)this.top, 0.0)
				.texture((float)this.left / 32.0F, (float)(this.top + (int)this.scroll) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int m = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
			int n = this.top + 4 - (int)this.scroll;
			if (this.renderHeader) {
				this.renderHeader(m, n, tessellator);
			}

			this.renderList(m, n, i, j, f);
			RenderSystem.disableDepthTest();
			this.renderHoleBackground(0, this.top, 255, 255);
			this.renderHoleBackground(this.bottom, this.height, 255, 255);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
			);
			RenderSystem.disableAlphaTest();
			RenderSystem.shadeModel(7425);
			RenderSystem.disableTexture();
			int o = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.left, (double)(this.top + 4), 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)(this.top + 4), 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.left, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)(this.bottom - 4), 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.left, (double)(this.bottom - 4), 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 0).next();
			tessellator.draw();
			int p = this.getMaxScroll();
			if (p > 0) {
				int q = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
				q = MathHelper.clamp(q, 32, this.bottom - this.top - 8);
				int r = (int)this.scroll * (this.bottom - this.top - q) / p + this.top;
				if (r < this.top) {
					r = this.top;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(r + q), 0.0).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)(r + q), 0.0).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)r, 0.0).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)k, (double)r, 0.0).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(r + q - 1), 0.0).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)(r + q - 1), 0.0).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)r, 0.0).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)k, (double)r, 0.0).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
				tessellator.draw();
			}

			this.renderDecorations(i, j);
			RenderSystem.enableTexture();
			RenderSystem.shadeModel(7424);
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
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
				this.clickedHeader((int)(d - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.top) + (int)this.scroll - 4);
				return true;
			} else if (j != -1 && this.selectItem(j, i, d, e)) {
				if (this.children().size() > j) {
					this.setFocused((Element)this.children().get(j));
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
			if (e < (double)this.top) {
				this.scroll = 0.0;
			} else if (e > (double)this.bottom) {
				this.scroll = (double)this.getMaxScroll();
			} else {
				double h = (double)this.getMaxScroll();
				if (h < 1.0) {
					h = 1.0;
				}

				int j = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
				j = MathHelper.clamp(j, 32, this.bottom - this.top - 8);
				double k = h / (double)(this.bottom - this.top - j);
				if (k < 1.0) {
					k = 1.0;
				}

				this.scroll += g * k;
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
			this.scroll = this.scroll - f * (double)this.itemHeight / 2.0;
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.itemHeight + this.headerHeight;
			int p = this.itemHeight - 4;
			if (o > this.bottom || o + p < this.top) {
				this.updateItemPosition(n, i, o, f);
			}

			if (this.renderSelection && this.isSelectedItem(n)) {
				int q = this.left + this.width / 2 - this.getRowWidth() / 2;
				int r = this.left + this.width / 2 + this.getRowWidth() / 2;
				RenderSystem.disableTexture();
				float g = this.isFocused() ? 1.0F : 0.5F;
				RenderSystem.color4f(g, g, g, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)q, (double)(o + p + 2), 0.0).next();
				bufferBuilder.vertex((double)r, (double)(o + p + 2), 0.0).next();
				bufferBuilder.vertex((double)r, (double)(o - 2), 0.0).next();
				bufferBuilder.vertex((double)q, (double)(o - 2), 0.0).next();
				tessellator.draw();
				RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)(q + 1), (double)(o + p + 1), 0.0).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o + p + 1), 0.0).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o - 1), 0.0).next();
				bufferBuilder.vertex((double)(q + 1), (double)(o - 1), 0.0).next();
				tessellator.draw();
				RenderSystem.enableTexture();
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.left, (double)j, 0.0).texture(0.0F, (float)j / 32.0F).color(64, 64, 64, l).next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)j, 0.0).texture((float)this.width / 32.0F, (float)j / 32.0F).color(64, 64, 64, l).next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)i, 0.0).texture((float)this.width / 32.0F, (float)i / 32.0F).color(64, 64, 64, k).next();
		bufferBuilder.vertex((double)this.left, (double)i, 0.0).texture(0.0F, (float)i / 32.0F).color(64, 64, 64, k).next();
		tessellator.draw();
	}

	public void setLeftPos(int i) {
		this.left = i;
		this.right = i + this.width;
	}

	public int getItemHeight() {
		return this.itemHeight;
	}
}
