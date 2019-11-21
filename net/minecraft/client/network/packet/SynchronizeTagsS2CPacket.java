/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.PacketByteBuf;

public class SynchronizeTagsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private RegistryTagManager tagManager;

    public SynchronizeTagsS2CPacket() {
    }

    public SynchronizeTagsS2CPacket(RegistryTagManager registryTagManager) {
        this.tagManager = registryTagManager;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.tagManager = RegistryTagManager.fromPacket(packetByteBuf);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        this.tagManager.toPacket(packetByteBuf);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSynchronizeTags(this);
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryTagManager getTagManager() {
        return this.tagManager;
    }
}

