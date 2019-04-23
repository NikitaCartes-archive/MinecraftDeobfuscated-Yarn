/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ContainerScreen9
extends ContainerScreen<Generic3x3Container> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/dispenser.png");

    public ContainerScreen9(Generic3x3Container generic3x3Container, PlayerInventory playerInventory, Component component) {
        super(generic3x3Container, playerInventory, component);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
    }

    @Override
    protected void drawForeground(int i, int j) {
        String string = this.title.getFormattedText();
        this.font.draw(string, this.containerWidth / 2 - this.font.getStringWidth(string) / 2, 6.0f, 0x404040);
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

