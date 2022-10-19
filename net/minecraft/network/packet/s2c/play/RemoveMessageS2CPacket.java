/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageSignatureData;

public record RemoveMessageS2CPacket(MessageSignatureData.Indexed messageSignature) implements Packet<ClientPlayPacketListener>
{
    public RemoveMessageS2CPacket(PacketByteBuf buf) {
        this(MessageSignatureData.Indexed.fromBuf(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        MessageSignatureData.Indexed.write(buf, this.messageSignature);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onRemoveMessage(this);
    }
}

