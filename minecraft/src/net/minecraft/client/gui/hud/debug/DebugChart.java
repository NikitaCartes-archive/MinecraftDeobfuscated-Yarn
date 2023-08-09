package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.PerformanceLog;

@Environment(EnvType.CLIENT)
public abstract class DebugChart {
	protected static final int TEXT_COLOR = 14737632;
	protected static final int field_45916 = 60;
	protected static final int field_45917 = 1;
	protected final TextRenderer textRenderer;
	protected final PerformanceLog log;

	protected DebugChart(TextRenderer textRenderer, PerformanceLog log) {
		this.textRenderer = textRenderer;
		this.log = log;
	}

	public int getWidth(int centerX) {
		return Math.min(this.log.size() + 2, centerX);
	}

	public void render(DrawContext context, int x, int width) {
		int i = context.getScaledWindowHeight();
		context.fill(RenderLayer.getGuiOverlay(), x, i - 60, x + width, i, -1873784752);
		long l = 0L;
		long m = 2147483647L;
		long n = -2147483648L;
		int j = Math.max(0, this.log.size() - (width - 2));
		int k = this.log.getMaxIndex() - j;

		for (int o = 0; o < k; o++) {
			int p = x + o + 1;
			long q = this.log.get(j + o);
			m = Math.min(m, q);
			n = Math.max(n, q);
			l += q;
			int r = this.getHeight((double)q);
			int s = this.getColor(q);
			context.fill(RenderLayer.getGuiOverlay(), p, i - r, p + 1, i, s);
		}

		context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + width - 1, i - 60, -1);
		context.drawHorizontalLine(RenderLayer.getGuiOverlay(), x, x + width - 1, i - 1, -1);
		context.drawVerticalLine(RenderLayer.getGuiOverlay(), x, i - 60, i, -1);
		context.drawVerticalLine(RenderLayer.getGuiOverlay(), x + width - 1, i - 60, i, -1);
		if (k > 0) {
			String string = this.format((double)m) + " min";
			String string2 = this.format((double)l / (double)k) + " avg";
			String string3 = this.format((double)n) + " max";
			context.drawTextWithShadow(this.textRenderer, string, x + 2, i - 60 - 9, 14737632);
			context.drawCenteredTextWithShadow(this.textRenderer, string2, x + width / 2, i - 60 - 9, 14737632);
			context.drawTextWithShadow(this.textRenderer, string3, x + width - this.textRenderer.getWidth(string3) - 2, i - 60 - 9, 14737632);
		}

		this.renderThresholds(context, x, width, i);
	}

	protected void renderThresholds(DrawContext context, int x, int width, int height) {
	}

	protected void drawBorderedText(DrawContext context, String string, int x, int y) {
		context.fill(RenderLayer.getGuiOverlay(), x, y, x + this.textRenderer.getWidth(string) + 1, y + 9, -1873784752);
		context.drawText(this.textRenderer, string, x + 1, y + 1, 14737632, false);
	}

	protected abstract String format(double value);

	protected abstract int getHeight(double value);

	protected abstract int getColor(long value);

	protected int getColor(double value, double min, int minColor, double median, int medianColor, double max, int maxColor) {
		value = MathHelper.clamp(value, min, max);
		return value < median
			? ColorHelper.Argb.lerp((float)(value / (median - min)), minColor, medianColor)
			: ColorHelper.Argb.lerp((float)((value - median) / (max - median)), medianColor, maxColor);
	}
}
