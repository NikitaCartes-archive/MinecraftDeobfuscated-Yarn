package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WrittenBookScreen;

@Environment(EnvType.CLIENT)
public abstract class BookPageButtonWidget extends class_4185 {
	private final boolean isNextPageButton;

	public BookPageButtonWidget(int i, int j, boolean bl) {
		super(i, j, 23, 13, "");
		this.isNextPageButton = bl;
	}

	@Override
	public void drawButton(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().method_1531().method_4618(WrittenBookScreen.field_17117);
		int k = 0;
		int l = 192;
		if (this.isHovered()) {
			k += 23;
		}

		if (!this.isNextPageButton) {
			l += 13;
		}

		this.drawTexturedRect(this.x, this.y, k, l, 23, 13);
	}
}
