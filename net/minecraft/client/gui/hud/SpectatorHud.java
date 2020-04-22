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

@Environment(value=EnvType.CLIENT)
public class SpectatorHud
extends DrawableHelper
implements SpectatorMenuCloseCallback {
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
    public static final Identifier SPECTATOR_TEX = new Identifier("textures/gui/spectator_widgets.png");
    private final MinecraftClient client;
    private long lastInteractionTime;
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

    public void render(MatrixStack matrixStack, float f) {
        if (this.spectatorMenu == null) {
            return;
        }
        float g = this.getSpectatorMenuHeight();
        if (g <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        int i = this.client.getWindow().getScaledWidth() / 2;
        int j = this.getZOffset();
        this.setZOffset(-90);
        int k = MathHelper.floor((float)this.client.getWindow().getScaledHeight() - 22.0f * g);
        SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
        this.renderSpectatorMenu(matrixStack, g, i, k, spectatorMenuState);
        this.setZOffset(j);
    }

    protected void renderSpectatorMenu(MatrixStack matrixStack, float f, int i, int j, SpectatorMenuState spectatorMenuState) {
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f);
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        this.drawTexture(matrixStack, i - 91, j, 0, 0, 182, 22);
        if (spectatorMenuState.getSelectedSlot() >= 0) {
            this.drawTexture(matrixStack, i - 91 - 1 + spectatorMenuState.getSelectedSlot() * 20, j - 1, 0, 22, 24, 22);
        }
        for (int k = 0; k < 9; ++k) {
            this.renderSpectatorCommand(matrixStack, k, this.client.getWindow().getScaledWidth() / 2 - 90 + k * 20 + 2, j + 3, f, spectatorMenuState.getCommand(k));
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void renderSpectatorCommand(MatrixStack matrixStack, int i, int j, float f, float g, SpectatorMenuCommand spectatorMenuCommand) {
        this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
        if (spectatorMenuCommand != SpectatorMenu.BLANK_COMMAND) {
            int k = (int)(g * 255.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(j, f, 0.0f);
            float h = spectatorMenuCommand.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.color4f(h, h, h, g);
            spectatorMenuCommand.renderIcon(matrixStack, h, k);
            RenderSystem.popMatrix();
            if (k > 3 && spectatorMenuCommand.isEnabled()) {
                Text text = this.client.options.keysHotbar[i].getLocalizedName();
                this.client.textRenderer.drawWithShadow(matrixStack, text, (float)(j + 19 - 2 - this.client.textRenderer.getWidth(text)), f + 6.0f + 3.0f, 0xFFFFFF + (k << 24));
            }
        }
    }

    public void render(MatrixStack matrixStack) {
        int i = (int)(this.getSpectatorMenuHeight() * 255.0f);
        if (i > 3 && this.spectatorMenu != null) {
            Text text;
            SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
            Text text2 = text = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt() : spectatorMenuCommand.getName();
            if (text != null) {
                int j = (this.client.getWindow().getScaledWidth() - this.client.textRenderer.getWidth(text)) / 2;
                int k = this.client.getWindow().getScaledHeight() - 35;
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.client.textRenderer.drawWithShadow(matrixStack, text, (float)j, (float)k, 0xFFFFFF + (i << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
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

