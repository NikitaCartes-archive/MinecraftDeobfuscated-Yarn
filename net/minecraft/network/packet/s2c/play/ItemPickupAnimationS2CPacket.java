/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ItemPickupAnimationS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int entityId;
    private final int collectorEntityId;
    private final int stackAmount;

    public ItemPickupAnimationS2CPacket(int entityId, int collectorId, int stackAmount) {
        this.entityId = entityId;
        this.collectorEntityId = collectorId;
        this.stackAmount = stackAmount;
    }

    public ItemPickupAnimationS2CPacket(PacketByteBuf packetByteBuf) {
        this.entityId = packetByteBuf.readVarInt();
        this.collectorEntityId = packetByteBuf.readVarInt();
        this.stackAmount = packetByteBuf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.collectorEntityId);
        buf.writeVarInt(this.stackAmount);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onItemPickupAnimation(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCollectorEntityId() {
        return this.collectorEntityId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getStackAmount() {
        return this.stackAmount;
    }
}

