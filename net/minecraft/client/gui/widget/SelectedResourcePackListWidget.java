/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ResourcePackListWidget;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(value=EnvType.CLIENT)
public class SelectedResourcePackListWidget
extends ResourcePackListWidget {
    public SelectedResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
        super(minecraftClient, i, j, new TranslatableComponent("resourcePack.selected.title", new Object[0]));
    }
}

