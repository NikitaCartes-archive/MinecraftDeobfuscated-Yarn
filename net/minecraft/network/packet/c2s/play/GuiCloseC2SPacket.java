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

public class GuiCloseC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int id;

    public GuiCloseC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public GuiCloseC2SPacket(int i) {
        this.id = i;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onGuiClose(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeByte(this.id);
    }
}

