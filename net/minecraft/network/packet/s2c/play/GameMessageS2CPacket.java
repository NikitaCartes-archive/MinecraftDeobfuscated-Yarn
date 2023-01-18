/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public record GameMessageS2CPacket(Text content, boolean overlay) implements Packet<ClientPlayPacketListener>
{
    public GameMessageS2CPacket(PacketByteBuf buf) {
        this(buf.readText(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeText(this.content);
        buf.writeBoolean(this.overlay);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameMessage(this);
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return true;
    }
}

