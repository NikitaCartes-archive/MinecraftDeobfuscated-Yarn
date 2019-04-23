package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementToast implements Toast {
	private final Advancement field_2205;
	private boolean field_2206;

	public AdvancementToast(Advancement advancement) {
		this.field_2205 = advancement;
	}

	@Override
	public Toast.Visibility draw(ToastManager toastManager, long l) {
		toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		AdvancementDisplay advancementDisplay = this.field_2205.getDisplay();
		toastManager.blit(0, 0, 0, 0, 160, 32);
		if (advancementDisplay != null) {
			List<String> list = toastManager.getGame().textRenderer.wrapStringToWidthAsList(advancementDisplay.getTitle().getFormattedText(), 125);
			int i = advancementDisplay.getFrame() == AdvancementFrame.field_1250 ? 16746751 : 16776960;
			if (list.size() == 1) {
				toastManager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId()), 30.0F, 7.0F, i | 0xFF000000);
				toastManager.getGame().textRenderer.draw(advancementDisplay.getTitle().getFormattedText(), 30.0F, 18.0F, -1);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (l < 1500L) {
					int k = MathHelper.floor(MathHelper.clamp((float)(1500L - l) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					toastManager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId()), 30.0F, 11.0F, i | k);
				} else {
					int k = MathHelper.floor(MathHelper.clamp((float)(l - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int m = 16 - list.size() * 9 / 2;

					for (String string : list) {
						toastManager.getGame().textRenderer.draw(string, 30.0F, (float)m, 16777215 | k);
						m += 9;
					}
				}
			}

			if (!this.field_2206 && l > 0L) {
				this.field_2206 = true;
				if (advancementDisplay.getFrame() == AdvancementFrame.field_1250) {
					toastManager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.field_15195, 1.0F, 1.0F));
				}
			}

			GuiLighting.enableForItems();
			toastManager.getGame().getItemRenderer().renderGuiItem(null, advancementDisplay.getIcon(), 8, 8);
			return l >= 5000L ? Toast.Visibility.field_2209 : Toast.Visibility.field_2210;
		} else {
			return Toast.Visibility.field_2209;
		}
	}
}
