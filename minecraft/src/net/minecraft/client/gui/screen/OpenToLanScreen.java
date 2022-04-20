package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen {
	private static final Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
	private static final Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
	private static final Text OTHER_PLAYERS_TEXT = Text.translatable("lanServer.otherPlayers");
	private final Screen parent;
	private GameMode gameMode = GameMode.SURVIVAL;
	private boolean allowCommands;

	public OpenToLanScreen(Screen parent) {
		super(Text.translatable("lanServer.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.addDrawableChild(
			CyclingButtonWidget.<GameMode>builder(GameMode::getSimpleTranslatableName)
				.values(GameMode.SURVIVAL, GameMode.SPECTATOR, GameMode.CREATIVE, GameMode.ADVENTURE)
				.initially(this.gameMode)
				.build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button, gameMode) -> this.gameMode = gameMode)
		);
		this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.allowCommands)
				.build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button, allowCommands) -> this.allowCommands = allowCommands)
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, Text.translatable("lanServer.start"), button -> {
			this.client.setScreen(null);
			int i = NetworkUtils.findLocalPort();
			Text text;
			if (this.client.getServer().openToLan(this.gameMode, this.allowCommands, i)) {
				text = Text.translatable("commands.publish.started", i);
			} else {
				text = Text.translatable("commands.publish.failed");
			}

			this.client.inGameHud.getChatHud().addMessage(text);
			this.client.updateWindowTitle();
		}));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 50, 16777215);
		drawCenteredText(matrices, this.textRenderer, OTHER_PLAYERS_TEXT, this.width / 2, 82, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
