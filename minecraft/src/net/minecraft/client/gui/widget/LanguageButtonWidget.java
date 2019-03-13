package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class LanguageButtonWidget extends class_4185 {
	public LanguageButtonWidget(int i, int j) {
		super(i, j, 20, 20, "");
	}

	@Override
	public void drawButton(int i, int j, float f) {
		MinecraftClient.getInstance().method_1531().method_4618(class_4185.field_2072);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.opacity);
		int k = 106;
		if (this.isHovered()) {
			k += this.height;
		}

		this.drawTexturedRect(this.x, this.y, 0, k, this.width, this.height);
	}
}
