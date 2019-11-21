/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityAnimationS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private int animationId;

    public EntityAnimationS2CPacket() {
    }

    public EntityAnimationS2CPacket(Entity entity, int i) {
        this.id = entity.getEntityId();
        this.animationId = i;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.id = packetByteBuf.readVarInt();
        this.animationId = packetByteBuf.readUnsignedByte();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.id);
        packetByteBuf.writeByte(this.animationId);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAnimation(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public int getAnimationId() {
        return this.animationId;
    }
}

