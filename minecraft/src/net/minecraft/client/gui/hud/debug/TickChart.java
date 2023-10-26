package net.minecraft.client.gui.hud.debug;

import java.util.Locale;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.profiler.PerformanceLog;

@Environment(EnvType.CLIENT)
public class TickChart extends DebugChart {
	private static final int field_45935 = -65536;
	private static final int field_45936 = -256;
	private static final int field_45937 = -16711936;
	private final Supplier<Float> millisPerTickSupplier;

	public TickChart(TextRenderer textRenderer, PerformanceLog log, Supplier<Float> millisPerTickSupplier) {
		super(textRenderer, log);
		this.millisPerTickSupplier = millisPerTickSupplier;
	}

	@Override
	protected void renderThresholds(DrawContext context, int x, int width, int height) {
		float f = (float)TimeHelper.SECOND_IN_MILLIS / (Float)this.millisPerTickSupplier.get();
		this.drawBorderedText(context, String.format("%.1f TPS", f), x + 1, height - 60 + 1);
	}

	@Override
	protected String format(double value) {
		return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMillisecondsPerTick(value)));
	}

	@Override
	protected int getHeight(double value) {
		return (int)Math.round(toMillisecondsPerTick(value) * 60.0 / (double)((Float)this.millisPerTickSupplier.get()).floatValue());
	}

	@Override
	protected int getColor(long value) {
		float f = (Float)this.millisPerTickSupplier.get();
		return this.getColor(toMillisecondsPerTick((double)value), 0.0, -16711936, (double)f / 2.0, -256, (double)f, -65536);
	}

	private static double toMillisecondsPerTick(double nanosecondsPerTick) {
		return nanosecondsPerTick / 1000000.0;
	}
}
