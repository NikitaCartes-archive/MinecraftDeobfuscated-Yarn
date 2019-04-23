/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.container.AbstractFurnaceScreen;
import net.minecraft.client.gui.container.FurnaceRecipeBookScreen;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FurnaceScreen
extends AbstractFurnaceScreen<FurnaceContainer> {
    private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

    public FurnaceScreen(FurnaceContainer furnaceContainer, PlayerInventory playerInventory, Component component) {
        super(furnaceContainer, new FurnaceRecipeBookScreen(), playerInventory, component, BG_TEX);
    }
}

