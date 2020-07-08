/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.toast;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SystemToast
implements Toast {
    private final Type type;
    private StringRenderable title;
    private List<StringRenderable> lines;
    private long startTime;
    private boolean justUpdated;
    private final int width;

    public SystemToast(Type type, Text title, @Nullable Text description) {
        this(type, title, SystemToast.getTextAsList(description), 160);
    }

    public static SystemToast create(MinecraftClient client, Type type, Text title, Text description) {
        TextRenderer textRenderer = client.textRenderer;
        List<StringRenderable> list = textRenderer.getTextHandler().wrapLines(description, 200, Style.EMPTY);
        int i = Math.max(200, list.stream().mapToInt(textRenderer::getWidth).max().orElse(200));
        return new SystemToast(type, title, list, i + 30);
    }

    private SystemToast(Type type, Text title, List<StringRenderable> lines, int width) {
        this.type = type;
        this.title = title;
        this.lines = lines;
        this.width = width;
    }

    private static ImmutableList<StringRenderable> getTextAsList(@Nullable Text text) {
        return text == null ? ImmutableList.of() : ImmutableList.of(text);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        int k;
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        int i = this.getWidth();
        int j = 12;
        if (i == 160 && this.lines.size() <= 1) {
            manager.drawTexture(matrices, 0, 0, 0, 64, i, this.getHeight());
        } else {
            k = this.getHeight() + Math.max(0, this.lines.size() - 1) * 12;
            int l = 28;
            int m = Math.min(4, k - 28);
            this.drawPart(matrices, manager, i, 0, 0, 28);
            for (int n = 28; n < k - m; n += 10) {
                this.drawPart(matrices, manager, i, 16, n, Math.min(16, k - n - m));
            }
            this.drawPart(matrices, manager, i, 32 - m, k - m, m);
        }
        if (this.lines == null) {
            manager.getGame().textRenderer.draw(matrices, this.title, 18.0f, 12.0f, -256);
        } else {
            manager.getGame().textRenderer.draw(matrices, this.title, 18.0f, 7.0f, -256);
            for (k = 0; k < this.lines.size(); ++k) {
                manager.getGame().textRenderer.draw(matrices, this.lines.get(k), 18.0f, (float)(18 + k * 12), -1);
            }
        }
        return startTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void drawPart(MatrixStack matrices, ToastManager manager, int width, int textureV, int y, int height) {
        int i = textureV == 0 ? 20 : 5;
        int j = Math.min(60, width - i);
        manager.drawTexture(matrices, 0, y, 0, 64 + textureV, i, height);
        for (int k = i; k < width - j; k += 64) {
            manager.drawTexture(matrices, k, y, 32, 64 + textureV, Math.min(64, width - k - j), height);
        }
        manager.drawTexture(matrices, width - j, y, 160 - j, 64 + textureV, j, height);
    }

    public void setContent(Text title, @Nullable Text description) {
        this.title = title;
        this.lines = SystemToast.getTextAsList(description);
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

    public static void addPackCopyFailure(MinecraftClient client, String directory) {
        SystemToast.add(client.getToastManager(), Type.PACK_COPY_FAILURE, new TranslatableText("pack.copyFailure"), new LiteralText(directory));
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
        WORLD_GEN_SETTINGS_TRANSFER,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE,
        PACK_COPY_FAILURE;

    }
}

