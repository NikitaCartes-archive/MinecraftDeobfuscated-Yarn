/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ToastManager
extends DrawableHelper {
    private static final int MAX_VISIBLE_ENTRIES = 5;
    final MinecraftClient client;
    private final Entry<?>[] visibleEntries = new Entry[5];
    private final Deque<Toast> toastQueue = Queues.newArrayDeque();

    public ToastManager(MinecraftClient client) {
        this.client = client;
    }

    public void draw(MatrixStack matrices) {
        if (this.client.options.hudHidden) {
            return;
        }
        for (int i = 0; i < this.visibleEntries.length; ++i) {
            Entry<?> entry = this.visibleEntries[i];
            if (entry != null && entry.draw(this.client.getWindow().getScaledWidth(), i, matrices)) {
                this.visibleEntries[i] = null;
            }
            if (this.visibleEntries[i] != null || this.toastQueue.isEmpty()) continue;
            this.visibleEntries[i] = new Entry(this, this.toastQueue.removeFirst());
        }
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> toastClass, Object type) {
        for (Entry<?> entry : this.visibleEntries) {
            if (entry == null || !toastClass.isAssignableFrom(entry.getInstance().getClass()) || !entry.getInstance().getType().equals(type)) continue;
            return (T)entry.getInstance();
        }
        for (Toast toast : this.toastQueue) {
            if (!toastClass.isAssignableFrom(toast.getClass()) || !toast.getType().equals(type)) continue;
            return (T)toast;
        }
        return null;
    }

    public void clear() {
        Arrays.fill(this.visibleEntries, null);
        this.toastQueue.clear();
    }

    public void add(Toast toast) {
        this.toastQueue.add(toast);
    }

    public MinecraftClient getClient() {
        return this.client;
    }

    @Environment(value=EnvType.CLIENT)
    static class Entry<T extends Toast> {
        private static final long field_32221 = 600L;
        private final T instance;
        private long startTime = -1L;
        private long showTime = -1L;
        private Toast.Visibility visibility = Toast.Visibility.SHOW;
        final /* synthetic */ ToastManager field_2245;

        Entry(T instance) {
            this.field_2245 = toastManager;
            this.instance = instance;
        }

        public T getInstance() {
            return this.instance;
        }

        private float getDisappearProgress(long time) {
            float f = MathHelper.clamp((float)(time - this.startTime) / 600.0f, 0.0f, 1.0f);
            f *= f;
            if (this.visibility == Toast.Visibility.HIDE) {
                return 1.0f - f;
            }
            return f;
        }

        public boolean draw(int x, int y, MatrixStack matrices) {
            long l = Util.getMeasuringTimeMs();
            if (this.startTime == -1L) {
                this.startTime = l;
                this.visibility.playSound(this.field_2245.client.getSoundManager());
            }
            if (this.visibility == Toast.Visibility.SHOW && l - this.startTime <= 600L) {
                this.showTime = l;
            }
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate((float)x - (float)this.instance.getWidth() * this.getDisappearProgress(l), y * this.instance.getHeight(), 800 + y);
            RenderSystem.applyModelViewMatrix();
            Toast.Visibility visibility = this.instance.draw(matrices, this.field_2245, l - this.showTime);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            if (visibility != this.visibility) {
                this.startTime = l - (long)((int)((1.0f - this.getDisappearProgress(l)) * 600.0f));
                this.visibility = visibility;
                this.visibility.playSound(this.field_2245.client.getSoundManager());
            }
            return this.visibility == Toast.Visibility.HIDE && l - this.startTime > 600L;
        }
    }
}

