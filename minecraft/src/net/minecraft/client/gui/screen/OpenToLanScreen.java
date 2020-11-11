package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.GameModeSelection;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen {
	private static final Text ALLOW_COMMANDS_TEXT = new TranslatableText("selectWorld.allowCommands");
	private static final Text GAME_MODE_TEXT = new TranslatableText("selectWorld.gameMode");
	private static final Text OTHER_PLAYERS_TEXT = new TranslatableText("lanServer.otherPlayers");
	private final Screen parent;
	private GameModeSelection gameMode = GameModeSelection.SURVIVAL;
	private boolean allowCommands;

	public OpenToLanScreen(Screen parent) {
		super(new TranslatableText("lanServer.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.addButton(
			CyclingButtonWidget.<GameModeSelection>method_32606(GameModeSelection::getName)
				.method_32624(GameModeSelection.SURVIVAL, GameModeSelection.SPECTATOR, GameModeSelection.CREATIVE, GameModeSelection.ADVENTURE)
				.value(this.gameMode)
				.build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (cyclingButtonWidget, gameModeSelection) -> this.gameMode = gameModeSelection)
		);
		this.addButton(
			CyclingButtonWidget.method_32613(this.allowCommands)
				.build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (cyclingButtonWidget, boolean_) -> this.allowCommands = boolean_)
		);
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), buttonWidget -> {
			this.client.openScreen(null);
			int i = NetworkUtils.findLocalPort();
			Text text;
			if (this.client.getServer().openToLan(this.gameMode.getGameMode(), this.allowCommands, i)) {
				text = new TranslatableText("commands.publish.started", i);
			} else {
				text = new TranslatableText("commands.publish.failed");
			}

			this.client.inGameHud.getChatHud().addMessage(text);
			this.client.updateWindowTitle();
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 50, 16777215);
		drawCenteredText(matrices, this.textRenderer, OTHER_PLAYERS_TEXT, this.width / 2, 82, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
