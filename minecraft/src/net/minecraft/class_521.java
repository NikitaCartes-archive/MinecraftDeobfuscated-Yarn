package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.text.TextFormat;

@Environment(EnvType.CLIENT)
public abstract class class_521 extends EntryListWidget<class_520> {
	protected final MinecraftClient field_3166;

	public class_521(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j, 32, j - 55 + 4, 36);
		this.field_3166 = minecraftClient;
		this.field_2173 = false;
		this.method_1927(true, (int)((float)minecraftClient.fontRenderer.FONT_HEIGHT * 1.5F));
	}

	@Override
	protected void method_1940(int i, int j, Tessellator tessellator) {
		String string = TextFormat.UNDERLINE + "" + TextFormat.BOLD + this.method_2689();
		this.field_3166
			.fontRenderer
			.draw(string, (float)(i + this.width / 2 - this.field_3166.fontRenderer.getStringWidth(string) / 2), (float)Math.min(this.y1 + 3, j), 16777215);
	}

	protected abstract String method_2689();

	@Override
	public int getEntryWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.x2 - 6;
	}

	public void method_2690(class_520 arg) {
		super.method_1901(arg);
	}
}
