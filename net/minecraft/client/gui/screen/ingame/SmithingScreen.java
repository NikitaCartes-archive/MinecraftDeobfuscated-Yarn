/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SmithingScreen
extends ForgingScreen<SmithingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/smithing.png");

    public SmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, TEXTURE);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        this.textRenderer.draw(matrices, this.title, 60.0f, 20.0f, 0x404040);
        this.textRenderer.draw(matrices, this.playerInventory.getDisplayName(), 8.0f, (float)(this.backgroundHeight - 96 + 2), 0x404040);
    }
}

