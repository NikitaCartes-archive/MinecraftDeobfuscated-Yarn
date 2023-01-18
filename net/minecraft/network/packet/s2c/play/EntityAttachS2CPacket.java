/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

public class EntityAttachS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int attachedId;
    private final int holdingId;

    public EntityAttachS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
        this.attachedId = attachedEntity.getId();
        this.holdingId = holdingEntity != null ? holdingEntity.getId() : 0;
    }

    public EntityAttachS2CPacket(PacketByteBuf buf) {
        this.attachedId = buf.readInt();
        this.holdingId = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.attachedId);
        buf.writeInt(this.holdingId);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAttach(this);
    }

    public int getAttachedEntityId() {
        return this.attachedId;
    }

    public int getHoldingEntityId() {
        return this.holdingId;
    }
}

