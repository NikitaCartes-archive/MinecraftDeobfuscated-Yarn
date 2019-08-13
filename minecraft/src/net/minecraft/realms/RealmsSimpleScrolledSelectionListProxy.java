package net.minecraft.realms;

import com.mojang.blaze3d.platform.GlStateManager;
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
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			int m = this.field_2180 + this.width / 2 - this.getRowWidth() / 2 + 2;
			int n = this.field_2166 + 4 - (int)this.field_2175;
			if (this.renderHeader) {
				this.renderHeader(m, n, tessellator);
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
			int o = this.getMaxScroll();
			if (o > 0) {
				int p = (this.field_2165 - this.field_2166) * (this.field_2165 - this.field_2166) / this.getMaxPosition();
				p = MathHelper.clamp(p, 32, this.field_2165 - this.field_2166 - 8);
				int q = (int)this.field_2175 * (this.field_2165 - this.field_2166 - p) / o + this.field_2166;
				if (q < this.field_2166) {
					q = this.field_2166;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.field_2165, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.field_2165, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.field_2166, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.field_2166, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
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
