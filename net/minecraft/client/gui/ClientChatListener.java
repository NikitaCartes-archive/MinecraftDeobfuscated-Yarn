/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public interface ClientChatListener {
    public void onChatMessage(ChatMessageType var1, Component var2);
}

