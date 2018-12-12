package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenToLanGui extends Gui {
	private final Gui parent;
	private ButtonWidget buttonAllowCommands;
	private ButtonWidget buttonGameMode;
	private String gameMode = "survival";
	private boolean allowCommands;

	public OpenToLanGui(Gui gui) {
		this.parent = gui;
	}

	@Override
	protected void onInitialized() {
		this.addButton(new ButtonWidget(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start")) {
			@Override
			public void onPressed(double d, double e) {
				OpenToLanGui.this.client.openGui(null);
				int i = NetworkUtils.findLocalPort();
				TextComponent textComponent;
				if (OpenToLanGui.this.client.getServer().openToLan(GameMode.byName(OpenToLanGui.this.gameMode), OpenToLanGui.this.allowCommands, i)) {
					textComponent = new TranslatableTextComponent("commands.publish.started", i);
				} else {
					textComponent = new TranslatableTextComponent("commands.publish.failed");
				}

				OpenToLanGui.this.client.hudInGame.getHudChat().addMessage(textComponent);
			}
		});
		this.addButton(new ButtonWidget(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				OpenToLanGui.this.client.openGui(OpenToLanGui.this.parent);
			}
		});
		this.buttonGameMode = this.addButton(new ButtonWidget(104, this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void onPressed(double d, double e) {
				if ("spectator".equals(OpenToLanGui.this.gameMode)) {
					OpenToLanGui.this.gameMode = "creative";
				} else if ("creative".equals(OpenToLanGui.this.gameMode)) {
					OpenToLanGui.this.gameMode = "adventure";
				} else if ("adventure".equals(OpenToLanGui.this.gameMode)) {
					OpenToLanGui.this.gameMode = "survival";
				} else {
					OpenToLanGui.this.gameMode = "spectator";
				}

				OpenToLanGui.this.updateButtonText();
			}
		});
		this.buttonAllowCommands = this.addButton(new ButtonWidget(103, this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void onPressed(double d, double e) {
				OpenToLanGui.this.allowCommands = !OpenToLanGui.this.allowCommands;
				OpenToLanGui.this.updateButtonText();
			}
		});
		this.updateButtonText();
	}

	private void updateButtonText() {
		this.buttonGameMode.text = I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode);
		this.buttonAllowCommands.text = I18n.translate("selectWorld.allowCommands") + " ";
		if (this.allowCommands) {
			this.buttonAllowCommands.text = this.buttonAllowCommands.text + I18n.translate("options.on");
		} else {
			this.buttonAllowCommands.text = this.buttonAllowCommands.text + I18n.translate("options.off");
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.title"), this.width / 2, 50, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
		super.draw(i, j, f);
	}
}
