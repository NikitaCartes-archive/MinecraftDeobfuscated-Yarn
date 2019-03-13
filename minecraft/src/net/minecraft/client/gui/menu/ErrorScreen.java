package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class ErrorScreen extends Screen {
	private final String title;
	private final String message;

	public ErrorScreen(String string, String string2) {
		this.title = string;
		this.message = string2;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new class_4185(this.screenWidth / 2 - 100, 140, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				ErrorScreen.this.client.method_1507(null);
			}
		});
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawGradientRect(0, 0, this.screenWidth, this.screenHeight, -12574688, -11530224);
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 90, 16777215);
		this.drawStringCentered(this.fontRenderer, this.message, this.screenWidth / 2, 110, 16777215);
		super.draw(i, j, f);
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}
}
