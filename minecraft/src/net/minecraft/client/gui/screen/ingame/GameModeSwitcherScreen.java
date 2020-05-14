package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GameModeSwitcherScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
	private static final int WIDTH = GameModeSwitcherScreen.GameMode.values().length * 30 - 5;
	private final Optional<GameModeSwitcherScreen.GameMode> previousGameMode;
	private Optional<GameModeSwitcherScreen.GameMode> selectedGameMode = Optional.empty();
	private int cachedMouseX;
	private int cachedMouseY;
	private boolean useMouse;
	private final List<GameModeSwitcherScreen.Button> gameModeButtons = Lists.<GameModeSwitcherScreen.Button>newArrayList();

	public GameModeSwitcherScreen() {
		super(NarratorManager.EMPTY);
		this.previousGameMode = GameModeSwitcherScreen.GameMode.fromGameMode(MinecraftClient.getInstance().interactionManager.getPreviousGameMode());
	}

	@Override
	protected void init() {
		super.init();
		this.selectedGameMode = this.previousGameMode.isPresent()
			? this.previousGameMode
			: GameModeSwitcherScreen.GameMode.fromGameMode(this.client.interactionManager.getCurrentGameMode());

		for (int i = 0; i < GameModeSwitcherScreen.GameMode.MODES.length; i++) {
			GameModeSwitcherScreen.GameMode gameMode = GameModeSwitcherScreen.GameMode.MODES[i];
			this.gameModeButtons.add(new GameModeSwitcherScreen.Button(gameMode, this.width / 2 - WIDTH / 2 + i * 30, this.height / 2 - 30));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!this.shouldClose()) {
			matrices.push();
			RenderSystem.enableBlend();
			this.client.getTextureManager().bindTexture(TEXTURE);
			int i = this.width / 2 - 62;
			int j = this.height / 2 - 30 - 27;
			drawTexture(matrices, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
			matrices.pop();
			super.render(matrices, mouseX, mouseY, delta);
			this.selectedGameMode
				.ifPresent(gameMode -> this.drawCenteredText(matrices, this.textRenderer, gameMode.getName(), this.width / 2, this.height / 2 - 30 - 20, -1));
			int k = this.textRenderer.getWidth(I18n.translate("debug.gamemodes.press_f4"));
			this.drawText(matrices, I18n.translate("debug.gamemodes.press_f4"), I18n.translate("debug.gamemodes.select_next"), 5, k);
			if (!this.useMouse) {
				this.cachedMouseX = mouseX;
				this.cachedMouseY = mouseY;
				this.useMouse = true;
			}

			boolean bl = this.cachedMouseX == mouseX && this.cachedMouseY == mouseY;

			for (GameModeSwitcherScreen.Button button : this.gameModeButtons) {
				button.render(matrices, mouseX, mouseY, delta);
				this.selectedGameMode.ifPresent(gameMode -> button.select(gameMode == button.gameMode));
				if (!bl && button.isHovered()) {
					this.selectedGameMode = Optional.of(button.gameMode);
				}
			}
		}
	}

	private void drawText(MatrixStack matrices, String pressF4, String selectNext, int y, int x) {
		int i = 5636095;
		int j = 16777215;
		this.drawStringWithShadow(matrices, this.textRenderer, "[", this.width / 2 - x - 18, this.height / 2 + y, 5636095);
		this.drawCenteredString(matrices, this.textRenderer, pressF4, this.width / 2 - x / 2 - 10, this.height / 2 + y, 5636095);
		this.drawCenteredString(matrices, this.textRenderer, "]", this.width / 2 - 5, this.height / 2 + y, 5636095);
		this.drawStringWithShadow(matrices, this.textRenderer, selectNext, this.width / 2 + 5, this.height / 2 + y, 16777215);
	}

	private void onClose() {
		switchGameMode(this.client, this.selectedGameMode);
	}

	private static void switchGameMode(MinecraftClient client, Optional<GameModeSwitcherScreen.GameMode> gameMode) {
		if (client.interactionManager != null && client.player != null && gameMode.isPresent()) {
			Optional<GameModeSwitcherScreen.GameMode> optional = GameModeSwitcherScreen.GameMode.fromGameMode(client.interactionManager.getCurrentGameMode());
			GameModeSwitcherScreen.GameMode gameMode2 = (GameModeSwitcherScreen.GameMode)gameMode.get();
			if (optional.isPresent() && client.player.hasPermissionLevel(2) && gameMode2 != optional.get()) {
				client.player.sendChatMessage(gameMode2.getCommand());
			}
		}
	}

	private boolean shouldClose() {
		if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), 292)) {
			this.onClose();
			this.client.openScreen(null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 293 && this.selectedGameMode.isPresent()) {
			this.useMouse = false;
			this.selectedGameMode = ((GameModeSwitcherScreen.GameMode)this.selectedGameMode.get()).getNext();
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
	public class Button extends AbstractButtonWidget {
		private final GameModeSwitcherScreen.GameMode gameMode;
		private boolean selected;

		public Button(GameModeSwitcherScreen.GameMode gameMode, int x, int y) {
			super(x, y, 25, 25, gameMode.getName());
			this.gameMode = gameMode;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			this.render(matrices, minecraftClient.getTextureManager());
			this.gameMode.renderIcon(GameModeSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
			if (this.selected) {
				this.renderSelected(matrices, minecraftClient.getTextureManager());
			}
		}

		@Override
		public boolean isHovered() {
			return super.isHovered() || this.selected;
		}

		public void select(boolean selected) {
			this.selected = selected;
			this.narrate();
		}

		private void render(MatrixStack matrices, TextureManager textureManager) {
			textureManager.bindTexture(GameModeSwitcherScreen.TEXTURE);
			matrices.push();
			matrices.translate((double)this.x, (double)this.y, 0.0);
			drawTexture(matrices, 0, 0, 0.0F, 75.0F, 25, 25, 128, 128);
			matrices.pop();
		}

		private void renderSelected(MatrixStack matrices, TextureManager textureManager) {
			textureManager.bindTexture(GameModeSwitcherScreen.TEXTURE);
			matrices.push();
			matrices.translate((double)this.x, (double)this.y, 0.0);
			drawTexture(matrices, 0, 0, 25.0F, 75.0F, 25, 25, 128, 128);
			matrices.pop();
		}
	}

	@Environment(EnvType.CLIENT)
	static enum GameMode {
		CREATIVE(new TranslatableText("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
		SURVIVAL(new TranslatableText("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
		ADVENTURE(new TranslatableText("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
		SPECTATOR(new TranslatableText("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

		protected static final GameModeSwitcherScreen.GameMode[] MODES = values();
		final Text name;
		final String command;
		final ItemStack icon;

		private GameMode(Text name, String command, ItemStack icon) {
			this.name = name;
			this.command = command;
			this.icon = icon;
		}

		private void renderIcon(ItemRenderer itemRenderer, int x, int y) {
			itemRenderer.renderGuiItem(this.icon, x, y);
		}

		private Text getName() {
			return this.name;
		}

		private String getCommand() {
			return this.command;
		}

		private Optional<GameModeSwitcherScreen.GameMode> getNext() {
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

		private static Optional<GameModeSwitcherScreen.GameMode> fromGameMode(net.minecraft.world.GameMode gameMode) {
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
