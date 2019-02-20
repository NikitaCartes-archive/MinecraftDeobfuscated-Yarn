package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen {
	private final Screen parent;
	private ButtonWidget buttonAllowCommands;
	private ButtonWidget buttonGameMode;
	private String gameMode = "survival";
	private boolean allowCommands;

	public OpenToLanScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start")) {
			@Override
			public void onPressed(double d, double e) {
				OpenToLanScreen.this.client.openScreen(null);
				int i = NetworkUtils.findLocalPort();
				TextComponent textComponent;
				if (OpenToLanScreen.this.client.getServer().openToLan(GameMode.byName(OpenToLanScreen.this.gameMode), OpenToLanScreen.this.allowCommands, i)) {
					textComponent = new TranslatableTextComponent("commands.publish.started", i);
				} else {
					textComponent = new TranslatableTextComponent("commands.publish.failed");
				}

				OpenToLanScreen.this.client.inGameHud.getChatHud().addMessage(textComponent);
			}
		});
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				OpenToLanScreen.this.client.openScreen(OpenToLanScreen.this.parent);
			}
		});
		this.buttonGameMode = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void onPressed(double d, double e) {
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
		this.buttonAllowCommands = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void onPressed(double d, double e) {
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
	public void method_18326(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.title"), this.width / 2, 50, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
		super.method_18326(i, j, f);
	}
}
