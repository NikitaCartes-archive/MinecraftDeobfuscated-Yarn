/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private final Type field_2213;
    private String field_2215;
    private String field_2217;
    private long startTime;
    private boolean justUpdated;

    public SystemToast(Type type, Component component, @Nullable Component component2) {
        this.field_2213 = type;
        this.field_2215 = component.getString();
        this.field_2217 = component2 == null ? null : component2.getString();
    }

    @Override
    public Toast.Visibility draw(ToastManager toastManager, long l) {
        if (this.justUpdated) {
            this.startTime = l;
            this.justUpdated = false;
        }
        toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        toastManager.blit(0, 0, 0, 64, 160, 32);
        if (this.field_2217 == null) {
            toastManager.getGame().textRenderer.draw(this.field_2215, 18.0f, 12.0f, -256);
        } else {
            toastManager.getGame().textRenderer.draw(this.field_2215, 18.0f, 7.0f, -256);
            toastManager.getGame().textRenderer.draw(this.field_2217, 18.0f, 18.0f, -1);
        }
        return l - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public void setContent(Component component, @Nullable Component component2) {
        this.field_2215 = component.getString();
        this.field_2217 = component2 == null ? null : component2.getString();
        this.justUpdated = true;
    }

    public Type method_1989() {
        return this.field_2213;
    }

    public static void show(ToastManager toastManager, Type type, Component component, @Nullable Component component2) {
        SystemToast systemToast = toastManager.getToast(SystemToast.class, (Object)type);
        if (systemToast == null) {
            toastManager.add(new SystemToast(type, component, component2));
        } else {
            systemToast.setContent(component, component2);
        }
    }

    @Override
    public /* synthetic */ Object getType() {
        return this.method_1989();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP;

    }
}

