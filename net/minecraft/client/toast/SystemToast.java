/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private final Type type;
    private String title;
    private String description;
    private long startTime;
    private boolean justUpdated;

    public SystemToast(Type type, Text title, @Nullable Text description) {
        this.type = type;
        this.title = title.getString();
        this.description = description == null ? null : description.getString();
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        manager.drawTexture(matrices, 0, 0, 0, 64, 160, 32);
        if (this.description == null) {
            manager.getGame().textRenderer.draw(matrices, this.title, 18.0f, 12.0f, -256);
        } else {
            manager.getGame().textRenderer.draw(matrices, this.title, 18.0f, 7.0f, -256);
            manager.getGame().textRenderer.draw(matrices, this.description, 18.0f, 18.0f, -1);
        }
        return startTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public void setContent(Text title, @Nullable Text description) {
        this.title = title.getString();
        this.description = description == null ? null : description.getString();
        this.justUpdated = true;
    }

    public Type getType() {
        return this.type;
    }

    public static void add(ToastManager manager, Type type, Text title, @Nullable Text description) {
        manager.add(new SystemToast(type, title, description));
    }

    public static void show(ToastManager manager, Type type, Text title, @Nullable Text description) {
        SystemToast systemToast = manager.getToast(SystemToast.class, (Object)type);
        if (systemToast == null) {
            SystemToast.add(manager, type, title, description);
        } else {
            systemToast.setContent(title, description);
        }
    }

    public static void addWorldAccessFailureToast(MinecraftClient client, String worldName) {
        SystemToast.add(client.getToastManager(), Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.access_failure"), new LiteralText(worldName));
    }

    public static void addWorldDeleteFailureToast(MinecraftClient client, String worldName) {
        SystemToast.add(client.getToastManager(), Type.WORLD_ACCESS_FAILURE, new TranslatableText("selectWorld.delete_failure"), new LiteralText(worldName));
    }

    @Override
    public /* synthetic */ Object getType() {
        return this.getType();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE;

    }
}

