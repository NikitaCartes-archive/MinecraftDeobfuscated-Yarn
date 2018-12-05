package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class ErrorGui extends Gui {
	private final String title;
	private final String message;

	public ErrorGui(String string, String string2) {
		this.title = string;
		this.message = string2;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, 140, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				ErrorGui.this.client.openGui(null);
			}
		});
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 90, 16777215);
		this.drawStringCentered(this.fontRenderer, this.message, this.width / 2, 110, 16777215);
		super.draw(i, j, f);
	}

	@Override
	public boolean canClose() {
		return false;
	}
}
