/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.HopperContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HopperScreen
extends ContainerScreen<HopperContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/hopper.png");

    public HopperScreen(HopperContainer hopperContainer, PlayerInventory playerInventory, Component component) {
        super(hopperContainer, playerInventory, component);
        this.passEvents = false;
        this.containerHeight = 133;
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
    }

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.getFormattedText(), 8.0f, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.containerWidth) / 2;
        int l = (this.height - this.containerHeight) / 2;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
    }
}

