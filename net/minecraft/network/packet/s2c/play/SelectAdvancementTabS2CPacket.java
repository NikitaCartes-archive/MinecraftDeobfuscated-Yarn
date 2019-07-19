/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class SelectAdvancementTabS2CPacket
implements Packet<ClientPlayPacketListener> {
    @Nullable
    private Identifier tabId;

    public SelectAdvancementTabS2CPacket() {
    }

    public SelectAdvancementTabS2CPacket(@Nullable Identifier identifier) {
        this.tabId = identifier;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSelectAdvancementTab(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        if (packetByteBuf.readBoolean()) {
            this.tabId = packetByteBuf.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeBoolean(this.tabId != null);
        if (this.tabId != null) {
            packetByteBuf.writeIdentifier(this.tabId);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Identifier getTabId() {
        return this.tabId;
    }
}

