package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpectatorHud extends DrawableHelper implements SpectatorMenuCloseCallback {
	private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
	public static final Identifier SPECTATOR_TEXTURE = new Identifier("textures/gui/spectator_widgets.png");
	private final MinecraftClient client;
	private long lastInteractionTime;
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

	public void render(MatrixStack matrices, float f) {
		if (this.spectatorMenu != null) {
			float g = this.getSpectatorMenuHeight();
			if (g <= 0.0F) {
				this.spectatorMenu.close();
			} else {
				int i = this.client.getWindow().getScaledWidth() / 2;
				int j = this.getZOffset();
				this.setZOffset(-90);
				int k = MathHelper.floor((float)this.client.getWindow().getScaledHeight() - 22.0F * g);
				SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
				this.renderSpectatorMenu(matrices, g, i, k, spectatorMenuState);
				this.setZOffset(j);
			}
		}
	}

	protected void renderSpectatorMenu(MatrixStack matrices, float f, int i, int j, SpectatorMenuState spectatorMenuState) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		this.drawTexture(matrices, i - 91, j, 0, 0, 182, 22);
		if (spectatorMenuState.getSelectedSlot() >= 0) {
			this.drawTexture(matrices, i - 91 - 1 + spectatorMenuState.getSelectedSlot() * 20, j - 1, 0, 22, 24, 22);
		}

		for (int k = 0; k < 9; k++) {
			this.renderSpectatorCommand(matrices, k, this.client.getWindow().getScaledWidth() / 2 - 90 + k * 20 + 2, (float)(j + 3), f, spectatorMenuState.getCommand(k));
		}

		RenderSystem.disableBlend();
	}

	private void renderSpectatorCommand(MatrixStack matrices, int i, int j, float f, float g, SpectatorMenuCommand spectatorMenuCommand) {
		RenderSystem.setShaderTexture(0, SPECTATOR_TEXTURE);
		if (spectatorMenuCommand != SpectatorMenu.BLANK_COMMAND) {
			int k = (int)(g * 255.0F);
			matrices.push();
			matrices.translate((double)j, (double)f, 0.0);
			float h = spectatorMenuCommand.isEnabled() ? 1.0F : 0.25F;
			RenderSystem.setShaderColor(h, h, h, g);
			spectatorMenuCommand.renderIcon(matrices, h, k);
			matrices.pop();
			if (k > 3 && spectatorMenuCommand.isEnabled()) {
				Text text = this.client.options.keysHotbar[i].getBoundKeyLocalizedText();
				this.client
					.textRenderer
					.drawWithShadow(matrices, text, (float)(j + 19 - 2 - this.client.textRenderer.getWidth(text)), f + 6.0F + 3.0F, 16777215 + (k << 24));
			}
		}
	}

	public void render(MatrixStack matrices) {
		int i = (int)(this.getSpectatorMenuHeight() * 255.0F);
		if (i > 3 && this.spectatorMenu != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
			Text text = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt() : spectatorMenuCommand.getName();
			if (text != null) {
				int j = (this.client.getWindow().getScaledWidth() - this.client.textRenderer.getWidth(text)) / 2;
				int k = this.client.getWindow().getScaledHeight() - 35;
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				this.client.textRenderer.drawWithShadow(matrices, text, (float)j, (float)k, 16777215 + (i << 24));
				RenderSystem.disableBlend();
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

	public void cycleSlot(double offset) {
		int i = this.spectatorMenu.getSelectedSlot() + (int)offset;

		while (i >= 0 && i <= 8 && (this.spectatorMenu.getCommand(i) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(i).isEnabled())) {
			i = (int)((double)i + offset);
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
