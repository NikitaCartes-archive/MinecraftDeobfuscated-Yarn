package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class PageTurnWidget extends ButtonWidget {
	private final boolean isNextPageButton;
	private final boolean playPageTurnSound;

	public PageTurnWidget(int x, int y, boolean isNextPageButton, ButtonWidget.PressAction action, boolean playPageTurnSound) {
		super(x, y, 23, 13, ScreenTexts.EMPTY, action, DEFAULT_NARRATION_SUPPLIER);
		this.isNextPageButton = isNextPageButton;
		this.playPageTurnSound = playPageTurnSound;
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		int i = 0;
		int j = 192;
		if (this.isSelected()) {
			i += 23;
		}

		if (!this.isNextPageButton) {
			j += 13;
		}

		context.drawTexture(BookScreen.BOOK_TEXTURE, this.getX(), this.getY(), i, j, 23, 13);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
		if (this.playPageTurnSound) {
			soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
		}
	}
}
