/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.LegacySmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Deprecated(forRemoval=true)
@Environment(value=EnvType.CLIENT)
public class LegacySmithingScreen
extends ForgingScreen<LegacySmithingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/legacy_smithing.png");

    public LegacySmithingScreen(LegacySmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, TEXTURE);
        this.titleX = 60;
        this.titleY = 18;
    }

    @Override
    protected void drawInvalidRecipeArrow(MatrixStack matrices, int x, int y) {
        if ((((LegacySmithingScreenHandler)this.handler).getSlot(0).hasStack() || ((LegacySmithingScreenHandler)this.handler).getSlot(1).hasStack()) && !((LegacySmithingScreenHandler)this.handler).getSlot(((LegacySmithingScreenHandler)this.handler).getResultSlotIndex()).hasStack()) {
            LegacySmithingScreen.drawTexture(matrices, x + 99, y + 45, this.backgroundWidth, 0, 28, 21);
        }
    }
}

