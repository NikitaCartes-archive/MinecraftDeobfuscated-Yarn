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
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ToastManager
extends DrawableHelper {
    private final MinecraftClient client;
    private final Entry<?>[] visibleEntries = new Entry[5];
    private final Deque<Toast> toastQueue = Queues.newArrayDeque();

    public ToastManager(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void draw() {
        if (this.client.options.hudHidden) {
            return;
        }
        for (int i = 0; i < this.visibleEntries.length; ++i) {
            Entry<?> entry = this.visibleEntries[i];
            if (entry != null && entry.draw(this.client.getWindow().getScaledWidth(), i)) {
                this.visibleEntries[i] = null;
            }
            if (this.visibleEntries[i] != null || this.toastQueue.isEmpty()) continue;
            this.visibleEntries[i] = new Entry(this, this.toastQueue.removeFirst());
        }
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> class_, Object object) {
        for (Entry<?> entry : this.visibleEntries) {
            if (entry == null || !class_.isAssignableFrom(entry.getInstance().getClass()) || !entry.getInstance().getType().equals(object)) continue;
            return (T)entry.getInstance();
        }
        for (Toast toast : this.toastQueue) {
            if (!class_.isAssignableFrom(toast.getClass()) || !toast.getType().equals(object)) continue;
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

    public MinecraftClient getGame() {
        return this.client;
    }

    @Environment(value=EnvType.CLIENT)
    static class Entry<T extends Toast> {
        private final T instance;
        private long field_2243 = -1L;
        private long field_2242 = -1L;
        private Toast.Visibility visibility = Toast.Visibility.SHOW;
        final /* synthetic */ ToastManager field_2245;

        private Entry(T toast) {
            this.field_2245 = toastManager;
            this.instance = toast;
        }

        public T getInstance() {
            return this.instance;
        }

        private float getDissapearProgress(long l) {
            float f = MathHelper.clamp((float)(l - this.field_2243) / 600.0f, 0.0f, 1.0f);
            f *= f;
            if (this.visibility == Toast.Visibility.HIDE) {
                return 1.0f - f;
            }
            return f;
        }

        public boolean draw(int i, int j) {
            long l = Util.getMeasuringTimeMs();
            if (this.field_2243 == -1L) {
                this.field_2243 = l;
                this.visibility.playSound(this.field_2245.client.getSoundManager());
            }
            if (this.visibility == Toast.Visibility.SHOW && l - this.field_2243 <= 600L) {
                this.field_2242 = l;
            }
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)i - 160.0f * this.getDissapearProgress(l), j * 32, 500 + j);
            Toast.Visibility visibility = this.instance.draw(this.field_2245, l - this.field_2242);
            RenderSystem.popMatrix();
            if (visibility != this.visibility) {
                this.field_2243 = l - (long)((int)((1.0f - this.getDissapearProgress(l)) * 600.0f));
                this.visibility = visibility;
                this.visibility.playSound(this.field_2245.client.getSoundManager());
            }
            return this.visibility == Toast.Visibility.HIDE && l - this.field_2243 > 600L;
        }
    }
}

