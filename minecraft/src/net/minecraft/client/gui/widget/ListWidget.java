package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
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

	public ListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		this.minecraft = minecraftClient;
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
		int i = this.field_2180 + this.width / 2 - this.getRowWidth() / 2;
		int j = this.field_2180 + this.width / 2 + this.getRowWidth() / 2;
		int k = MathHelper.floor(e - (double)this.field_2166) - this.headerHeight + (int)this.field_2175 - 4;
		int l = k / this.itemHeight;
		return d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount() ? l : -1;
	}

	protected void capYPosition() {
		this.field_2175 = MathHelper.clamp(this.field_2175, 0.0, (double)this.getMaxScroll());
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
			RenderSystem.disableLighting();
			RenderSystem.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float g = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.field_2180, (double)this.field_2165, 0.0)
				.texture((double)((float)this.field_2180 / 32.0F), (double)((float)(this.field_2165 + (int)this.field_2175) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.field_2181, (double)this.field_2165, 0.0)
				.texture((double)((float)this.field_2181 / 32.0F), (double)((float)(this.field_2165 + (int)this.field_2175) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.field_2181, (double)this.field_2166, 0.0)
				.texture((double)((float)this.field_2181 / 32.0F), (double)((float)(this.field_2166 + (int)this.field_2175) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.field_2180, (double)this.field_2166, 0.0)
				.texture((double)((float)this.field_2180 / 32.0F), (double)((float)(this.field_2166 + (int)this.field_2175) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int m = this.field_2180 + this.width / 2 - this.getRowWidth() / 2 + 2;
			int n = this.field_2166 + 4 - (int)this.field_2175;
			if (this.renderHeader) {
				this.renderHeader(m, n, tessellator);
			}

			this.renderList(m, n, i, j, f);
			RenderSystem.disableDepthTest();
			this.renderHoleBackground(0, this.field_2166, 255, 255);
			this.renderHoleBackground(this.field_2165, this.height, 255, 255);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ZERO, class_4493.class_4534.ONE
			);
			RenderSystem.disableAlphaTest();
			RenderSystem.shadeModel(7425);
			RenderSystem.disableTexture();
			int o = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.field_2180, (double)(this.field_2166 + 4), 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.field_2181, (double)(this.field_2166 + 4), 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.field_2181, (double)this.field_2166, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.field_2180, (double)this.field_2166, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.field_2180, (double)this.field_2165, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.field_2181, (double)this.field_2165, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.field_2181, (double)(this.field_2165 - 4), 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.field_2180, (double)(this.field_2165 - 4), 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
			int p = this.getMaxScroll();
			if (p > 0) {
				int q = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.getMaxPosition());
				q = MathHelper.clamp(q, 32, this.field_2165 - this.field_2166 - 8);
				int r = (int)this.field_2175 * (this.field_2165 - this.field_2166 - q) / p + this.field_2166;
				if (r < this.field_2166) {
					r = this.field_2166;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.field_2165, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.field_2165, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.field_2166, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.field_2166, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
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
				this.clickedHeader(
					(int)(d - (double)(this.field_2180 + this.width / 2 - this.getRowWidth() / 2)), (int)(e - (double)this.field_2166) + (int)this.field_2175 - 4
				);
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
				j = MathHelper.clamp(j, 32, this.field_2165 - this.field_2166 - 8);
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.itemHeight + this.headerHeight;
			int p = this.itemHeight - 4;
			if (o > this.field_2165 || o + p < this.field_2166) {
				this.updateItemPosition(n, i, o, f);
			}

			if (this.renderSelection && this.isSelectedItem(n)) {
				int q = this.field_2180 + this.width / 2 - this.getRowWidth() / 2;
				int r = this.field_2180 + this.width / 2 + this.getRowWidth() / 2;
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
		bufferBuilder.vertex((double)this.field_2180, (double)j, 0.0).texture(0.0, (double)((float)j / 32.0F)).color(64, 64, 64, l).next();
		bufferBuilder.vertex((double)(this.field_2180 + this.width), (double)j, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)j / 32.0F))
			.color(64, 64, 64, l)
			.next();
		bufferBuilder.vertex((double)(this.field_2180 + this.width), (double)i, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)i / 32.0F))
			.color(64, 64, 64, k)
			.next();
		bufferBuilder.vertex((double)this.field_2180, (double)i, 0.0).texture(0.0, (double)((float)i / 32.0F)).color(64, 64, 64, k).next();
		tessellator.draw();
	}

	public void setLeftPos(int i) {
		this.field_2180 = i;
		this.field_2181 = i + this.width;
	}

	public int getItemHeight() {
		return this.itemHeight;
	}
}
