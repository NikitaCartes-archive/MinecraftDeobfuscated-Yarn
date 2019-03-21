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
		super(new TranslatableTextComponent("lanServer.title"));
		this.parent = screen;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight - 28, 150, 20, I18n.translate("lanServer.start"), buttonWidget -> {
			this.client.openScreen(null);
			int i = NetworkUtils.findLocalPort();
			TextComponent textComponent;
			if (this.client.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i)) {
				textComponent = new TranslatableTextComponent("commands.publish.started", i);
			} else {
				textComponent = new TranslatableTextComponent("commands.publish.failed");
			}

			this.client.inGameHud.getChatHud().addMessage(textComponent);
		}));
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 + 5, this.screenHeight - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.client.openScreen(this.parent)
			)
		);
		this.buttonGameMode = this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
			if ("spectator".equals(this.gameMode)) {
				this.gameMode = "creative";
			} else if ("creative".equals(this.gameMode)) {
				this.gameMode = "adventure";
			} else if ("adventure".equals(this.gameMode)) {
				this.gameMode = "survival";
			} else {
				this.gameMode = "spectator";
			}

			this.updateButtonText();
		}));
		this.buttonAllowCommands = this.addButton(
			new ButtonWidget(this.screenWidth / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
				this.allowCommands = !this.allowCommands;
				this.updateButtonText();
			})
		);
		this.updateButtonText();
	}

	private void updateButtonText() {
		this.buttonGameMode.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
		this.buttonAllowCommands.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.allowCommands ? "options.on" : "options.off"));
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 50, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.otherPlayers"), this.screenWidth / 2, 82, 16777215);
		super.render(i, j, f);
	}
}
