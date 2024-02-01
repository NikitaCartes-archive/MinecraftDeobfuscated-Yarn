package net.minecraft.client.gui.hud.debug;

import java.util.Locale;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.profiler.ServerTickType;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

@Environment(EnvType.CLIENT)
public class TickChart extends DebugChart {
	private static final int field_45935 = -65536;
	private static final int field_45936 = -256;
	private static final int field_45937 = -16711936;
	private static final int field_48763 = -6745839;
	private static final int field_48764 = -4548257;
	private static final int field_48765 = -10547572;
	private final Supplier<Float> millisPerTickSupplier;

	public TickChart(TextRenderer textRenderer, MultiValueDebugSampleLog log, Supplier<Float> millisPerTickSupplier) {
		super(textRenderer, log);
		this.millisPerTickSupplier = millisPerTickSupplier;
	}

	@Override
	protected void renderThresholds(DrawContext context, int x, int width, int height) {
		float f = (float)TimeHelper.SECOND_IN_MILLIS / (Float)this.millisPerTickSupplier.get();
		this.drawBorderedText(context, String.format("%.1f TPS", f), x + 1, height - 60 + 1);
	}

	@Override
	protected void drawOverlayBar(DrawContext context, int y, int x, int index) {
		long l = this.log.get(index, ServerTickType.TICK_SERVER_METHOD.ordinal());
		int i = this.getHeight((double)l);
		context.fill(RenderLayer.getGuiOverlay(), x, y - i, x + 1, y, -6745839);
		long m = this.log.get(index, ServerTickType.SCHEDULED_TASKS.ordinal());
		int j = this.getHeight((double)m);
		context.fill(RenderLayer.getGuiOverlay(), x, y - i - j, x + 1, y - i, -4548257);
		long n = this.log.get(index) - this.log.get(index, ServerTickType.IDLE.ordinal()) - l - m;
		int k = this.getHeight((double)n);
		context.fill(RenderLayer.getGuiOverlay(), x, y - k - j - i, x + 1, y - j - i, -10547572);
	}

	@Override
	protected long get(int index) {
		return this.log.get(index) - this.log.get(index, ServerTickType.IDLE.ordinal());
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
		return this.getColor(toMillisecondsPerTick((double)value), (double)f, -16711936, (double)f * 1.125, -256, (double)f * 1.25, -65536);
	}

	private static double toMillisecondsPerTick(double nanosecondsPerTick) {
		return nanosecondsPerTick / 1000000.0;
	}
}
