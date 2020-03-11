/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BlastFurnaceScreen
extends AbstractFurnaceScreen<BlastFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/blast_furnace.png");

    public BlastFurnaceScreen(BlastFurnaceScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, new BlastFurnaceRecipeBookScreen(), inventory, title, TEXTURE);
    }
}

