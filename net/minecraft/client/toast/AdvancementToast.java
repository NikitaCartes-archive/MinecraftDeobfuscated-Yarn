/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AdvancementToast
implements Toast {
    private final Advancement advancement;
    private boolean soundPlayed;

    public AdvancementToast(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Toast.Visibility draw(ToastManager toastManager, long l) {
        toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        AdvancementDisplay advancementDisplay = this.advancement.getDisplay();
        toastManager.blit(0, 0, 0, 0, 160, 32);
        if (advancementDisplay != null) {
            int i;
            List<String> list = toastManager.getGame().textRenderer.wrapStringToWidthAsList(advancementDisplay.getTitle().asFormattedString(), 125);
            int n = i = advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE ? 0xFF88FF : 0xFFFF00;
            if (list.size() == 1) {
                toastManager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId(), new Object[0]), 30.0f, 7.0f, i | 0xFF000000);
                toastManager.getGame().textRenderer.draw(advancementDisplay.getTitle().asFormattedString(), 30.0f, 18.0f, -1);
            } else {
                int j = 1500;
                float f = 300.0f;
                if (l < 1500L) {
                    int k = MathHelper.floor(MathHelper.clamp((float)(1500L - l) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    toastManager.getGame().textRenderer.draw(I18n.translate("advancements.toast." + advancementDisplay.getFrame().getId(), new Object[0]), 30.0f, 11.0f, i | k);
                } else {
                    int k = MathHelper.floor(MathHelper.clamp((float)(l - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    int m = 16 - list.size() * toastManager.getGame().textRenderer.fontHeight / 2;
                    for (String string : list) {
                        toastManager.getGame().textRenderer.draw(string, 30.0f, m, 0xFFFFFF | k);
                        m += toastManager.getGame().textRenderer.fontHeight;
                    }
                }
            }
            if (!this.soundPlayed && l > 0L) {
                this.soundPlayed = true;
                if (advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE) {
                    toastManager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
                }
            }
            DiffuseLighting.enableForItems();
            toastManager.getGame().getItemRenderer().renderGuiItem(null, advancementDisplay.getIcon(), 8, 8);
            return l >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
        return Toast.Visibility.HIDE;
    }
}

