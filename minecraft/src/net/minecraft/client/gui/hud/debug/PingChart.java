package net.minecraft.client.gui.hud.debug;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

@Environment(EnvType.CLIENT)
public class PingChart extends DebugChart {
	private static final int field_45931 = -65536;
	private static final int field_45932 = -256;
	private static final int field_45933 = -16711936;
	private static final int field_45934 = 500;

	public PingChart(TextRenderer textRenderer, MultiValueDebugSampleLog multiValueDebugSampleLog) {
		super(textRenderer, multiValueDebugSampleLog);
	}

	@Override
	protected void renderThresholds(DrawContext context, int x, int width, int height) {
		this.drawBorderedText(context, "500 ms", x + 1, height - 60 + 1);
	}

	@Override
	protected String format(double value) {
		return String.format(Locale.ROOT, "%d ms", (int)Math.round(value));
	}

	@Override
	protected int getHeight(double value) {
		return (int)Math.round(value * 60.0 / 500.0);
	}

	@Override
	protected int getColor(long value) {
		return this.getColor((double)value, 0.0, -16711936, 250.0, -256, 500.0, -65536);
	}
}
