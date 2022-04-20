package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GameModeSelectionScreen extends Screen {
	static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
	private static final int TEXTURE_WIDTH = 128;
	private static final int TEXTURE_HEIGHT = 128;
	private static final int BUTTON_SIZE = 26;
	private static final int ICON_OFFSET = 5;
	private static final int field_32314 = 31;
	private static final int field_32315 = 5;
	private static final int UI_WIDTH = GameModeSelectionScreen.GameModeSelection.values().length * 31 - 5;
	private static final Text SELECT_NEXT_TEXT = Text.method_43469(
		"debug.gamemodes.select_next", Text.method_43471("debug.gamemodes.press_f4").formatted(Formatting.AQUA)
	);
	private final Optional<GameModeSelectionScreen.GameModeSelection> currentGameMode;
	private Optional<GameModeSelectionScreen.GameModeSelection> gameMode = Optional.empty();
	private int lastMouseX;
	private int lastMouseY;
	private boolean mouseUsedForSelection;
	private final List<GameModeSelectionScreen.ButtonWidget> gameModeButtons = Lists.<GameModeSelectionScreen.ButtonWidget>newArrayList();

	public GameModeSelectionScreen() {
		super(NarratorManager.EMPTY);
		this.currentGameMode = GameModeSelectionScreen.GameModeSelection.of(this.getPreviousGameMode());
	}

	private GameMode getPreviousGameMode() {
		ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
		GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
		if (gameMode != null) {
			return gameMode;
		} else {
			return clientPlayerInteractionManager.getCurrentGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
		}
	}

	@Override
	protected void init() {
		super.init();
		this.gameMode = this.currentGameMode.isPresent()
			? this.currentGameMode
			: GameModeSelectionScreen.GameModeSelection.of(this.client.interactionManager.getCurrentGameMode());

		for (int i = 0; i < GameModeSelectionScreen.GameModeSelection.VALUES.length; i++) {
			GameModeSelectionScreen.GameModeSelection gameModeSelection = GameModeSelectionScreen.GameModeSelection.VALUES[i];
			this.gameModeButtons.add(new GameModeSelectionScreen.ButtonWidget(gameModeSelection, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!this.checkForClose()) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			matrices.push();
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, TEXTURE);
			int i = this.width / 2 - 62;
			int j = this.height / 2 - 31 - 27;
			drawTexture(matrices, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
			matrices.pop();
			super.render(matrices, mouseX, mouseY, delta);
			this.gameMode.ifPresent(gameMode -> drawCenteredText(matrices, this.textRenderer, gameMode.getText(), this.width / 2, this.height / 2 - 31 - 20, -1));
			drawCenteredText(matrices, this.textRenderer, SELECT_NEXT_TEXT, this.width / 2, this.height / 2 + 5, 16777215);
			if (!this.mouseUsedForSelection) {
				this.lastMouseX = mouseX;
				this.lastMouseY = mouseY;
				this.mouseUsedForSelection = true;
			}

			boolean bl = this.lastMouseX == mouseX && this.lastMouseY == mouseY;

			for (GameModeSelectionScreen.ButtonWidget buttonWidget : this.gameModeButtons) {
				buttonWidget.render(matrices, mouseX, mouseY, delta);
				this.gameMode.ifPresent(gameMode -> buttonWidget.setSelected(gameMode == buttonWidget.gameMode));
				if (!bl && buttonWidget.isHovered()) {
					this.gameMode = Optional.of(buttonWidget.gameMode);
				}
			}
		}
	}

	private void apply() {
		apply(this.client, this.gameMode);
	}

	private static void apply(MinecraftClient client, Optional<GameModeSelectionScreen.GameModeSelection> gameMode) {
		if (client.interactionManager != null && client.player != null && gameMode.isPresent()) {
			Optional<GameModeSelectionScreen.GameModeSelection> optional = GameModeSelectionScreen.GameModeSelection.of(client.interactionManager.getCurrentGameMode());
			GameModeSelectionScreen.GameModeSelection gameModeSelection = (GameModeSelectionScreen.GameModeSelection)gameMode.get();
			if (optional.isPresent() && client.player.hasPermissionLevel(2) && gameModeSelection != optional.get()) {
				client.player.sendChatMessage(gameModeSelection.getCommand());
			}
		}
	}

	private boolean checkForClose() {
		if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) {
			this.apply();
			this.client.setScreen(null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_F4 && this.gameMode.isPresent()) {
			this.mouseUsedForSelection = false;
			this.gameMode = ((GameModeSelectionScreen.GameModeSelection)this.gameMode.get()).next();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public class ButtonWidget extends ClickableWidget {
		final GameModeSelectionScreen.GameModeSelection gameMode;
		private boolean selected;

		public ButtonWidget(GameModeSelectionScreen.GameModeSelection gameMode, int x, int y) {
			super(x, y, 26, 26, gameMode.getText());
			this.gameMode = gameMode;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			this.drawBackground(matrices, minecraftClient.getTextureManager());
			this.gameMode.renderIcon(GameModeSelectionScreen.this.itemRenderer, this.x + 5, this.y + 5);
			if (this.selected) {
				this.drawSelectionBox(matrices, minecraftClient.getTextureManager());
			}
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {
			this.appendDefaultNarrations(builder);
		}

		@Override
		public boolean isHovered() {
			return super.isHovered() || this.selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		private void drawBackground(MatrixStack matrices, TextureManager textureManager) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GameModeSelectionScreen.TEXTURE);
			matrices.push();
			matrices.translate((double)this.x, (double)this.y, 0.0);
			drawTexture(matrices, 0, 0, 0.0F, 75.0F, 26, 26, 128, 128);
			matrices.pop();
		}

		private void drawSelectionBox(MatrixStack matrices, TextureManager textureManager) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GameModeSelectionScreen.TEXTURE);
			matrices.push();
			matrices.translate((double)this.x, (double)this.y, 0.0);
			drawTexture(matrices, 0, 0, 26.0F, 75.0F, 26, 26, 128, 128);
			matrices.pop();
		}
	}

	@Environment(EnvType.CLIENT)
	static enum GameModeSelection {
		CREATIVE(Text.method_43471("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
		SURVIVAL(Text.method_43471("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
		ADVENTURE(Text.method_43471("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
		SPECTATOR(Text.method_43471("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

		protected static final GameModeSelectionScreen.GameModeSelection[] VALUES = values();
		private static final int field_32317 = 16;
		protected static final int field_32316 = 5;
		final Text text;
		final String command;
		final ItemStack icon;

		private GameModeSelection(Text text, String command, ItemStack icon) {
			this.text = text;
			this.command = command;
			this.icon = icon;
		}

		void renderIcon(ItemRenderer itemRenderer, int x, int y) {
			itemRenderer.renderInGuiWithOverrides(this.icon, x, y);
		}

		Text getText() {
			return this.text;
		}

		String getCommand() {
			return this.command;
		}

		Optional<GameModeSelectionScreen.GameModeSelection> next() {
			switch (this) {
				case CREATIVE:
					return Optional.of(SURVIVAL);
				case SURVIVAL:
					return Optional.of(ADVENTURE);
				case ADVENTURE:
					return Optional.of(SPECTATOR);
				default:
					return Optional.of(CREATIVE);
			}
		}

		static Optional<GameModeSelectionScreen.GameModeSelection> of(GameMode gameMode) {
			switch (gameMode) {
				case SPECTATOR:
					return Optional.of(SPECTATOR);
				case SURVIVAL:
					return Optional.of(SURVIVAL);
				case CREATIVE:
					return Optional.of(CREATIVE);
				case ADVENTURE:
					return Optional.of(ADVENTURE);
				default:
					return Optional.empty();
			}
		}
	}
}
