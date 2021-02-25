/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EntityStatusS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int id;
    private final byte status;

    public EntityStatusS2CPacket(Entity entity, byte status) {
        this.id = entity.getId();
        this.status = status;
    }

    public EntityStatusS2CPacket(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.status = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeByte(this.status);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityStatus(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World world) {
        return world.getEntityById(this.id);
    }

    @Environment(value=EnvType.CLIENT)
    public byte getStatus() {
        return this.status;
    }
}

