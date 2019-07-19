/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class RequestCommandCompletionsC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int completionId;
    private String partialCommand;

    public RequestCommandCompletionsC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public RequestCommandCompletionsC2SPacket(int i, String string) {
        this.completionId = i;
        this.partialCommand = string;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.completionId = packetByteBuf.readVarInt();
        this.partialCommand = packetByteBuf.readString(32500);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.completionId);
        packetByteBuf.writeString(this.partialCommand, 32500);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onRequestCommandCompletions(this);
    }

    public int getCompletionId() {
        return this.completionId;
    }

    public String getPartialCommand() {
        return this.partialCommand;
    }
}

