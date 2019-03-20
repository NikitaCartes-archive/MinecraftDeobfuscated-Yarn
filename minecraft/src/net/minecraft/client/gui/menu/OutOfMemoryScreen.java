package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class OutOfMemoryScreen extends Screen {
	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight / 4 + 120 + 12, 150, 20, I18n.translate("gui.toTitle")) {
			@Override
			public void onPressed() {
				OutOfMemoryScreen.this.client.openScreen(new MainMenuScreen());
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155 + 160, this.screenHeight / 4 + 120 + 12, 150, 20, I18n.translate("menu.quit")) {
			@Override
			public void onPressed() {
				OutOfMemoryScreen.this.client.scheduleStop();
			}
		});
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, "Out of memory!", this.screenWidth / 2, this.screenHeight / 4 - 60 + 20, 16777215);
		this.drawString(this.fontRenderer, "Minecraft has run out of memory.", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 0, 10526880);
		this.drawString(
			this.fontRenderer, "This could be caused by a bug in the game or by the", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 18, 10526880
		);
		this.drawString(
			this.fontRenderer, "Java Virtual Machine not being allocated enough", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 27, 10526880
		);
		this.drawString(this.fontRenderer, "memory.", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 36, 10526880);
		this.drawString(
			this.fontRenderer, "To prevent level corruption, the current game has quit.", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 54, 10526880
		);
		this.drawString(
			this.fontRenderer, "We've tried to free up enough memory to let you go back to", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 63, 10526880
		);
		this.drawString(
			this.fontRenderer,
			"the main menu and back to playing, but this may not have worked.",
			this.screenWidth / 2 - 140,
			this.screenHeight / 4 - 60 + 60 + 72,
			10526880
		);
		this.drawString(
			this.fontRenderer, "Please restart the game if you see this message again.", this.screenWidth / 2 - 140, this.screenHeight / 4 - 60 + 60 + 81, 10526880
		);
		super.render(i, j, f);
	}
}
