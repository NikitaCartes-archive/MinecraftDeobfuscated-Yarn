/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GrindstoneScreen
extends AbstractContainerScreen<GrindstoneContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/grindstone.png");

    public GrindstoneScreen(GrindstoneContainer grindstoneContainer, PlayerInventory playerInventory, Text text) {
        super(grindstoneContainer, playerInventory, text);
    }

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.asFormattedString(), 8.0f, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        this.drawBackground(f, i, j);
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.containerWidth) / 2;
        int l = (this.height - this.containerHeight) / 2;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        if ((((GrindstoneContainer)this.container).getSlot(0).hasStack() || ((GrindstoneContainer)this.container).getSlot(1).hasStack()) && !((GrindstoneContainer)this.container).getSlot(2).hasStack()) {
            this.blit(k + 92, l + 31, this.containerWidth, 0, 28, 21);
        }
    }
}

