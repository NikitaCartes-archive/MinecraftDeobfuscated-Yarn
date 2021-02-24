/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.login;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.text.Text;

public class LoginDisconnectS2CPacket
implements Packet<ClientLoginPacketListener> {
    private final Text reason;

    public LoginDisconnectS2CPacket(Text reason) {
        this.reason = reason;
    }

    public LoginDisconnectS2CPacket(PacketByteBuf packetByteBuf) {
        this.reason = Text.Serializer.fromLenientJson(packetByteBuf.readString(262144));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeText(this.reason);
    }

    @Override
    public void apply(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.onDisconnect(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getReason() {
        return this.reason;
    }
}

