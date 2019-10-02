package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RealmsSimpleScrolledSelectionListProxy extends ListWidget {
	private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;

	public RealmsSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
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
			RenderSystem.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
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
				GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ZERO, GlStateManager.class_4534.ONE
			);
			RenderSystem.disableAlphaTest();
			RenderSystem.shadeModel(7425);
			RenderSystem.disableTexture();
			int o = this.getMaxScroll();
			if (o > 0) {
				int p = (this.bottom - this.top) * (this.bottom - this.top) / this.getMaxPosition();
				p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
				int q = (int)this.scroll * (this.bottom - this.top - p) / o + this.top;
				if (q < this.top) {
					q = this.top;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(q + p), 0.0).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)(q + p), 0.0).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)l, (double)q, 0.0).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
				bufferBuilder.vertex((double)k, (double)q, 0.0).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
				tessellator.draw();
				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)(q + p - 1), 0.0).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)(q + p - 1), 0.0).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)(l - 1), (double)q, 0.0).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
				bufferBuilder.vertex((double)k, (double)q, 0.0).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
				tessellator.draw();
			}

			this.renderDecorations(i, j);
			RenderSystem.enableTexture();
			RenderSystem.shadeModel(7424);
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
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
