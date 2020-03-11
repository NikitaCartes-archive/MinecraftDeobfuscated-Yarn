/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;

public class EntitySetHeadYawS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int entity;
    private byte headYaw;

    public EntitySetHeadYawS2CPacket() {
    }

    public EntitySetHeadYawS2CPacket(Entity entity, byte headYaw) {
        this.entity = entity.getEntityId();
        this.headYaw = headYaw;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entity = buf.readVarInt();
        this.headYaw = buf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entity);
        buf.writeByte(this.headYaw);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntitySetHeadYaw(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World world) {
        return world.getEntityById(this.entity);
    }

    @Environment(value=EnvType.CLIENT)
    public byte getHeadYaw() {
        return this.headYaw;
    }
}

