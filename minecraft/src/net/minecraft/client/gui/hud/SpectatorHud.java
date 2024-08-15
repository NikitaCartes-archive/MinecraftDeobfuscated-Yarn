package net.minecraft.client.gui.hud;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpectatorHud implements SpectatorMenuCloseCallback {
	private static final Identifier HOTBAR_TEXTURE = Identifier.ofVanilla("hud/hotbar");
	private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.ofVanilla("hud/hotbar_selection");
	private static final long FADE_OUT_DELAY = 5000L;
	private static final long FADE_OUT_DURATION = 2000L;
	private final MinecraftClient client;
	private long lastInteractionTime;
	@Nullable
	private SpectatorMenu spectatorMenu;

	public SpectatorHud(MinecraftClient client) {
		this.client = client;
	}

	public void selectSlot(int slot) {
		this.lastInteractionTime = Util.getMeasuringTimeMs();
		if (this.spectatorMenu != null) {
			this.spectatorMenu.useCommand(slot);
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}

	private float getSpectatorMenuHeight() {
		long l = this.lastInteractionTime - Util.getMeasuringTimeMs() + 5000L;
		return MathHelper.clamp((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void renderSpectatorMenu(DrawContext context) {
		if (this.spectatorMenu != null) {
			float f = this.getSpectatorMenuHeight();
			if (f <= 0.0F) {
				this.spectatorMenu.close();
			} else {
				int i = context.getScaledWindowWidth() / 2;
				context.getMatrices().push();
				context.getMatrices().translate(0.0F, 0.0F, -90.0F);
				int j = MathHelper.floor((float)context.getScaledWindowHeight() - 22.0F * f);
				SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
				this.renderSpectatorMenu(context, f, i, j, spectatorMenuState);
				context.getMatrices().pop();
			}
		}
	}

	protected void renderSpectatorMenu(DrawContext context, float height, int x, int y, SpectatorMenuState state) {
		int i = ColorHelper.getWhite(height);
		context.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_TEXTURE, x - 91, y, 182, 22, i);
		if (state.getSelectedSlot() >= 0) {
			context.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_SELECTION_TEXTURE, x - 91 - 1 + state.getSelectedSlot() * 20, y - 1, 24, 23, i);
		}

		for (int j = 0; j < 9; j++) {
			this.renderSpectatorCommand(context, j, context.getScaledWindowWidth() / 2 - 90 + j * 20 + 2, (float)(y + 3), height, state.getCommand(j));
		}
	}

	private void renderSpectatorCommand(DrawContext drawContext, int slot, int x, float y, float height, SpectatorMenuCommand spectatorMenuCommand) {
		if (spectatorMenuCommand != SpectatorMenu.BLANK_COMMAND) {
			drawContext.getMatrices().push();
			drawContext.getMatrices().translate((float)x, y, 0.0F);
			float f = spectatorMenuCommand.isEnabled() ? 1.0F : 0.25F;
			spectatorMenuCommand.renderIcon(drawContext, f, height);
			drawContext.getMatrices().pop();
			int i = (int)(height * 255.0F);
			if (i > 3 && spectatorMenuCommand.isEnabled()) {
				Text text = this.client.options.hotbarKeys[slot].getBoundKeyLocalizedText();
				drawContext.drawTextWithShadow(this.client.textRenderer, text, x + 19 - 2 - this.client.textRenderer.getWidth(text), (int)y + 6 + 3, 16777215 + (i << 24));
			}
		}
	}

	public void render(DrawContext context) {
		int i = (int)(this.getSpectatorMenuHeight() * 255.0F);
		if (i > 3 && this.spectatorMenu != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
			Text text = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt() : spectatorMenuCommand.getName();
			if (text != null) {
				int j = this.client.textRenderer.getWidth(text);
				int k = (context.getScaledWindowWidth() - j) / 2;
				int l = context.getScaledWindowHeight() - 35;
				context.drawTextWithBackground(this.client.textRenderer, text, k, l, j, ColorHelper.withAlpha(i, Colors.WHITE));
			}
		}
	}

	@Override
	public void close(SpectatorMenu menu) {
		this.spectatorMenu = null;
		this.lastInteractionTime = 0L;
	}

	public boolean isOpen() {
		return this.spectatorMenu != null;
	}

	public void cycleSlot(int offset) {
		int i = this.spectatorMenu.getSelectedSlot() + offset;

		while (i >= 0 && i <= 8 && (this.spectatorMenu.getCommand(i) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(i).isEnabled())) {
			i += offset;
		}

		if (i >= 0 && i <= 8) {
			this.spectatorMenu.useCommand(i);
			this.lastInteractionTime = Util.getMeasuringTimeMs();
		}
	}

	public void useSelectedCommand() {
		this.lastInteractionTime = Util.getMeasuringTimeMs();
		if (this.isOpen()) {
			int i = this.spectatorMenu.getSelectedSlot();
			if (i != -1) {
				this.spectatorMenu.useCommand(i);
			}
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}
}
