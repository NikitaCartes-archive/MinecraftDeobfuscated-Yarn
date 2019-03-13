package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen {
	private final Screen parent;
	private class_4185 buttonAllowCommands;
	private class_4185 buttonGameMode;
	private String gameMode = "survival";
	private boolean allowCommands;

	public OpenToLanScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new class_4185(this.screenWidth / 2 - 155, this.screenHeight - 28, 150, 20, I18n.translate("lanServer.start")) {
			@Override
			public void method_1826() {
				OpenToLanScreen.this.client.method_1507(null);
				int i = NetworkUtils.findLocalPort();
				TextComponent textComponent;
				if (OpenToLanScreen.this.client.method_1576().openToLan(GameMode.byName(OpenToLanScreen.this.gameMode), OpenToLanScreen.this.allowCommands, i)) {
					textComponent = new TranslatableTextComponent("commands.publish.started", i);
				} else {
					textComponent = new TranslatableTextComponent("commands.publish.failed");
				}

				OpenToLanScreen.this.client.field_1705.method_1743().method_1812(textComponent);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 + 5, this.screenHeight - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				OpenToLanScreen.this.client.method_1507(OpenToLanScreen.this.parent);
			}
		});
		this.buttonGameMode = this.addButton(new class_4185(this.screenWidth / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void method_1826() {
				if ("spectator".equals(OpenToLanScreen.this.gameMode)) {
					OpenToLanScreen.this.gameMode = "creative";
				} else if ("creative".equals(OpenToLanScreen.this.gameMode)) {
					OpenToLanScreen.this.gameMode = "adventure";
				} else if ("adventure".equals(OpenToLanScreen.this.gameMode)) {
					OpenToLanScreen.this.gameMode = "survival";
				} else {
					OpenToLanScreen.this.gameMode = "spectator";
				}

				OpenToLanScreen.this.updateButtonText();
			}
		});
		this.buttonAllowCommands = this.addButton(new class_4185(this.screenWidth / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void method_1826() {
				OpenToLanScreen.this.allowCommands = !OpenToLanScreen.this.allowCommands;
				OpenToLanScreen.this.updateButtonText();
			}
		});
		this.updateButtonText();
	}

	private void updateButtonText() {
		this.buttonGameMode.setText(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
		this.buttonAllowCommands.setText(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.allowCommands ? "options.on" : "options.off"));
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.title"), this.screenWidth / 2, 50, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.otherPlayers"), this.screenWidth / 2, 82, 16777215);
		super.draw(i, j, f);
	}
}
