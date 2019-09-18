/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BrewingStandScreen
extends AbstractContainerScreen<BrewingStandContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
    private static final int[] field_2824 = new int[]{29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandContainer brewingStandContainer, PlayerInventory playerInventory, Text text) {
        super(brewingStandContainer, playerInventory, text);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        super.render(i, j, f);
        this.drawMouseoverTooltip(i, j);
    }

    @Override
    protected void drawForeground(int i, int j) {
        this.font.draw(this.title.asFormattedString(), this.containerWidth / 2 - this.font.getStringWidth(this.title.asFormattedString()) / 2, 6.0f, 0x404040);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0f, this.containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float f, int i, int j) {
        int o;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.containerWidth) / 2;
        int l = (this.height - this.containerHeight) / 2;
        this.blit(k, l, 0, 0, this.containerWidth, this.containerHeight);
        int m = ((BrewingStandContainer)this.container).getFuel();
        int n = MathHelper.clamp((18 * m + 20 - 1) / 20, 0, 18);
        if (n > 0) {
            this.blit(k + 60, l + 44, 176, 29, n, 4);
        }
        if ((o = ((BrewingStandContainer)this.container).getBrewTime()) > 0) {
            int p = (int)(28.0f * (1.0f - (float)o / 400.0f));
            if (p > 0) {
                this.blit(k + 97, l + 16, 176, 0, 9, p);
            }
            if ((p = field_2824[o / 2 % 7]) > 0) {
                this.blit(k + 63, l + 14 + 29 - p, 185, 29 - p, 12, p);
            }
        }
    }
}

