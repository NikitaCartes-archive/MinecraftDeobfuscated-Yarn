package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.GuiEventListener;

@Environment(EnvType.CLIENT)
public class LabelWidget extends Drawable implements GuiEventListener {
	protected int field_2114;
	protected int field_2112;
	public int field_2111;
	public int field_2110;
	private final List<String> field_2121;
	private boolean field_2116;
	public boolean field_2117;
	private boolean field_2115;
	private final int field_2123;
	private int field_2122;
	private int field_2120;
	private int field_2119;
	private final FontRenderer field_2113;
	private int field_2118;

	public void draw(int i, int j, float f) {
		if (this.field_2117) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SrcBlendFactor.SRC_ALPHA,
				GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcBlendFactor.ONE,
				GlStateManager.DstBlendFactor.ZERO
			);
			this.method_1891(i, j, f);
			int k = this.field_2110 + this.field_2112 / 2 + this.field_2118 / 2;
			int l = k - this.field_2121.size() * 10 / 2;

			for (int m = 0; m < this.field_2121.size(); m++) {
				if (this.field_2116) {
					this.drawStringCentered(this.field_2113, (String)this.field_2121.get(m), this.field_2111 + this.field_2114 / 2, l + m * 10, this.field_2123);
				} else {
					this.drawString(this.field_2113, (String)this.field_2121.get(m), this.field_2111, l + m * 10, this.field_2123);
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
}
