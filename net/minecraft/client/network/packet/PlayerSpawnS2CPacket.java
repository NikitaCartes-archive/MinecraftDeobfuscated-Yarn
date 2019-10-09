/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerSpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;

    public PlayerSpawnS2CPacket() {
    }

    public PlayerSpawnS2CPacket(PlayerEntity playerEntity) {
        this.id = playerEntity.getEntityId();
        this.uuid = playerEntity.getGameProfile().getId();
        this.x = playerEntity.getX();
        this.y = playerEntity.getY();
        this.z = playerEntity.getZ();
        this.yaw = (byte)(playerEntity.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(playerEntity.pitch * 256.0f / 360.0f);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.id = packetByteBuf.readVarInt();
        this.uuid = packetByteBuf.readUuid();
        this.x = packetByteBuf.readDouble();
        this.y = packetByteBuf.readDouble();
        this.z = packetByteBuf.readDouble();
        this.yaw = packetByteBuf.readByte();
        this.pitch = packetByteBuf.readByte();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.id);
        packetByteBuf.writeUuid(this.uuid);
        packetByteBuf.writeDouble(this.x);
        packetByteBuf.writeDouble(this.y);
        packetByteBuf.writeDouble(this.z);
        packetByteBuf.writeByte(this.yaw);
        packetByteBuf.writeByte(this.pitch);
    }

    public void method_11235(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerSpawn(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getPlayerUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return this.x;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return this.y;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }
}

