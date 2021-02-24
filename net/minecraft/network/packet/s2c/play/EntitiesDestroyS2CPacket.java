/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntitiesDestroyS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final IntList entityIds;

    public EntitiesDestroyS2CPacket(IntList intList) {
        this.entityIds = new IntArrayList(intList);
    }

    public EntitiesDestroyS2CPacket(int ... entityIds) {
        this.entityIds = new IntArrayList(entityIds);
    }

    public EntitiesDestroyS2CPacket(PacketByteBuf packetByteBuf) {
        this.entityIds = packetByteBuf.method_34059();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.method_34060(this.entityIds);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntitiesDestroy(this);
    }

    @Environment(value=EnvType.CLIENT)
    public IntList getEntityIds() {
        return this.entityIds;
    }
}

