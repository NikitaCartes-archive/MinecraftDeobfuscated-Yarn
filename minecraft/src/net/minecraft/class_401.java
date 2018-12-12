package net.minecraft;

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
public class class_401 extends AbstractListWidget {
	private final RealmsSimpleScrolledSelectionList field_2343;

	public class_401(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.field_2343 = realmsSimpleScrolledSelectionList;
	}

	@Override
	protected int getEntryCount() {
		return this.field_2343.getItemCount();
	}

	@Override
	protected boolean selectEntry(int i, int j, double d, double e) {
		return this.field_2343.selectItem(i, j, d, e);
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return this.field_2343.isSelectedItem(i);
	}

	@Override
	protected void drawBackground() {
		this.field_2343.renderBackground();
	}

	@Override
	protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.field_2343.renderItem(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	protected int getMaxScrollPosition() {
		return this.field_2343.getMaxPosition();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.field_2343.getScrollbarPosition();
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			this.drawBackground();
			int k = this.getScrollbarPosition();
			int l = k + 6;
			this.clampScrollY();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			int m = this.x1 + this.width / 2 - this.getEntryWidth() / 2 + 2;
			int n = this.y1 + 4 - (int)this.scrollY;
			if (this.field_2170) {
				this.method_1940(m, n, tessellator);
			}

			this.drawEntries(m, n, i, j, f);
			GlStateManager.disableDepthTest();
			this.method_1954(0, this.y1, 255, 255);
			this.method_1954(this.y2, this.height, 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ZERO,
				GlStateManager.DstBlendFactor.ONE
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture();
			int o = this.getMaxScrollY();
			if (o > 0) {
				int p = (this.y2 - this.y1) * (this.y2 - this.y1) / this.getMaxScrollPosition();
				p = MathHelper.clamp(p, 32, this.y2 - this.y1 - 8);
				int q = (int)this.scrollY * (this.y2 - this.y1 - p) / o + this.y1;
				if (q < this.y1) {
					q = this.y1;
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);
				bufferBuilder.vertex((double)k, (double)this.y2, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.y2, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)l, (double)this.y1, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex((double)k, (double)this.y1, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
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

			this.method_1942(i, j);
			GlStateManager.enableTexture();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.field_2343.mouseScrolled(d) ? true : super.mouseScrolled(d);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_2343.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.field_2343.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.field_2343.mouseDragged(d, e, i, f, g);
	}
}
