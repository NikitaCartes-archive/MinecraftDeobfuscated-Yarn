/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

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

    public SelectAdvancementTabS2CPacket(@Nullable Identifier tabId) {
        this.tabId = tabId;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSelectAdvancementTab(this);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        if (buf.readBoolean()) {
            this.tabId = buf.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBoolean(this.tabId != null);
        if (this.tabId != null) {
            buf.writeIdentifier(this.tabId);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Identifier getTabId() {
        return this.tabId;
    }
}

