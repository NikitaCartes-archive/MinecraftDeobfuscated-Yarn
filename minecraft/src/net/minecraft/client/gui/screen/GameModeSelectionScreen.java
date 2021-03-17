package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GameModeSelectionScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
	private static final int UI_WIDTH = GameModeSelectionScreen.GameMode.values().length * 31 - 5;
	private static final Text SELECT_NEXT_TEXT = new TranslatableText(
		"debug.gamemodes.select_next", new TranslatableText("debug.gamemodes.press_f4").formatted(Formatting.AQUA)
	);
	private final Optional<GameModeSelectionScreen.GameMode> currentGameMode;
	private Optional<GameModeSelectionScreen.GameMode> gameMode = Optional.empty();
	private int lastMouseX;
	private int lastMouseY;
	private boolean mouseUsedForSelection;
	private final List<GameModeSelectionScreen.ButtonWidget> gameModeButtons = Lists.<GameModeSelectionScreen.ButtonWidget>newArrayList();

	public GameModeSelectionScreen() {
		super(NarratorManager.EMPTY);
		this.currentGameMode = GameModeSelectionScreen.GameMode.of(this.getPreviousGameMode());
	}

	private net.minecraft.world.GameMode getPreviousGameMode() {
		ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
		net.minecraft.world.GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
		if (gameMode != null) {
			return gameMode;
		} else {
			return clientPlayerInteractionManager.getCurrentGameMode() == net.minecraft.world.GameMode.CREATIVE
				? net.minecraft.world.GameMode.SURVIVAL
				: net.minecraft.world.GameMode.CREATIVE;
		}
	}

	@Override
	protected void init() {
		super.init();
		this.gameMode = this.currentGameMode.isPresent()
			? this.currentGameMode
			: GameModeSelectionScreen.GameMode.of(this.client.interactionManager.getCurrentGameMode());

		for (int i = 0; i < GameModeSelectionScreen.GameMode.VALUES.length; i++) {
			GameModeSelectionScreen.GameMode gameMode = GameModeSelectionScreen.GameMode.VALUES[i];
			this.gameModeButtons.add(new GameModeSelectionScreen.ButtonWidget(gameMode, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
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

	private static void apply(MinecraftClient client, Optional<GameModeSelectionScreen.GameMode> gameMode) {
		if (client.interactionManager != null && client.player != null && gameMode.isPresent()) {
			Optional<GameModeSelectionScreen.GameMode> optional = GameModeSelectionScreen.GameMode.of(client.interactionManager.getCurrentGameMode());
			GameModeSelectionScreen.GameMode gameMode2 = (GameModeSelectionScreen.GameMode)gameMode.get();
			if (optional.isPresent() && client.player.hasPermissionLevel(2) && gameMode2 != optional.get()) {
				client.player.sendChatMessage(gameMode2.getCommand());
			}
		}
	}

	private boolean checkForClose() {
		if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) {
			this.apply();
			this.client.openScreen(null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_F4 && this.gameMode.isPresent()) {
			this.mouseUsedForSelection = false;
			this.gameMode = ((GameModeSelectionScreen.GameMode)this.gameMode.get()).next();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public class ButtonWidget extends AbstractButtonWidget {
		private final GameModeSelectionScreen.GameMode gameMode;
		private boolean selected;

		public ButtonWidget(GameModeSelectionScreen.GameMode gameMode, int x, int y) {
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
		public boolean isHovered() {
			return super.isHovered() || this.selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
			this.narrate();
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
	static enum GameMode {
		CREATIVE(new TranslatableText("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
		SURVIVAL(new TranslatableText("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
		ADVENTURE(new TranslatableText("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
		SPECTATOR(new TranslatableText("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

		protected static final GameModeSelectionScreen.GameMode[] VALUES = values();
		final Text text;
		final String command;
		final ItemStack icon;

		private GameMode(Text text, String command, ItemStack icon) {
			this.text = text;
			this.command = command;
			this.icon = icon;
		}

		private void renderIcon(ItemRenderer itemRenderer, int x, int y) {
			itemRenderer.renderInGuiWithOverrides(this.icon, x, y);
		}

		private Text getText() {
			return this.text;
		}

		private String getCommand() {
			return this.command;
		}

		private Optional<GameModeSelectionScreen.GameMode> next() {
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

		private static Optional<GameModeSelectionScreen.GameMode> of(net.minecraft.world.GameMode gameMode) {
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
