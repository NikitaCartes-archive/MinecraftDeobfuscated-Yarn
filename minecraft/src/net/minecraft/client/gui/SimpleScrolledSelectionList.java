package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SimpleScrolledSelectionList extends AbstractListWidget {
	private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;

	public SimpleScrolledSelectionList(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.realmsSimpleScrolledSelectionList = realmsSimpleScrolledSelectionList;
	}

	@Override
	public int getEntryCount() {
		return this.realmsSimpleScrolledSelectionList.getItemCount();
	}

	@Override
	public boolean selectEntry(int i, int j, double d, double e) {
		return this.realmsSimpleScrolledSelectionList.selectItem(i, j, d, e);
	}

	@Override
	public boolean isSelectedEntry(int i) {
		return this.realmsSimpleScrolledSelectionList.isSelectedItem(i);
	}

	@Override
	public void drawBackground() {
		this.realmsSimpleScrolledSelectionList.renderBackground();
	}

	@Override
	public void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.realmsSimpleScrolledSelectionList.renderItem(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxScrollPosition() {
		return this.realmsSimpleScrolledSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsSimpleScrolledSelectionList.getScrollbarPosition();
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
			int o = this.getMaxScrollY();
			if (o > 0) {
				int p = (this.bottom - this.y) * (this.bottom - this.y) / this.getMaxScrollPosition();
				p = MathHelper.clamp(p, 32, this.bottom - this.y - 8);
				int q = (int)this.scrollY * (this.bottom - this.y - p) / o + this.y;
				if (q < this.y) {
					q = this.y;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.y, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.y, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(q + p), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)(q + p), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)q, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)k, (double)q, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(q + p - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)(q + p - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)q, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)k, (double)q, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
				tessellator.draw();
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
