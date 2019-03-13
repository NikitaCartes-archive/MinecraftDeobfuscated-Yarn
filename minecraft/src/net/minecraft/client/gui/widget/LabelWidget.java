package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.InputListener;

@Environment(EnvType.CLIENT)
public class LabelWidget extends DrawableHelper implements Drawable, InputListener {
	protected int field_2114;
	protected int field_2112;
	public final int field_2111;
	public int field_2110;
	private final List<String> lines;
	private boolean centered;
	public boolean field_2117;
	private boolean field_2115;
	private final int color;
	private int field_2122;
	private int field_2120;
	private int field_2119;
	private final TextRenderer fontRenderer;
	private int field_2118;

	@Override
	public void draw(int i, int j, float f) {
		if (this.field_2117) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			this.method_1891(i, j, f);
			int k = this.field_2110 + this.field_2112 / 2 + this.field_2118 / 2;
			int l = k - this.lines.size() * 10 / 2;

			for (int m = 0; m < this.lines.size(); m++) {
				if (this.centered) {
					this.drawStringCentered(this.fontRenderer, (String)this.lines.get(m), this.field_2111 + this.field_2114 / 2, l + m * 10, this.color);
				} else {
					this.drawString(this.fontRenderer, (String)this.lines.get(m), this.field_2111, l + m * 10, this.color);
				}
			}
		}
	}

	protected void method_1891(int i, int j, float f) {
		if (this.field_2115) {
			int k = this.field_2114 + this.field_2118 * 2;
			int l = this.field_2112 + this.field_2118 * 2;
			int m = this.field_2111 - this.field_2118;
			int n = this.field_2110 - this.field_2118;
			drawRect(m, n, m + k, n + l, this.field_2122);
			this.drawHorizontalLine(m, m + k, n, this.field_2120);
			this.drawHorizontalLine(m, m + k, n + l, this.field_2119);
			this.drawVerticalLine(m, n, n + l, this.field_2120);
			this.drawVerticalLine(m + k, n, n + l, this.field_2119);
		}
	}

	@Override
	public boolean method_19356(double d, double e) {
		return this.field_2117
			&& d >= (double)this.field_2111
			&& d < (double)(this.field_2111 + this.field_2114)
			&& e >= (double)this.field_2110
			&& e < (double)(this.field_2110 + this.field_2112);
	}
}
