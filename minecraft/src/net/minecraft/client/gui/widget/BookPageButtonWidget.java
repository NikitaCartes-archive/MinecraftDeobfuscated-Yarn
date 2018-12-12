package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.WrittenBookGui;

@Environment(EnvType.CLIENT)
public abstract class BookPageButtonWidget extends ButtonWidget {
	private final boolean isNextPageButton;

	public BookPageButtonWidget(int i, int j, int k, boolean bl) {
		super(i, j, k, 23, 13, "");
		this.isNextPageButton = bl;
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			MinecraftClient.getInstance().getTextureManager().bindTexture(WrittenBookGui.field_17117);
			int k = 0;
			int l = 192;
			if (bl) {
				k += 23;
			}

			if (!this.isNextPageButton) {
				l += 13;
			}

			this.drawTexturedRect(this.x, this.y, k, l, 23, 13);
		}
	}
}
