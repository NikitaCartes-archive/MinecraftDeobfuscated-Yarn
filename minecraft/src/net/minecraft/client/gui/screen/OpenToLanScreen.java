package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
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
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), buttonWidget -> {
			this.client.openScreen(null);
			int i = NetworkUtils.findLocalPort();
			Text text;
			if (this.client.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, i)) {
				text = new TranslatableText("commands.publish.started", i);
			} else {
				text = new TranslatableText("commands.publish.failed");
			}

			this.client.inGameHud.getChatHud().addMessage(text);
			this.client.updateWindowTitle();
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.buttonGameMode = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.gameMode"), buttonWidget -> {
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
			new ButtonWidget(this.width / 2 + 5, 100, 150, 20, new TranslatableText("selectWorld.allowCommands"), buttonWidget -> {
				this.allowCommands = !this.allowCommands;
				this.updateButtonText();
			})
		);
		this.updateButtonText();
	}

	private void updateButtonText() {
		this.buttonGameMode
			.setMessage(new TranslatableText("selectWorld.gameMode").append(": ").append(new TranslatableText("selectWorld.gameMode." + this.gameMode)));
		this.buttonAllowCommands.setMessage(new TranslatableText("selectWorld.allowCommands").append(" ").append(ScreenTexts.getToggleText(this.allowCommands)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawStringWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 50, 16777215);
		this.drawCenteredString(matrices, this.textRenderer, I18n.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
