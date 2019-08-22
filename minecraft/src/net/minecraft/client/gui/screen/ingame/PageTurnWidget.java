package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class PageTurnWidget extends ButtonWidget {
	private final boolean isNextPageButton;
	private final boolean playPageTurnSound;

	public PageTurnWidget(int i, int j, boolean bl, ButtonWidget.PressAction pressAction, boolean bl2) {
		super(i, j, 23, 13, "", pressAction);
		this.isNextPageButton = bl;
		this.playPageTurnSound = bl2;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(BookScreen.BOOK_TEXTURE);
		int k = 0;
		int l = 192;
		if (this.isHovered()) {
			k += 23;
		}

		if (!this.isNextPageButton) {
			l += 13;
		}

		this.blit(this.x, this.y, k, l, 23, 13);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
		if (this.playPageTurnSound) {
			soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
		}
	}
}
