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

    public void render(float tickDelta) {
        if (this.spectatorMenu == null) {
            return;
        }
        float f = this.getSpectatorMenuHeight();
        if (f <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        int i = this.client.getWindow().getScaledWidth() / 2;
        int j = this.getBlitOffset();
        this.setBlitOffset(-90);
        int k = MathHelper.floor((float)this.client.getWindow().getScaledHeight() - 22.0f * f);
        SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
        this.renderSpectatorMenu(f, i, k, spectatorMenuState);
        this.setBlitOffset(j);
    }

    protected void renderSpectatorMenu(float height, int x, int i, SpectatorMenuState state) {
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, height);
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        this.blit(x - 91, i, 0, 0, 182, 22);
        if (state.getSelectedSlot() >= 0) {
            this.blit(x - 91 - 1 + state.getSelectedSlot() * 20, i - 1, 0, 22, 24, 22);
        }
        for (int j = 0; j < 9; ++j) {
            this.renderSpectatorCommand(j, this.client.getWindow().getScaledWidth() / 2 - 90 + j * 20 + 2, i + 3, height, state.getCommand(j));
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void renderSpectatorCommand(int slot, int x, float y, float alpha, SpectatorMenuCommand command) {
        this.client.getTextureManager().bindTexture(SPECTATOR_TEX);
        if (command != SpectatorMenu.BLANK_COMMAND) {
            int i = (int)(alpha * 255.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(x, y, 0.0f);
            float f = command.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.color4f(f, f, f, alpha);
            command.renderIcon(f, i);
            RenderSystem.popMatrix();
            String string = String.valueOf(this.client.options.keysHotbar[slot].getLocalizedName());
            if (i > 3 && command.isEnabled()) {
                this.client.textRenderer.drawWithShadow(string, x + 19 - 2 - this.client.textRenderer.getStringWidth(string), y + 6.0f + 3.0f, 0xFFFFFF + (i << 24));
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

