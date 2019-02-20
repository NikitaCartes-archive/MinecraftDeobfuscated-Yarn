package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4068;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableContainer;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractListWidget extends DrawableContainer implements class_4068 {
	protected final MinecraftClient client;
	protected int width;
	protected int height;
	protected int y1;
	protected int y2;
	protected int x2;
	protected int x1;
	protected final int entryHeight;
	protected boolean field_2173 = true;
	protected int field_2178 = -2;
	protected double scrollY;
	protected boolean visible = true;
	protected boolean field_2171 = true;
	protected boolean field_2170;
	protected int field_2174;
	private boolean field_2169;

	public AbstractListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		this.client = minecraftClient;
		this.width = i;
		this.height = j;
		this.y1 = k;
		this.y2 = l;
		this.entryHeight = m;
		this.x1 = 0;
		this.x2 = i;
	}

	public void method_1953(int i, int j, int k, int l) {
		this.width = i;
		this.height = j;
		this.y1 = k;
		this.y2 = l;
		this.x1 = 0;
		this.x2 = i;
	}

	public void method_1943(boolean bl) {
		this.field_2171 = bl;
	}

	protected void method_1927(boolean bl, int i) {
		this.field_2170 = bl;
		this.field_2174 = i;
		if (!bl) {
			this.field_2174 = 0;
		}
	}

	public boolean isVisible() {
		return this.visible;
	}

	protected abstract int getEntryCount();

	@Override
	public List<? extends GuiEventListener> method_1968() {
		return Collections.emptyList();
	}

	protected boolean selectEntry(int i, int j, double d, double e) {
		return true;
	}

	protected abstract boolean isSelectedEntry(int i);

	protected int getMaxScrollPosition() {
		return this.getEntryCount() * this.entryHeight + this.field_2174;
	}

	protected abstract void drawBackground();

	protected void method_1952(int i, int j, int k, float f) {
	}

	protected abstract void drawEntry(int i, int j, int k, int l, int m, int n, float f);

	protected void method_1940(int i, int j, Tessellator tessellator) {
	}

	protected void method_1929(int i, int j) {
	}

	protected void method_1942(int i, int j) {
	}

	public int getSelectedEntry(double d, double e) {
		int i = this.x1 + this.width / 2 - this.getEntryWidth() / 2;
		int j = this.x1 + this.width / 2 + this.getEntryWidth() / 2;
		int k = MathHelper.floor(e - (double)this.y1) - this.field_2174 + (int)this.scrollY - 4;
		int l = k / this.entryHeight;
		return d < (double)this.getScrollbarPosition() && d >= (double)i && d <= (double)j && l >= 0 && k >= 0 && l < this.getEntryCount() ? l : -1;
	}

	protected void clampScrollY() {
		this.scrollY = MathHelper.clamp(this.scrollY, 0.0, (double)this.getMaxScrollY());
	}

	public int getMaxScrollY() {
		return Math.max(0, this.getMaxScrollPosition() - (this.y2 - this.y1 - 4));
	}

	public int getScrollY() {
		return (int)this.scrollY;
	}

	public boolean isSelected(double d, double e) {
		return e >= (double)this.y1 && e <= (double)this.y2 && d >= (double)this.x1 && d <= (double)this.x2;
	}

	public void scroll(int i) {
		this.scrollY += (double)i;
		this.clampScrollY();
		this.field_2178 = -2;
	}

	@Override
	public void method_18326(int i, int j, float f) {
		if (this.visible) {
			this.drawBackground();
			int k = this.getScrollbarPosition();
			int l = k + 6;
			this.clampScrollY();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			this.client.getTextureManager().bindTexture(Drawable.OPTIONS_BG);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			float g = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.x1, (double)this.y2, 0.0)
				.texture((double)((float)this.x1 / 32.0F), (double)((float)(this.y2 + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.x2, (double)this.y2, 0.0)
				.texture((double)((float)this.x2 / 32.0F), (double)((float)(this.y2 + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.x2, (double)this.y1, 0.0)
				.texture((double)((float)this.x2 / 32.0F), (double)((float)(this.y1 + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			bufferBuilder.vertex((double)this.x1, (double)this.y1, 0.0)
				.texture((double)((float)this.x1 / 32.0F), (double)((float)(this.y1 + (int)this.scrollY) / 32.0F))
				.color(32, 32, 32, 255)
				.next();
			tessellator.draw();
			int m = this.x1 + this.width / 2 - this.getEntryWidth() / 2 + 2;
			int n = this.y1 + 4 - (int)this.scrollY;
			if (this.field_2170) {
				this.method_1940(m, n, tessellator);
			}

			this.drawEntries(m, n, i, j, f);
			GlStateManager.disableDepthTest();
			this.renderCoverBackground(0, this.y1, 255, 255);
			this.renderCoverBackground(this.y2, this.height, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = 4;
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.x1, (double)(this.y1 + 4), 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.x2, (double)(this.y1 + 4), 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.x2, (double)this.y1, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.x1, (double)this.y1, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
			tessellator.draw();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
			bufferBuilder.vertex((double)this.x1, (double)this.y2, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.x2, (double)this.y2, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
			bufferBuilder.vertex((double)this.x2, (double)(this.y2 - 4), 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
			bufferBuilder.vertex((double)this.x1, (double)(this.y2 - 4), 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
			tessellator.draw();
			int p = this.getMaxScrollY();
			if (p > 0) {
				int q = (int)((float)((this.y2 - this.y1) * (this.y2 - this.y1)) / (float)this.getMaxScrollPosition());
				q = MathHelper.clamp(q, 32, this.y2 - this.y1 - 8);
				int r = (int)this.scrollY * (this.y2 - this.y1 - q) / p + this.y1;
				if (r < this.y1) {
					r = this.y1;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.y2, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.y2, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.y1, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.y1, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
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

			this.method_1942(i, j);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	protected void method_1933(double d, double e, int i) {
		this.field_2169 = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.method_1933(d, e, i);
		if (this.isVisible() && this.isSelected(d, e)) {
			int j = this.getSelectedEntry(d, e);
			if (j == -1 && i == 0) {
				this.method_1929((int)(d - (double)(this.x1 + this.width / 2 - this.getEntryWidth() / 2)), (int)(e - (double)this.y1) + (int)this.scrollY - 4);
				return true;
			} else if (j != -1 && this.selectEntry(j, i, d, e)) {
				if (this.method_1968().size() > j) {
					this.method_1967((GuiEventListener)this.method_1968().get(j));
				}

				this.method_1966(true);
				return true;
			} else {
				return this.field_2169;
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
		} else if (this.isVisible() && i == 0 && this.field_2169) {
			if (e < (double)this.y1) {
				this.scrollY = 0.0;
			} else if (e > (double)this.y2) {
				this.scrollY = (double)this.getMaxScrollY();
			} else {
				double h = (double)this.getMaxScrollY();
				if (h < 1.0) {
					h = 1.0;
				}

				int j = (int)((float)((this.y2 - this.y1) * (this.y2 - this.y1)) / (float)this.getMaxScrollPosition());
				j = MathHelper.clamp(j, 32, this.y2 - this.y1 - 8);
				double k = h / (double)(this.y2 - this.y1 - j);
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
	public boolean mouseScrolled(double d) {
		if (!this.isVisible()) {
			return false;
		} else {
			this.scrollY = this.scrollY - d * (double)this.entryHeight / 2.0;
			return true;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return !this.isVisible() ? false : super.keyPressed(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		return !this.isVisible() ? false : super.charTyped(c, i);
	}

	public int getEntryWidth() {
		return 220;
	}

	protected void drawEntries(int i, int j, int k, int l, float f) {
		int m = this.getEntryCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.entryHeight + this.field_2174;
			int p = this.entryHeight - 4;
			if (o > this.y2 || o + p < this.y1) {
				this.method_1952(n, i, o, f);
			}

			if (this.field_2171 && this.isSelectedEntry(n)) {
				int q = this.x1 + this.width / 2 - this.getEntryWidth() / 2;
				int r = this.x1 + this.width / 2 + this.getEntryWidth() / 2;
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableTexture();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)q, (double)(o + p + 2), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)r, (double)(o + p + 2), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)r, (double)(o - 2), 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)q, (double)(o - 2), 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)(q + 1), (double)(o + p + 1), 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o + p + 1), 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)(r - 1), (double)(o - 1), 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)(q + 1), (double)(o - 1), 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				GlStateManager.enableTexture();
			}

			this.drawEntry(n, i, o, p, k, l, f);
		}
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	protected void renderCoverBackground(int i, int j, int k, int l) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		this.client.getTextureManager().bindTexture(Drawable.OPTIONS_BG);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
		bufferBuilder.vertex((double)this.x1, (double)j, 0.0).texture(0.0, (double)((float)j / 32.0F)).color(64, 64, 64, l).next();
		bufferBuilder.vertex((double)(this.x1 + this.width), (double)j, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)j / 32.0F))
			.color(64, 64, 64, l)
			.next();
		bufferBuilder.vertex((double)(this.x1 + this.width), (double)i, 0.0)
			.texture((double)((float)this.width / 32.0F), (double)((float)i / 32.0F))
			.color(64, 64, 64, k)
			.next();
		bufferBuilder.vertex((double)this.x1, (double)i, 0.0).texture(0.0, (double)((float)i / 32.0F)).color(64, 64, 64, k).next();
		tessellator.draw();
	}

	public void setX(int i) {
		this.x1 = i;
		this.x2 = i + this.width;
	}

	public int getEntryHeight() {
		return this.entryHeight;
	}
}
