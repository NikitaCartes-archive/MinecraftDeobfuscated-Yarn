/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpectatorHud
extends DrawableHelper
implements SpectatorMenuCloseCallback {
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    public static final Identifier SPECTATOR_TEXTURE = new Identifier("textures/gui/spectator_widgets.png");
    private static final long FADE_OUT_DELAY = 5000L;
    private static final long FADE_OUT_DURATION = 2000L;
    private final MinecraftClient client;
    private long lastInteractionTime;
    @Nullable
    private SpectatorMenu spectatorMenu;

    public SpectatorHud(MinecraftClient client) {
        this.client = client;
    }

    public void selectSlot(int slot) {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.spectatorMenu != null) {
            this.spectatorMenu.useCommand(slot);
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }

    private float getSpectatorMenuHeight() {
        long l = this.lastInteractionTime - Util.getMeasuringTimeMs() + 5000L;
        return MathHelper.clamp((float)l / 2000.0f, 0.0f, 1.0f);
    }

    public void renderSpectatorMenu(MatrixStack matrices) {
        if (this.spectatorMenu == null) {
            return;
        }
        float f = this.getSpectatorMenuHeight();
        if (f <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        int i = this.client.getWindow().getScaledWidth() / 2;
        int j = this.getZOffset();
        this.setZOffset(-90);
        int k = MathHelper.floor((float)this.client.getWindow().getScaledHeight() - 22.0f * f);
        SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
        this.renderSpectatorMenu(matrices, f, i, k, spectatorMenuState);
        this.setZOffset(j);
    }

    protected void renderSpectatorMenu(MatrixStack matrices, float height, int x, int y, SpectatorMenuState state) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, height);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        this.drawTexture(matrices, x - 91, y, 0, 0, 182, 22);
        if (state.getSelectedSlot() >= 0) {
            this.drawTexture(matrices, x - 91 - 1 + state.getSelectedSlot() * 20, y - 1, 0, 22, 24, 22);
        }
        for (int i = 0; i < 9; ++i) {
            this.renderSpectatorCommand(matrices, i, this.client.getWindow().getScaledWidth() / 2 - 90 + i * 20 + 2, y + 3, height, state.getCommand(i));
        }
        RenderSystem.disableBlend();
    }

    private void renderSpectatorCommand(MatrixStack matrices, int slot, int x, float y, float height, SpectatorMenuCommand command) {
        RenderSystem.setShaderTexture(0, SPECTATOR_TEXTURE);
        if (command != SpectatorMenu.BLANK_COMMAND) {
            int i = (int)(height * 255.0f);
            matrices.push();
            matrices.translate(x, y, 0.0);
            float f = command.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.setShaderColor(f, f, f, height);
            command.renderIcon(matrices, f, i);
            matrices.pop();
            if (i > 3 && command.isEnabled()) {
                Text text = this.client.options.keysHotbar[slot].getBoundKeyLocalizedText();
                this.client.textRenderer.drawWithShadow(matrices, text, (float)(x + 19 - 2 - this.client.textRenderer.getWidth(text)), y + 6.0f + 3.0f, 0xFFFFFF + (i << 24));
            }
        }
    }

    public void render(MatrixStack matrices) {
        int i = (int)(this.getSpectatorMenuHeight() * 255.0f);
        if (i > 3 && this.spectatorMenu != null) {
            Text text;
            SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
            Text text2 = text = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt() : spectatorMenuCommand.getName();
            if (text != null) {
                int j = (this.client.getWindow().getScaledWidth() - this.client.textRenderer.getWidth(text)) / 2;
                int k = this.client.getWindow().getScaledHeight() - 35;
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.client.textRenderer.drawWithShadow(matrices, text, (float)j, (float)k, 0xFFFFFF + (i << 24));
                RenderSystem.disableBlend();
            }
        }
    }

    @Override
    public void close(SpectatorMenu menu) {
        this.spectatorMenu = null;
        this.lastInteractionTime = 0L;
    }

    public boolean isOpen() {
        return this.spectatorMenu != null;
    }

    public void cycleSlot(double offset) {
        int i = this.spectatorMenu.getSelectedSlot() + (int)offset;
        while (!(i < 0 || i > 8 || this.spectatorMenu.getCommand(i) != SpectatorMenu.BLANK_COMMAND && this.spectatorMenu.getCommand(i).isEnabled())) {
            i = (int)((double)i + offset);
        }
        if (i >= 0 && i <= 8) {
            this.spectatorMenu.useCommand(i);
            this.lastInteractionTime = Util.getMeasuringTimeMs();
        }
    }

    public void useSelectedCommand() {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.isOpen()) {
            int i = this.spectatorMenu.getSelectedSlot();
            if (i != -1) {
                this.spectatorMenu.useCommand(i);
            }
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }
}

