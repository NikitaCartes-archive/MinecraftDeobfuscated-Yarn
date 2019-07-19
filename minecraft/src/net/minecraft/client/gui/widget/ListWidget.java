package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
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

	public ListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		this.minecraft = client;
		this.width = width;
		this.height = height;
		this.field_2166 = top;
		this.field_2165 = bottom;
		this.itemHeight = itemHeight;
		this.field_2180 = 0;
		this.field_2181 = width;
	}

	public void updateSize(int width, int height, int y, int bottom) {
		this.width = width;
		this.height = height;
		this.field_2166 = y;
		this.field_2165 = bottom;
		this.field_2180 = 0;
		this.field_2181 = width;
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

	protected boolean selectItem(int index, int button, double mouseX, double mouseY) {
		return true;
	}

	protected abstract boolean isSelectedItem(int index);

	protected int getMaxPosition() {
		return this.getItemCount() * this.itemHeight + this.headerHeight;
	}

	protected abstract void renderBackground();

	protected void updateItemPosition(int index, int i, int j, float f) {
	}

	protected abstract void renderItem(int index, int y, int i, int j, int k, int l, float f);

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

	public boolean isMouseInList(double mouseX, double mouseY) {
		return mouseY >= (double)this.field_2166 && mouseY <= (double)this.field_2165 && mouseX >= (double)this.field_2180 && mouseX <= (double)this.field_2181;
	}

	public int getScrollBottom() {
		return (int)this.field_2175 - this.height - this.headerHeight;
	}

	public void scroll(int amount) {
		this.field_2175 += (double)amount;
		this.capYPosition();
		this.yDrag = -2;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.renderBackground();
			int i = this.getScrollbarPosition();
			int j = i + 6;
			this.capYPosition();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float f = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
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
			int k = this.field_2180 + this.width / 2 - this.getRowWidth() / 2 + 2;
			int l = this.field_2166 + 4 - (int)this.field_2175;
			if (this.renderHeader) {
				this.renderHeader(k, l, tessellator);
			}

			this.renderList(k, l, mouseX, mouseY, delta);
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
			int m = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.field_2180, (double)(this.field_2166 + 4), 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.field_2181, (double)(this.field_2166 + 4), 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.field_2181, (double)this.field_2166, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.field_2180, (double)this.field_2166, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.field_2180, (double)this.field_2165, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.field_2181, (double)this.field_2165, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.field_2181, (double)(this.field_2165 - 4), 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.field_2180, (double)(this.field_2165 - 4), 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
			int n = this.getMaxScroll();
			if (n > 0) {
				int o = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.getMaxPosition());
				o = MathHelper.clamp(o, 32, this.field_2165 - this.field_2166 - 8);
				int p = (int)this.field_2175 * (this.field_2165 - this.field_2166 - o) / n + this.field_2166;
				if (p < this.field_2166) {
					p = this.field_2166;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)this.field_2165, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.field_2165, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.field_2166, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)i, (double)this.field_2166, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(p + o), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)(p + o), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)p, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(p + o - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)(p + o - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)p, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
				tessellator.draw();
			}

			this.renderDecorations(mouseX, mouseY);
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (this.isVisible() && this.isMouseInList(mouseX, mouseY)) {
			int i = this.getItemAtPosition(mouseX, mouseY);
			if (i == -1 && button == 0) {
				this.clickedHeader(
					(int)(mouseX - (double)(this.field_2180 + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.field_2166) + (int)this.field_2175 - 4
				);
				return true;
			} else if (i != -1 && this.selectItem(i, button, mouseX, mouseY)) {
				if (this.children().size() > i) {
					this.setFocused((Element)this.children().get(i));
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
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(mouseX, mouseY, button);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (this.isVisible() && button == 0 && this.scrolling) {
			if (mouseY < (double)this.field_2166) {
				this.field_2175 = 0.0;
			} else if (mouseY > (double)this.field_2165) {
				this.field_2175 = (double)this.getMaxScroll();
			} else {
				double d = (double)this.getMaxScroll();
				if (d < 1.0) {
					d = 1.0;
				}

				int i = (int)((float)((this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166)) / (float)this.getMaxPosition());
				i = MathHelper.clamp(i, 32, this.field_2165 - this.field_2166 - 8);
				double e = d / (double)(this.field_2165 - this.field_2166 - i);
				if (e < 1.0) {
					e = 1.0;
				}

				this.field_2175 += deltaY * e;
				this.capYPosition();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		if (!this.isVisible()) {
			return false;
		} else {
			this.field_2175 = this.field_2175 - amount * (double)this.itemHeight / 2.0;
			return true;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.isVisible()) {
			return false;
		} else if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == 264) {
			this.moveSelection(1);
			return true;
		} else if (keyCode == 265) {
			this.moveSelection(-1);
			return true;
		} else {
			return false;
		}
	}

	protected void moveSelection(int i) {
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return !this.isVisible() ? false : super.charTyped(chr, keyCode);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.isMouseInList(mouseX, mouseY);
	}

	public int getRowWidth() {
		return 220;
	}

	protected void renderList(int x, int y, int mouseX, int mouseY, float f) {
		int i = this.getItemCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int j = 0; j < i; j++) {
			int k = y + j * this.itemHeight + this.headerHeight;
			int l = this.itemHeight - 4;
			if (k > this.field_2165 || k + l < this.field_2166) {
				this.updateItemPosition(j, x, k, f);
			}

			if (this.renderSelection && this.isSelectedItem(j)) {
				int m = this.field_2180 + this.width / 2 - this.getRowWidth() / 2;
				int n = this.field_2180 + this.width / 2 + this.getRowWidth() / 2;
				GlStateManager.disableTexture();
				float g = this.isFocused() ? 1.0F : 0.5F;
				GlStateManager.color4f(g, g, g, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)m, (double)(k + l + 2), 0.0).next();
				bufferBuilder.vertex((double)n, (double)(k + l + 2), 0.0).next();
				bufferBuilder.vertex((double)n, (double)(k - 2), 0.0).next();
				bufferBuilder.vertex((double)m, (double)(k - 2), 0.0).next();
				tessellator.draw();
				GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex((double)(m + 1), (double)(k + l + 1), 0.0).next();
				bufferBuilder.vertex((double)(n - 1), (double)(k + l + 1), 0.0).next();
				bufferBuilder.vertex((double)(n - 1), (double)(k - 1), 0.0).next();
				bufferBuilder.vertex((double)(m + 1), (double)(k - 1), 0.0).next();
				tessellator.draw();
				GlStateManager.enableTexture();
			}

			this.renderItem(j, x, k, l, mouseX, mouseY, f);
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
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
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

	public void setLeftPos(int x) {
		this.field_2180 = x;
		this.field_2181 = x + this.width;
	}

	public int getItemHeight() {
		return this.itemHeight;
	}
}
