/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BrewingStandScreen
extends ContainerScreen<BrewingStandContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandContainer container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), this.containerWidth / 2 - this.font.getStringWidth(this.title.asFormattedString()) / 2, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        int m;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.containerWidth) / 2;
        int j = (this.height - this.containerHeight) / 2;
        this.blit(i, j, 0, 0, this.containerWidth, this.containerHeight);
        int k = ((BrewingStandContainer)this.container).getFuel();
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            this.blit(i + 60, j + 44, 176, 29, l, 4);
        }
        if ((m = ((BrewingStandContainer)this.container).getBrewTime()) > 0) {
            int n = (int)(28.0f * (1.0f - (float)m / 400.0f));
            if (n > 0) {
                this.blit(i + 97, j + 16, 176, 0, 9, n);
            }
            if ((n = BUBBLE_PROGRESS[m / 2 % 7]) > 0) {
                this.blit(i + 63, j + 14 + 29 - n, 185, 29 - n, 12, n);
            }
        }
    }
}

