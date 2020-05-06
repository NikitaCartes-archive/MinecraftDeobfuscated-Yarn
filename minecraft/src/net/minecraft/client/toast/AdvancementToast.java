package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementToast implements Toast {
	private final Advancement advancement;
	private boolean soundPlayed;

	public AdvancementToast(Advancement advancement) {
		this.advancement = advancement;
	}

	@Override
	public Toast.Visibility draw(MatrixStack matrixStack, ToastManager toastManager, long l) {
		toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		AdvancementDisplay advancementDisplay = this.advancement.getDisplay();
		toastManager.drawTexture(matrixStack, 0, 0, 0, 0, 160, 32);
		if (advancementDisplay != null) {
			List<Text> list = toastManager.getGame().textRenderer.wrapLines(advancementDisplay.getTitle(), 125);
			int i = advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE ? 16746751 : 16776960;
			if (list.size() == 1) {
				toastManager.getGame()
					.textRenderer
					.draw(matrixStack, I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId()), 30.0F, 7.0F, i | 0xFF000000);
				toastManager.getGame().textRenderer.draw(matrixStack, (Text)list.get(0), 30.0F, 18.0F, -1);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (l < 1500L) {
					int k = MathHelper.floor(MathHelper.clamp((float)(1500L - l) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					toastManager.getGame().textRenderer.draw(matrixStack, I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId()), 30.0F, 11.0F, i | k);
				} else {
					int k = MathHelper.floor(MathHelper.clamp((float)(l - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int m = 16 - list.size() * 9 / 2;

					for (Text text : list) {
						toastManager.getGame().textRenderer.draw(matrixStack, text, 30.0F, (float)m, 16777215 | k);
						m += 9;
					}
				}
			}

			if (!this.soundPlayed && l > 0L) {
				this.soundPlayed = true;
				if (advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE) {
					toastManager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
				}
			}

			toastManager.getGame().getItemRenderer().method_27953(advancementDisplay.getIcon(), 8, 8);
			return l >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
		} else {
			return Toast.Visibility.HIDE;
		}
	}
}
