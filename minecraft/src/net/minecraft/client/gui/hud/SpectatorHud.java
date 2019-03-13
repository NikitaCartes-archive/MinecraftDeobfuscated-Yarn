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
	private static final Identifier field_2197 = new Identifier("textures/gui/widgets.png");
	public static final Identifier field_2199 = new Identifier("textures/gui/spectator_widgets.png");
	private final MinecraftClient client;
	private long lastKeyPressTime;
	private SpectatorMenu field_2200;

	public SpectatorHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void onHotbarKeyPress(int i) {
		this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
		if (this.field_2200 != null) {
			this.field_2200.setSelectedSlot(i);
		} else {
			this.field_2200 = new SpectatorMenu(this);
		}
	}

	private float getSpectatorMenuHeight() {
		long l = this.lastKeyPressTime - SystemUtil.getMeasuringTimeMs() + 5000L;
		return MathHelper.clamp((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void draw(float f) {
		if (this.field_2200 != null) {
			float g = this.getSpectatorMenuHeight();
			if (g <= 0.0F) {
				this.field_2200.close();
			} else {
				int i = this.client.window.getScaledWidth() / 2;
				float h = this.zOffset;
				this.zOffset = -90.0F;
				float j = (float)this.client.window.getScaledHeight() - 22.0F * g;
				SpectatorMenuState spectatorMenuState = this.field_2200.method_2772();
				this.method_1975(g, i, j, spectatorMenuState);
				this.zOffset = h;
			}
		}
	}

	protected void method_1975(float f, int i, float g, SpectatorMenuState spectatorMenuState) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
		this.client.method_1531().method_4618(field_2197);
		this.drawTexturedRect((float)(i - 91), g, 0, 0, 182, 22);
		if (spectatorMenuState.getSelectedSlot() >= 0) {
			this.drawTexturedRect((float)(i - 91 - 1 + spectatorMenuState.getSelectedSlot() * 20), g - 1.0F, 0, 22, 24, 22);
		}

		GuiLighting.enableForItems();

		for (int j = 0; j < 9; j++) {
			this.method_1982(j, this.client.window.getScaledWidth() / 2 - 90 + j * 20 + 2, g + 3.0F, f, spectatorMenuState.getCommand(j));
		}

		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void method_1982(int i, int j, float f, float g, SpectatorMenuCommand spectatorMenuCommand) {
		this.client.method_1531().method_4618(field_2199);
		if (spectatorMenuCommand != SpectatorMenu.field_3260) {
			int k = (int)(g * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)j, f, 0.0F);
			float h = spectatorMenuCommand.enabled() ? 1.0F : 0.25F;
			GlStateManager.color4f(h, h, h, g);
			spectatorMenuCommand.renderIcon(h, k);
			GlStateManager.popMatrix();
			String string = String.valueOf(this.client.field_1690.keysHotbar[i].getLocalizedName());
			if (k > 3 && spectatorMenuCommand.enabled()) {
				this.client.field_1772.drawWithShadow(string, (float)(j + 19 - 2 - this.client.field_1772.getStringWidth(string)), f + 6.0F + 3.0F, 16777215 + (k << 24));
			}
		}
	}

	public void draw() {
		int i = (int)(this.getSpectatorMenuHeight() * 255.0F);
		if (i > 3 && this.field_2200 != null) {
			SpectatorMenuCommand spectatorMenuCommand = this.field_2200.method_2774();
			String string = spectatorMenuCommand == SpectatorMenu.field_3260
				? this.field_2200.method_2776().method_2781().getFormattedText()
				: spectatorMenuCommand.method_16892().getFormattedText();
			if (string != null) {
				int j = (this.client.window.getScaledWidth() - this.client.field_1772.getStringWidth(string)) / 2;
				int k = this.client.window.getScaledHeight() - 35;
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				this.client.field_1772.drawWithShadow(string, (float)j, (float)k, 16777215 + (i << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void close(SpectatorMenu spectatorMenu) {
		this.field_2200 = null;
		this.lastKeyPressTime = 0L;
	}

	public boolean method_1980() {
		return this.field_2200 != null;
	}

	public void method_1976(double d) {
		int i = this.field_2200.getSelectedSlot() + (int)d;

		while (i >= 0 && i <= 8 && (this.field_2200.method_2777(i) == SpectatorMenu.field_3260 || !this.field_2200.method_2777(i).enabled())) {
			i = (int)((double)i + d);
		}

		if (i >= 0 && i <= 8) {
			this.field_2200.setSelectedSlot(i);
			this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
		}
	}

	public void method_1983() {
		this.lastKeyPressTime = SystemUtil.getMeasuringTimeMs();
		if (this.method_1980()) {
			int i = this.field_2200.getSelectedSlot();
			if (i != -1) {
				this.field_2200.setSelectedSlot(i);
			}
		} else {
			this.field_2200 = new SpectatorMenu(this);
		}
	}
}
