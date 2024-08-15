package net.minecraft;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;

@Environment(EnvType.CLIENT)
public class class_9931 {
	private static final int field_52773 = 105;
	private static final int field_52774 = 5;
	private static final int field_52775 = 10;
	private final TextRenderer field_52776;
	@Nullable
	private ProfileResult field_52777;
	private String field_52778 = "root";
	private int field_52779 = 0;

	public class_9931(TextRenderer textRenderer) {
		this.field_52776 = textRenderer;
	}

	public void method_61985(@Nullable ProfileResult profileResult) {
		this.field_52777 = profileResult;
	}

	public void method_61984(int i) {
		this.field_52779 = i;
	}

	public void method_61986(DrawContext drawContext) {
		if (this.field_52777 != null) {
			List<ProfilerTiming> list = this.field_52777.getTimings(this.field_52778);
			ProfilerTiming profilerTiming = (ProfilerTiming)list.removeFirst();
			int i = drawContext.getScaledWindowWidth() - 105 - 10;
			int j = i - 105;
			int k = i + 105;
			int l = list.size() * 9;
			int m = drawContext.getScaledWindowHeight() - this.field_52779 - 5;
			int n = m - l;
			int o = 62;
			int p = n - 62 - 5;
			drawContext.fill(j - 5, p - 62 - 5, k + 5, m + 5, -1873784752);
			double d = 0.0;

			for (ProfilerTiming profilerTiming2 : list) {
				int q = MathHelper.floor(profilerTiming2.parentSectionUsagePercentage / 4.0) + 1;
				VertexConsumer vertexConsumer = drawContext.getVertexConsumers().getBuffer(RenderLayer.getDebugTriangleFan());
				int r = ColorHelper.fullAlpha(profilerTiming2.getColor());
				int s = ColorHelper.mix(r, Colors.GRAY);
				MatrixStack.Entry entry = drawContext.getMatrices().peek();
				vertexConsumer.vertex(entry, (float)i, (float)p, 10.0F).color(r);

				for (int t = q; t >= 0; t--) {
					float f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)t / (double)q) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 105.0F;
					float h = MathHelper.cos(f) * 105.0F * 0.5F;
					vertexConsumer.vertex(entry, (float)i + g, (float)p - h, 10.0F).color(r);
				}

				vertexConsumer = drawContext.getVertexConsumers().getBuffer(RenderLayer.getDebugQuads());

				for (int t = q; t > 0; t--) {
					float f = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)t / (double)q) * (float) (Math.PI * 2) / 100.0);
					float g = MathHelper.sin(f) * 105.0F;
					float h = MathHelper.cos(f) * 105.0F * 0.5F;
					float u = (float)((d + profilerTiming2.parentSectionUsagePercentage * (double)(t - 1) / (double)q) * (float) (Math.PI * 2) / 100.0);
					float v = MathHelper.sin(u) * 105.0F;
					float w = MathHelper.cos(u) * 105.0F * 0.5F;
					if (!((h + w) / 2.0F > 0.0F)) {
						vertexConsumer.vertex(entry, (float)i + g, (float)p - h, 10.0F).color(s);
						vertexConsumer.vertex(entry, (float)i + g, (float)p - h + 10.0F, 10.0F).color(s);
						vertexConsumer.vertex(entry, (float)i + v, (float)p - w + 10.0F, 10.0F).color(s);
						vertexConsumer.vertex(entry, (float)i + v, (float)p - w, 10.0F).color(s);
					}
				}

				d += profilerTiming2.parentSectionUsagePercentage;
			}

			DecimalFormat decimalFormat = new DecimalFormat("##0.00");
			decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
			String string = ProfileResult.getHumanReadableName(profilerTiming.name);
			String string2 = "";
			if (!"unspecified".equals(string)) {
				string2 = string2 + "[0] ";
			}

			if (string.isEmpty()) {
				string2 = string2 + "ROOT ";
			} else {
				string2 = string2 + string + " ";
			}

			int x = 16777215;
			int r = p - 62;
			drawContext.drawTextWithShadow(this.field_52776, string2, j, r, 16777215);
			string2 = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
			drawContext.drawTextWithShadow(this.field_52776, string2, k - this.field_52776.getWidth(string2), r, 16777215);

			for (int s = 0; s < list.size(); s++) {
				ProfilerTiming profilerTiming3 = (ProfilerTiming)list.get(s);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(profilerTiming3.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(s + 1).append("] ");
				}

				String string3 = stringBuilder.append(profilerTiming3.name).toString();
				int y = n + s * 9;
				drawContext.drawTextWithShadow(this.field_52776, string3, j, y, profilerTiming3.getColor());
				string3 = decimalFormat.format(profilerTiming3.parentSectionUsagePercentage) + "%";
				drawContext.drawTextWithShadow(this.field_52776, string3, k - 50 - this.field_52776.getWidth(string3), y, profilerTiming3.getColor());
				string3 = decimalFormat.format(profilerTiming3.totalUsagePercentage) + "%";
				drawContext.drawTextWithShadow(this.field_52776, string3, k - this.field_52776.getWidth(string3), y, profilerTiming3.getColor());
			}
		}
	}

	public void method_61987(int i) {
		if (this.field_52777 != null) {
			List<ProfilerTiming> list = this.field_52777.getTimings(this.field_52778);
			if (!list.isEmpty()) {
				ProfilerTiming profilerTiming = (ProfilerTiming)list.remove(0);
				if (i == 0) {
					if (!profilerTiming.name.isEmpty()) {
						int j = this.field_52778.lastIndexOf(30);
						if (j >= 0) {
							this.field_52778 = this.field_52778.substring(0, j);
						}
					}
				} else {
					i--;
					if (i < list.size() && !"unspecified".equals(((ProfilerTiming)list.get(i)).name)) {
						if (!this.field_52778.isEmpty()) {
							this.field_52778 = this.field_52778 + "\u001e";
						}

						this.field_52778 = this.field_52778 + ((ProfilerTiming)list.get(i)).name;
					}
				}
			}
		}
	}
}
