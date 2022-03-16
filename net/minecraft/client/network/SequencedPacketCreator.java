/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface SequencedPacketCreator {
    public Packet<ServerPlayPacketListener> predict(int var1);
}

