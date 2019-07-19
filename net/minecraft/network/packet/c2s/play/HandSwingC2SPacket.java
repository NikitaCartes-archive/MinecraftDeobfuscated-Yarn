/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class HandSwingC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Hand hand;

    public HandSwingC2SPacket() {
    }

    public HandSwingC2SPacket(Hand hand) {
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
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onHandSwing(this);
    }

    public Hand getHand() {
        return this.hand;
    }
}

