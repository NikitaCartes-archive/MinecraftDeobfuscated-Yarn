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
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpectatorHud extends DrawableHelper implements SpectatorMenuCloseCallback {
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
	public static final Identifier SPECTATOR_TEX = new Identifier("textures/gui/spectator_widgets.png");
	private final MinecraftClient client;
	private long lastKeyPressTime;
	private SpectatorMenu spectatorMenu;

	public SpectatorHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void onHotbarKeyPress(int i) {
		this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
		if (this.spectatorMenu != null) {
			this.spectatorMenu.setSelectedSlot(i);
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}

	private float getSpectatorMenuHeight() {
		long l = this.lastKeyPressTime - SystemUtil.getMeasuringTimeMs() + 5000L;
		return MathHelper.clamp((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void render(float f) {
		if (this.spectatorMenu != null) {
			float g = this.getSpectatorMenuHeight();
			if (g <= 0.0F) {
				this.spectatorMenu.close();
			} else {
				int i = this.client.window.getScaledWidth() / 2;
				int j = this.blitOffset;
				this.blitOffset = -90;
				int k = MathHelper.floor((float)this.client.window.getScaledHeight() - 22.0F * g);
				SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
				this.renderSpectatorMenu(g, i, k, spectatorMenuState);
				this.blitOffset = j;
			}
		}
	}

	protected void renderSpectatorMenu(float f, int i, int j, SpectatorMenuState spectatorMenuState) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(WIDGETS_TEX);
		this.blit(i - 91, j, 0, 0, 182, 22);
		if (spectatorMenuState.getSelectedSlot() >= 0) {
			this.blit(i - 91 - 1 + spectatorMenuState.getSelectedSlot() * 20, j - 1, 0, 22, 24, 22);
		}

		GuiLighting.enableForItems();

		for (int k = 0; k < 9; k++) {
			this.renderSpectatorCommand(k, this.client.window.getScaledWidth() / 2 - 90 + k * 20 + 2, (float)(j + 3), f, spectatorMenuState.getCommand(k));
		}

		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void renderSpectatorCommand(int i, int j, float f, float g, SpectatorMenuCommand spectatorMenuCommand) {
		this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
		if (spectatorMenuCommand != SpectatorMenu.BLANK_COMMAND) {
			int k = (int)(g * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)j, f, 0.0F);
			float h = spectatorMenuCommand.isEnabled() ? 1.0F : 0.25F;
			GlStateManager.color4f(h, h, h, g);
			spectatorMenuCommand.renderIcon(h, k);
			GlStateManager.popMatrix();
			String string = String.valueOf(this.client.options.keysHotbar[i].getLocalizedName());
			if (k > 3 && spectatorMenuCommand.isEnabled()) {
				this.client
					.textRenderer
					.drawWithShadow(string, (float)(j + 19 - 2 - this.client.textRenderer.getStringWidth(string)), f + 6.0F + 3.0F, 16777215 + (k << 24));
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
	public void close(SpectatorMenu spectatorMenu) {
		this.spectatorMenu = null;
		this.lastKeyPressTime = 0L;
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
			this.spectatorMenu.setSelectedSlot(i);
			this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
		}
	}

	public void method_1983() {
		this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
		if (this.method_1980()) {
			int i = this.spectatorMenu.getSelectedSlot();
			if (i != -1) {
				this.spectatorMenu.setSelectedSlot(i);
			}
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}
}
