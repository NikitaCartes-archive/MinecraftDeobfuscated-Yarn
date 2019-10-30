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

    public SpectatorHud(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void selectSlot(int i) {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.spectatorMenu != null) {
            this.spectatorMenu.useCommand(i);
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }

    private float getSpectatorMenuHeight() {
        long l = this.lastInteractionTime - Util.getMeasuringTimeMs() + 5000L;
        return MathHelper.clamp((float)l / 2000.0f, 0.0f, 1.0f);
    }

    public void render(float f) {
        if (this.spectatorMenu == null) {
            return;
        }
        float g = this.getSpectatorMenuHeight();
        if (g <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        int i = this.client.getWindow().getScaledWidth() / 2;
        int j = this.getBlitOffset();
        this.setBlitOffset(-90);
        int k = MathHelper.floor((float)this.client.getWindow().getScaledHeight() - 22.0f * g);
        SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
        this.renderSpectatorMenu(g, i, k, spectatorMenuState);
        this.setBlitOffset(j);
    }

    protected void renderSpectatorMenu(float f, int i, int j, SpectatorMenuState spectatorMenuState) {
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f);
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        this.blit(i - 91, j, 0, 0, 182, 22);
        if (spectatorMenuState.getSelectedSlot() >= 0) {
            this.blit(i - 91 - 1 + spectatorMenuState.getSelectedSlot() * 20, j - 1, 0, 22, 24, 22);
        }
        for (int k = 0; k < 9; ++k) {
            this.renderSpectatorCommand(k, this.client.getWindow().getScaledWidth() / 2 - 90 + k * 20 + 2, j + 3, f, spectatorMenuState.getCommand(k));
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void renderSpectatorCommand(int i, int j, float f, float g, SpectatorMenuCommand spectatorMenuCommand) {
        this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
        if (spectatorMenuCommand != SpectatorMenu.BLANK_COMMAND) {
            int k = (int)(g * 255.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(j, f, 0.0f);
            float h = spectatorMenuCommand.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.color4f(h, h, h, g);
            spectatorMenuCommand.renderIcon(h, k);
            RenderSystem.popMatrix();
            String string = String.valueOf(this.client.options.keysHotbar[i].getLocalizedName());
            if (k > 3 && spectatorMenuCommand.isEnabled()) {
                this.client.textRenderer.drawWithShadow(string, j + 19 - 2 - this.client.textRenderer.getStringWidth(string), f + 6.0f + 3.0f, 0xFFFFFF + (k << 24));
            }
        }
    }

    public void render() {
        int i = (int)(this.getSpectatorMenuHeight() * 255.0f);
        if (i > 3 && this.spectatorMenu != null) {
            String string;
            SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
            String string2 = string = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt().asFormattedString() : spectatorMenuCommand.getName().asFormattedString();
            if (string != null) {
                int j = (this.client.getWindow().getScaledWidth() - this.client.textRenderer.getStringWidth(string)) / 2;
                int k = this.client.getWindow().getScaledHeight() - 35;
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.client.textRenderer.drawWithShadow(string, j, k, 0xFFFFFF + (i << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public void close(SpectatorMenu spectatorMenu) {
        this.spectatorMenu = null;
        this.lastInteractionTime = 0L;
    }

    public boolean isOpen() {
        return this.spectatorMenu != null;
    }

    public void cycleSlot(double d) {
        int i = this.spectatorMenu.getSelectedSlot() + (int)d;
        while (!(i < 0 || i > 8 || this.spectatorMenu.getCommand(i) != SpectatorMenu.BLANK_COMMAND && this.spectatorMenu.getCommand(i).isEnabled())) {
            i = (int)((double)i + d);
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

