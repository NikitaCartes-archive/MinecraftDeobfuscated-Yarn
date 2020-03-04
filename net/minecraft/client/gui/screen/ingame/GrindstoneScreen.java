/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ScreenWithHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GrindstoneScreen
extends ScreenWithHandler<GrindstoneScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/grindstone.png");

    public GrindstoneScreen(GrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.textRenderer.draw(this.title.asFormattedString(), 8.0f, 6.0f, 0x404040);
        this.textRenderer.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.backgroundHeight - 96 + 2, 0x404040);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawBackground(delta, mouseX, mouseY);
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.blit(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if ((((GrindstoneScreenHandler)this.handler).getSlot(0).hasStack() || ((GrindstoneScreenHandler)this.handler).getSlot(1).hasStack()) && !((GrindstoneScreenHandler)this.handler).getSlot(2).hasStack()) {
            this.blit(i + 92, j + 31, this.backgroundWidth, 0, 28, 21);
        }
    }
}

