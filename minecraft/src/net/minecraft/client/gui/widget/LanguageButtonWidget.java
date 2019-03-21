package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class LanguageButtonWidget extends ButtonWidget {
	public LanguageButtonWidget(int i, int j, ButtonWidget.class_4241 arg) {
		super(i, j, 20, 20, "", arg);
	}

	@Override
	public void renderButton(int i, int j, float f) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(ButtonWidget.WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int k = 106;
		if (this.isHovered()) {
			k += this.height;
		}

		this.drawTexturedRect(this.x, this.y, 0, k, this.width, this.height);
	}
}
