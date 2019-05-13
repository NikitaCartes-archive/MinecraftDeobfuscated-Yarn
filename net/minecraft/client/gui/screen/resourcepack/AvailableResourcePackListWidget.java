/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.resourcepack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(value=EnvType.CLIENT)
public class AvailableResourcePackListWidget
extends ResourcePackListWidget {
    public AvailableResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
        super(minecraftClient, i, j, new TranslatableComponent("resourcePack.available.title", new Object[0]));
    }
}

