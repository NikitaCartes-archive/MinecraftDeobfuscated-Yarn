package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.WrittenBookScreen;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class BookPageButtonWidget extends ButtonWidget {
	private final boolean isNextPageButton;
	private final boolean field_18977;

	public BookPageButtonWidget(int i, int j, boolean bl, ButtonWidget.class_4241 arg, boolean bl2) {
		super(i, j, 23, 13, "", arg);
		this.isNextPageButton = bl;
		this.field_18977 = bl2;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(WrittenBookScreen.BOOK_TEXTURE);
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

	@Override
	public void playDownSound(SoundManager soundManager) {
		if (this.field_18977) {
			soundManager.play(PositionedSoundInstance.master(SoundEvents.field_17481, 1.0F));
		}
	}
}
