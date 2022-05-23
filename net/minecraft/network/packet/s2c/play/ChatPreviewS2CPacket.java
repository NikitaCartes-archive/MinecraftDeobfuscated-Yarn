/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record ChatPreviewS2CPacket(int queryId, @Nullable Text preview) implements Packet<ClientPlayPacketListener>
{
    public ChatPreviewS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), (Text)buf.readNullable(PacketByteBuf::readText));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.queryId);
        buf.writeNullable(this.preview, PacketByteBuf::writeText);
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return true;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onChatPreview(this);
    }

    @Nullable
    public Text preview() {
        return this.preview;
    }
}

