package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_536;
import net.minecraft.class_539;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuElement;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuImpl;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SpectatorHud extends Drawable implements class_536 {
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
	public static final Identifier SPECTATOR_TEX = new Identifier("textures/gui/spectator_widgets.png");
	private final MinecraftClient client;
	private long field_2198;
	private SpectatorMenuImpl spectatorMenu;

	public SpectatorHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_1977(int i) {
		this.field_2198 = SystemUtil.getMeasuringTimeMs();
		if (this.spectatorMenu != null) {
			this.spectatorMenu.method_2771(i);
		} else {
			this.spectatorMenu = new SpectatorMenuImpl(this);
		}
	}

	private float method_1981() {
		long l = this.field_2198 - SystemUtil.getMeasuringTimeMs() + 5000L;
		return MathHelper.clamp((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void draw(float f) {
		if (this.spectatorMenu != null) {
			float g = this.method_1981();
			if (g <= 0.0F) {
				this.spectatorMenu.method_2779();
			} else {
				int i = this.client.window.getScaledWidth() / 2;
				float h = this.zOffset;
				this.zOffset = -90.0F;
				float j = (float)this.client.window.getScaledHeight() - 22.0F * g;
				class_539 lv = this.spectatorMenu.method_2772();
				this.method_1975(g, i, j, lv);
				this.zOffset = h;
			}
		}
	}

	protected void method_1975(float f, int i, float g, class_539 arg) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(WIDGETS_TEX);
		this.drawTexturedRect((float)(i - 91), g, 0, 0, 182, 22);
		if (arg.method_2787() >= 0) {
			this.drawTexturedRect((float)(i - 91 - 1 + arg.method_2787() * 20), g - 1.0F, 0, 22, 24, 22);
		}

		GuiLighting.enableForItems();

		for (int j = 0; j < 9; j++) {
			this.method_1982(j, this.client.window.getScaledWidth() / 2 - 90 + j * 20 + 2, g + 3.0F, f, arg.method_2786(j));
		}

		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void method_1982(int i, int j, float f, float g, SpectatorMenuElement spectatorMenuElement) {
		this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
		if (spectatorMenuElement != SpectatorMenuImpl.field_3260) {
			int k = (int)(g * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)j, f, 0.0F);
			float h = spectatorMenuElement.enabled() ? 1.0F : 0.25F;
			GlStateManager.color4f(h, h, h, g);
			spectatorMenuElement.renderIcon(h, k);
			GlStateManager.popMatrix();
			String string = String.valueOf(this.client.options.keysHotbar[i].getLocalizedName());
			if (k > 3 && spectatorMenuElement.enabled()) {
				this.client
					.fontRenderer
					.drawWithShadow(string, (float)(j + 19 - 2 - this.client.fontRenderer.getStringWidth(string)), f + 6.0F + 3.0F, 16777215 + (k << 24));
			}
		}
	}

	public void method_1979() {
		int i = (int)(this.method_1981() * 255.0F);
		if (i > 3 && this.spectatorMenu != null) {
			SpectatorMenuElement spectatorMenuElement = this.spectatorMenu.method_2774();
			String string = spectatorMenuElement == SpectatorMenuImpl.field_3260
				? this.spectatorMenu.method_2776().getMessage().getFormattedText()
				: spectatorMenuElement.method_16892().getFormattedText();
			if (string != null) {
				int j = (this.client.window.getScaledWidth() - this.client.fontRenderer.getStringWidth(string)) / 2;
				int k = this.client.window.getScaledHeight() - 35;
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				this.client.fontRenderer.drawWithShadow(string, (float)j, (float)k, 16777215 + (i << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void method_2782(SpectatorMenuImpl spectatorMenuImpl) {
		this.spectatorMenu = null;
		this.field_2198 = 0L;
	}

	public boolean method_1980() {
		return this.spectatorMenu != null;
	}

	public void method_1976(double d) {
		int i = this.spectatorMenu.method_2773() + (int)d;

		while (i >= 0 && i <= 8 && (this.spectatorMenu.method_2777(i) == SpectatorMenuImpl.field_3260 || !this.spectatorMenu.method_2777(i).enabled())) {
			i = (int)((double)i + d);
		}

		if (i >= 0 && i <= 8) {
			this.spectatorMenu.method_2771(i);
			this.field_2198 = SystemUtil.getMeasuringTimeMs();
		}
	}

	public void method_1983() {
		this.field_2198 = SystemUtil.getMeasuringTimeMs();
		if (this.method_1980()) {
			int i = this.spectatorMenu.method_2773();
			if (i != -1) {
				this.spectatorMenu.method_2771(i);
			}
		} else {
			this.spectatorMenu = new SpectatorMenuImpl(this);
		}
	}
}
