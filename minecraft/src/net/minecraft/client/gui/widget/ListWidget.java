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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ListWidget extends AbstractParentElement implements Drawable {
	protected final MinecraftClient client;
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected final int itemHeight;
	protected boolean centerListVertically = true;
	protected int yDrag = -2;
	protected double scrollAmount;
	protected boolean visible = true;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;

	public ListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.itemHeight = itemHeight;
		this.left = 0;
		this.right = width;
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

	protected void updateItemPosition(int index, int x, int y, float delta) {
	}

	protected abstract void renderItem(MatrixStack matrixStack, int x, int y, int itemHeight, int mouseX, int mouseY, int i, float f);

	protected void renderHeader(int x, int y, Tessellator tessellator) {
	}

	protected void clickedHeader(int i, int j) {
	}

	protected void renderDecorations(int mouseX, int mouseY) {
	}

	public int getItemAtPosition(double mouseX, double mouseY) {
		int i = this.left + this.width / 2 - this.getRowWidth() / 2;
		int j = this.left + this.width / 2 + this.getRowWidth() / 2;
		int k = MathHelper.floor(mouseY - (double)this.top) - this.headerHeight + (int)this.scrollAmount - 4;
		int l = k / this.itemHeight;
		return mouseX < (double)this.getScrollbarPosition() && mouseX >= (double)i && mouseX <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount() ? l : -1;
	}

	protected void capYPosition() {
		this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0, (double)this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	public boolean isMouseInList(double mouseX, double mouseY) {
		return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.renderBackground();
			int i = this.getScrollbarPosition();
			int j = i + 6;
			this.capYPosition();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float f = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0)
				.texture((float)this.left / 32.0F, (float)(this.bottom + (int)this.scrollAmount) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0)
				.texture((float)this.right / 32.0F, (float)(this.bottom + (int)this.scrollAmount) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.right, (double)this.top, 0.0)
				.texture((float)this.right / 32.0F, (float)(this.top + (int)this.scrollAmount) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.left, (double)this.top, 0.0)
				.texture((float)this.left / 32.0F, (float)(this.top + (int)this.scrollAmount) / 32.0F)
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int k = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
			int l = this.top + 4 - (int)this.scrollAmount;
			if (this.renderHeader) {
				this.renderHeader(k, l, tessellator);
			}

			this.renderList(matrices, k, l, mouseX, mouseY, delta);
			RenderSystem.disableDepthTest();
			this.renderHoleBackground(0, this.top, 255, 255);
			this.renderHoleBackground(this.bottom, this.height, 255, 255);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
			);
			RenderSystem.disableAlphaTest();
			RenderSystem.shadeModel(7425);
			RenderSystem.disableTexture();
			int m = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.left, (double)(this.top + 4), 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)(this.top + 4), 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.right, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.left, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)this.left, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.right, (double)(this.bottom - 4), 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.left, (double)(this.bottom - 4), 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 0).next();
			tessellator.draw();
			int n = this.getMaxScroll();
			if (n > 0) {
				int o = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
				o = MathHelper.clamp(o, 32, this.bottom - this.top - 8);
				int p = (int)this.scrollAmount * (this.bottom - this.top - o) / n + this.top;
				if (p < this.top) {
					p = this.top;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)j, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)i, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(p + o), 0.0).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)(p + o), 0.0).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)j, (double)p, 0.0).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex((double)i, (double)(p + o - 1), 0.0).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)(p + o - 1), 0.0).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(j - 1), (double)p, 0.0).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)i, (double)p, 0.0).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
				tessellator.draw();
			}

			this.renderDecorations(mouseX, mouseY);
			RenderSystem.enableTexture();
			RenderSystem.shadeModel(7424);
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
		}
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPosition() && mouseX < (double)(this.getScrollbarPosition() + 6);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (this.isVisible() && this.isMouseInList(mouseX, mouseY)) {
			int i = this.getItemAtPosition(mouseX, mouseY);
			if (i == -1 && button == 0) {
				this.clickedHeader(
					(int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.scrollAmount - 4
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
			if (mouseY < (double)this.top) {
				this.scrollAmount = 0.0;
			} else if (mouseY > (double)this.bottom) {
				this.scrollAmount = (double)this.getMaxScroll();
			} else {
				double d = (double)this.getMaxScroll();
				if (d < 1.0) {
					d = 1.0;
				}

				int i = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
				i = MathHelper.clamp(i, 32, this.bottom - this.top - 8);
				double e = d / (double)(this.bottom - this.top - i);
				if (e < 1.0) {
					e = 1.0;
				}

				this.scrollAmount += deltaY * e;
				this.capYPosition();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!this.isVisible()) {
			return false;
		} else {
			this.scrollAmount = this.scrollAmount - amount * (double)this.itemHeight / 2.0;
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

	protected void moveSelection(int by) {
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

	protected void renderList(MatrixStack matrixStack, int i, int j, int k, int l, float f) {
		int m = this.getItemCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

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

			this.renderItem(matrixStack, n, i, o, p, k, l, f);
		}
	}

	protected boolean isFocused() {
		return false;
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	protected void renderHoleBackground(int top, int bottom, int topAlpha, int bottomAlpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		this.client.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex((double)this.left, (double)bottom, 0.0).texture(0.0F, (float)bottom / 32.0F).color(64, 64, 64, bottomAlpha).next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)bottom, 0.0)
			.texture((float)this.width / 32.0F, (float)bottom / 32.0F)
			.color(64, 64, 64, bottomAlpha)
			.next();
		bufferBuilder.vertex((double)(this.left + this.width), (double)top, 0.0)
			.texture((float)this.width / 32.0F, (float)top / 32.0F)
			.color(64, 64, 64, topAlpha)
			.next();
		bufferBuilder.vertex((double)this.left, (double)top, 0.0).texture(0.0F, (float)top / 32.0F).color(64, 64, 64, topAlpha).next();
		tessellator.draw();
	}
}
