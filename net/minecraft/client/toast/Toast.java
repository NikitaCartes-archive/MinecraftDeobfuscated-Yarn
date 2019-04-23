/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface Toast {
    public static final Identifier TOASTS_TEX = new Identifier("textures/gui/toasts.png");
    public static final Object field_2208 = new Object();

    public Visibility draw(ToastManager var1, long var2);

    default public Object getType() {
        return field_2208;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Visibility {
        SHOW(SoundEvents.UI_TOAST_IN),
        HIDE(SoundEvents.UI_TOAST_OUT);

        private final SoundEvent sound;

        private Visibility(SoundEvent soundEvent) {
            this.sound = soundEvent;
        }

        public void play(SoundManager soundManager) {
            soundManager.play(PositionedSoundInstance.master(this.sound, 1.0f, 1.0f));
        }
    }
}

