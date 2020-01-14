package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen {
	private final Screen parent;
	private ButtonWidget buttonAllowCommands;
	private ButtonWidget buttonGameMode;
	private String gameMode = "survival";
	private boolean allowCommands;

	public OpenToLanScreen(Screen parent) {
		super(new TranslatableText("lanServer.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start"), buttonWidget -> {
			this.minecraft.openScreen(null);
			int i = NetworkUtils.findLocalPort();
			Text text;
			if (this.minecraft.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i)) {
				text = new TranslatableText("commands.publish.started", i);
			} else {
				text = new TranslatableText("commands.publish.failed");
			}

			this.minecraft.inGameHud.getChatHud().addMessage(text);
			this.minecraft.method_24288();
		}));
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.buttonGameMode = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
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
		this.buttonAllowCommands = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
			this.allowCommands = !this.allowCommands;
			this.updateButtonText();
		}));
		this.updateButtonText();
	}

	private void updateButtonText() {
		this.buttonGameMode.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
		this.buttonAllowCommands.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.allowCommands ? "options.on" : "options.off"));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 50, 16777215);
		this.drawCenteredString(this.font, I18n.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
		super.render(mouseX, mouseY, delta);
	}
}
