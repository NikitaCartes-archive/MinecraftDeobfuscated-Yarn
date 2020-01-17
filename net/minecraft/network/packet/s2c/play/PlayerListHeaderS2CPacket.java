/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class PlayerListHeaderS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Text header;
    private Text footer;

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.header = buf.readText();
        this.footer = buf.readText();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeText(this.header);
        buf.writeText(this.footer);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerListHeader(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getHeader() {
        return this.header;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getFooter() {
        return this.footer;
    }
}

