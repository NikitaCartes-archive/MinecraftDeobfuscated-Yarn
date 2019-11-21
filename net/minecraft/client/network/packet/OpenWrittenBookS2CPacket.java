/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class OpenWrittenBookS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Hand hand;

    public OpenWrittenBookS2CPacket() {
    }

    public OpenWrittenBookS2CPacket(Hand hand) {
        this.hand = hand;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.hand = packetByteBuf.readEnumConstant(Hand.class);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.hand);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onOpenWrittenBook(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Hand getHand() {
        return this.hand;
    }
}

