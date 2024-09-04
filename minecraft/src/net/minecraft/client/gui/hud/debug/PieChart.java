package net.minecraft.client.gui.hud.debug;

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
public class PieChart {
	private static final int field_52773 = 105;
	private static final int field_52774 = 5;
	private static final int field_52775 = 10;
	private final TextRenderer textRenderer;
	@Nullable
	private ProfileResult profileResult;
	private String currentPath = "root";
	private int bottomMargin = 0;

	public PieChart(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

	public void setProfileResult(@Nullable ProfileResult profileResult) {
		this.profileResult = profileResult;
	}

	public void setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public void render(DrawContext context) {
		if (this.profileResult != null) {
			List<ProfilerTiming> list = this.profileResult.getTimings(this.currentPath);
			ProfilerTiming profilerTiming = (ProfilerTiming)list.removeFirst();
			int i = context.getScaledWindowWidth() - 105 - 10;
			int j = i - 105;
			int k = i + 105;
			int l = list.size() * 9;
			int m = context.getScaledWindowHeight() - this.bottomMargin - 5;
			int n = m - l;
			int o = 62;
			int p = n - 62 - 5;
			context.fill(j - 5, p - 62 - 5, k + 5, m + 5, -1873784752);
			context.draw(vertexConsumers -> {
				double d = 0.0;

				for (ProfilerTiming profilerTimingx : list) {
					int kx = MathHelper.floor(profilerTimingx.parentSectionUsagePercentage / 4.0) + 1;
					VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugTriangleFan());
					int lx = ColorHelper.fullAlpha(profilerTimingx.getColor());
					int mx = ColorHelper.mix(lx, Colors.GRAY);
					MatrixStack.Entry entry = context.getMatrices().peek();
					vertexConsumer.vertex(entry, (float)i, (float)p, 10.0F).color(lx);

					for (int nxx = kx; nxx >= 0; nxx--) {
						float f = (float)((d + profilerTimingx.parentSectionUsagePercentage * (double)nxx / (double)kx) * (float) (Math.PI * 2) / 100.0);
						float g = MathHelper.sin(f) * 105.0F;
						float h = MathHelper.cos(f) * 105.0F * 0.5F;
						vertexConsumer.vertex(entry, (float)i + g, (float)p - h, 10.0F).color(lx);
					}

					vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());

					for (int nx = kx; nx > 0; nx--) {
						float f = (float)((d + profilerTimingx.parentSectionUsagePercentage * (double)nx / (double)kx) * (float) (Math.PI * 2) / 100.0);
						float g = MathHelper.sin(f) * 105.0F;
						float h = MathHelper.cos(f) * 105.0F * 0.5F;
						float ox = (float)((d + profilerTimingx.parentSectionUsagePercentage * (double)(nx - 1) / (double)kx) * (float) (Math.PI * 2) / 100.0);
						float px = MathHelper.sin(ox) * 105.0F;
						float qx = MathHelper.cos(ox) * 105.0F * 0.5F;
						if (!((h + qx) / 2.0F > 0.0F)) {
							vertexConsumer.vertex(entry, (float)i + g, (float)p - h, 10.0F).color(mx);
							vertexConsumer.vertex(entry, (float)i + g, (float)p - h + 10.0F, 10.0F).color(mx);
							vertexConsumer.vertex(entry, (float)i + px, (float)p - qx + 10.0F, 10.0F).color(mx);
							vertexConsumer.vertex(entry, (float)i + px, (float)p - qx, 10.0F).color(mx);
						}
					}

					d += profilerTimingx.parentSectionUsagePercentage;
				}
			});
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

			int q = 16777215;
			int r = p - 62;
			context.drawTextWithShadow(this.textRenderer, string2, j, r, 16777215);
			string2 = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
			context.drawTextWithShadow(this.textRenderer, string2, k - this.textRenderer.getWidth(string2), r, 16777215);

			for (int s = 0; s < list.size(); s++) {
				ProfilerTiming profilerTiming2 = (ProfilerTiming)list.get(s);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(profilerTiming2.name)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(s + 1).append("] ");
				}

				String string3 = stringBuilder.append(profilerTiming2.name).toString();
				int t = n + s * 9;
				context.drawTextWithShadow(this.textRenderer, string3, j, t, profilerTiming2.getColor());
				string3 = decimalFormat.format(profilerTiming2.parentSectionUsagePercentage) + "%";
				context.drawTextWithShadow(this.textRenderer, string3, k - 50 - this.textRenderer.getWidth(string3), t, profilerTiming2.getColor());
				string3 = decimalFormat.format(profilerTiming2.totalUsagePercentage) + "%";
				context.drawTextWithShadow(this.textRenderer, string3, k - this.textRenderer.getWidth(string3), t, profilerTiming2.getColor());
			}
		}
	}

	public void select(int index) {
		if (this.profileResult != null) {
			List<ProfilerTiming> list = this.profileResult.getTimings(this.currentPath);
			if (!list.isEmpty()) {
				ProfilerTiming profilerTiming = (ProfilerTiming)list.remove(0);
				if (index == 0) {
					if (!profilerTiming.name.isEmpty()) {
						int i = this.currentPath.lastIndexOf(30);
						if (i >= 0) {
							this.currentPath = this.currentPath.substring(0, i);
						}
					}
				} else {
					index--;
					if (index < list.size() && !"unspecified".equals(((ProfilerTiming)list.get(index)).name)) {
						if (!this.currentPath.isEmpty()) {
							this.currentPath = this.currentPath + "\u001e";
						}

						this.currentPath = this.currentPath + ((ProfilerTiming)list.get(index)).name;
					}
				}
			}
		}
	}
}
