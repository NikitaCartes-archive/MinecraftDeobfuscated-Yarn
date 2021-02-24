/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class class_5903
implements Packet<ClientPlayPacketListener> {
    private final Text field_29165;

    public class_5903(Text text) {
        this.field_29165 = text;
    }

    public class_5903(PacketByteBuf packetByteBuf) {
        this.field_29165 = packetByteBuf.readText();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeText(this.field_29165);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.method_34082(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Text method_34190() {
        return this.field_29165;
    }
}

