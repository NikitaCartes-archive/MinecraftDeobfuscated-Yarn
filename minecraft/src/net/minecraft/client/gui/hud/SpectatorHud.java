package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpectatorHud extends DrawableHelper implements SpectatorMenuCloseCallback {
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
	public static final Identifier SPECTATOR_TEX = new Identifier("textures/gui/spectator_widgets.png");
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

	public void render(float tickDelta) {
		if (this.spectatorMenu != null) {
			float f = this.getSpectatorMenuHeight();
			if (f <= 0.0F) {
				this.spectatorMenu.close();
			} else {
				int i = this.client.window.getScaledWidth() / 2;
				int j = this.blitOffset;
				this.blitOffset = -90;
				int k = MathHelper.floor((float)this.client.window.getScaledHeight() - 22.0F * f);
				SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
				this.renderSpectatorMenu(f, i, k, spectatorMenuState);
				this.blitOffset = j;
			}
		}
	}

	protected void renderSpectatorMenu(float height, int x, int i, SpectatorMenuState state) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, height);
		this.client.getTextureManager().bindTexture(WIDGETS_TEX);
		this.blit(x - 91, i, 0, 0, 182, 22);
		if (state.getSelectedSlot() >= 0) {
			this.blit(x - 91 - 1 + state.getSelectedSlot() * 20, i - 1, 0, 22, 24, 22);
		}

		DiffuseLighting.enableForItems();

		for (int j = 0; j < 9; j++) {
			this.renderSpectatorCommand(j, this.client.window.getScaledWidth() / 2 - 90 + j * 20 + 2, (float)(i + 3), height, state.getCommand(j));
		}

		DiffuseLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void renderSpectatorCommand(int slot, int x, float y, float alpha, SpectatorMenuCommand command) {
		this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
		if (command != SpectatorMenu.BLANK_COMMAND) {
			int i = (int)(alpha * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)x, y, 0.0F);
			float f = command.isEnabled() ? 1.0F : 0.25F;
			GlStateManager.color4f(f, f, f, alpha);
			command.renderIcon(f, i);
			GlStateManager.popMatrix();
			String string = String.valueOf(this.client.options.keysHotbar[slot].getLocalizedName());
			if (i > 3 && command.isEnabled()) {
				this.client
					.textRenderer
					.drawWithShadow(string, (float)(x + 19 - 2 - this.client.textRenderer.getStringWidth(string)), y + 6.0F + 3.0F, 16777215 + (i << 24));
			}
		}
	}

	public void render() {
		int i = (int)(this.getSpectatorMenuHeight() * 255.0F);
		if (i > 3 && this.spectatorMenu != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
			String string = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND
				? this.spectatorMenu.getCurrentGroup().getPrompt().asFormattedString()
				: spectatorMenuCommand.getName().asFormattedString();
			if (string != null) {
				int j = (this.client.window.getScaledWidth() - this.client.textRenderer.getStringWidth(string)) / 2;
				int k = this.client.window.getScaledHeight() - 35;
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				this.client.textRenderer.drawWithShadow(string, (float)j, (float)k, 16777215 + (i << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void close(SpectatorMenu menu) {
		this.spectatorMenu = null;
		this.lastInteractionTime = 0L;
	}

	public boolean method_1980() {
		return this.spectatorMenu != null;
	}

	public void method_1976(double d) {
		int i = this.spectatorMenu.getSelectedSlot() + (int)d;

		while (i >= 0 && i <= 8 && (this.spectatorMenu.getCommand(i) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(i).isEnabled())) {
			i = (int)((double)i + d);
		}

		if (i >= 0 && i <= 8) {
			this.spectatorMenu.useCommand(i);
			this.lastInteractionTime = Util.getMeasuringTimeMs();
		}
	}

	public void method_1983() {
		this.lastInteractionTime = Util.getMeasuringTimeMs();
		if (this.method_1980()) {
			int i = this.spectatorMenu.getSelectedSlot();
			if (i != -1) {
				this.spectatorMenu.useCommand(i);
			}
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}
}
