package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AdvancementToast implements Toast {
	private final SimpleAdvancement field_2205;
	private boolean field_2206;

	public AdvancementToast(SimpleAdvancement simpleAdvancement) {
		this.field_2205 = simpleAdvancement;
	}

	@Override
	public Toast.Visibility method_1986(ToastManager toastManager, long l) {
		toastManager.getGame().method_1531().method_4618(field_2207);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		AdvancementDisplay advancementDisplay = this.field_2205.method_686();
		toastManager.drawTexturedRect(0, 0, 0, 0, 160, 32);
		if (advancementDisplay != null) {
			List<String> list = toastManager.getGame().field_1772.wrapStringToWidthAsList(advancementDisplay.getTitle().getFormattedText(), 125);
			int i = advancementDisplay.method_815() == AdvancementFrame.CHALLENGE ? 16746751 : 16776960;
			if (list.size() == 1) {
				toastManager.getGame().field_1772.draw(I18n.translate("advancements.toast." + advancementDisplay.method_815().getId()), 30.0F, 7.0F, i | 0xFF000000);
				toastManager.getGame().field_1772.draw(advancementDisplay.getTitle().getFormattedText(), 30.0F, 18.0F, -1);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (l < 1500L) {
					int k = MathHelper.floor(MathHelper.clamp((float)(1500L - l) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					toastManager.getGame().field_1772.draw(I18n.translate("advancements.toast." + advancementDisplay.method_815().getId()), 30.0F, 11.0F, i | k);
				} else {
					int k = MathHelper.floor(MathHelper.clamp((float)(l - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int m = 16 - list.size() * 9 / 2;

					for (String string : list) {
						toastManager.getGame().field_1772.draw(string, 30.0F, (float)m, 16777215 | k);
						m += 9;
					}
				}
			}

			if (!this.field_2206 && l > 0L) {
				this.field_2206 = true;
				if (advancementDisplay.method_815() == AdvancementFrame.CHALLENGE) {
					toastManager.getGame().method_1483().play(PositionedSoundInstance.method_4757(SoundEvents.field_15195, 1.0F, 1.0F));
				}
			}

			GuiLighting.enableForItems();
			toastManager.getGame().method_1480().renderGuiItem(null, advancementDisplay.getIcon(), 8, 8);
			return l >= 5000L ? Toast.Visibility.field_2209 : Toast.Visibility.field_2210;
		} else {
			return Toast.Visibility.field_2209;
		}
	}
}
