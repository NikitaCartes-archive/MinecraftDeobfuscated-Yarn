/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class EntityAttachS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int attachedId;
    private int holdingId;

    public EntityAttachS2CPacket() {
    }

    public EntityAttachS2CPacket(Entity attachedEntity, @Nullable Entity holdingEntity) {
        this.attachedId = attachedEntity.getEntityId();
        this.holdingId = holdingEntity != null ? holdingEntity.getEntityId() : 0;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.attachedId = buf.readInt();
        this.holdingId = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeInt(this.attachedId);
        buf.writeInt(this.holdingId);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAttach(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getAttachedEntityId() {
        return this.attachedId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getHoldingEntityId() {
        return this.holdingId;
    }
}

