package net.minecraft.client.gui.hud.debug;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.profiler.PerformanceLog;

@Environment(EnvType.CLIENT)
public class TickChart extends DebugChart {
	private static final int field_45935 = -65536;
	private static final int field_45936 = -256;
	private static final int field_45937 = -16711936;
	private static final int field_45938 = 50;

	public TickChart(TextRenderer textRenderer, PerformanceLog performanceLog) {
		super(textRenderer, performanceLog);
	}

	@Override
	protected void renderThresholds(DrawContext context, int x, int width, int height) {
		this.drawBorderedText(context, "20 TPS", x + 1, height - 60 + 1);
	}

	@Override
	protected String format(double value) {
		return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMillisecondsPerTick(value)));
	}

	@Override
	protected int getHeight(double value) {
		return (int)Math.round(toMillisecondsPerTick(value) * 60.0 / 50.0);
	}

	@Override
	protected int getColor(long value) {
		return this.getColor(toMillisecondsPerTick((double)value), 0.0, -16711936, 25.0, -256, 50.0, -65536);
	}

	private static double toMillisecondsPerTick(double nanosecondsPerTick) {
		return nanosecondsPerTick / 1000000.0;
	}
}