/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface ClientChatListener {
    public void onChatMessage(MessageType var1, Text var2, UUID var3);
}

