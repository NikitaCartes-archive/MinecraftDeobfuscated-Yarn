/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TutorialToast
implements Toast {
    private final Type type;
    private final String title;
    private final String description;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastTime;
    private float lastProgress;
    private float progress;
    private final boolean hasProgressBar;

    public TutorialToast(Type type, Text text, @Nullable Text text2, boolean bl) {
        this.type = type;
        this.title = text.asFormattedString();
        this.description = text2 == null ? null : text2.asFormattedString();
        this.hasProgressBar = bl;
    }

    @Override
    public Toast.Visibility draw(ToastManager toastManager, long l) {
        toastManager.getGame().getTextureManager().method_22813(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        toastManager.blit(0, 0, 0, 96, 160, 32);
        this.type.drawIcon(toastManager, 6, 6);
        if (this.description == null) {
            toastManager.getGame().textRenderer.draw(this.title, 30.0f, 12.0f, -11534256);
        } else {
            toastManager.getGame().textRenderer.draw(this.title, 30.0f, 7.0f, -11534256);
            toastManager.getGame().textRenderer.draw(this.description, 30.0f, 18.0f, -16777216);
        }
        if (this.hasProgressBar) {
            DrawableHelper.fill(3, 28, 157, 29, -1);
            float f = (float)MathHelper.clampedLerp(this.lastProgress, this.progress, (float)(l - this.lastTime) / 100.0f);
            int i = this.progress >= this.lastProgress ? -16755456 : -11206656;
            DrawableHelper.fill(3, 28, (int)(3.0f + 154.0f * f), 29, i);
            this.lastProgress = f;
            this.lastTime = l;
        }
        return this.visibility;
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void setProgress(float f) {
        this.progress = f;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        MOVEMENT_KEYS(0, 0),
        MOUSE(1, 0),
        TREE(2, 0),
        RECIPE_BOOK(0, 1),
        WOODEN_PLANKS(1, 1);

        private final int textureSlotX;
        private final int textureSlotY;

        private Type(int j, int k) {
            this.textureSlotX = j;
            this.textureSlotY = k;
        }

        public void drawIcon(DrawableHelper drawableHelper, int i, int j) {
            RenderSystem.enableBlend();
            drawableHelper.blit(i, j, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }
}

