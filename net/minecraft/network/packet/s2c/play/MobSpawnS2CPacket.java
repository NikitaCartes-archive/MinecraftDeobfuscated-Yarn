/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class MobSpawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private UUID uuid;
    private int entityTypeId;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headYaw;

    public MobSpawnS2CPacket() {
    }

    public MobSpawnS2CPacket(LivingEntity entity) {
        this.id = entity.getEntityId();
        this.uuid = entity.getUuid();
        this.entityTypeId = Registry.ENTITY_TYPE.getRawId(entity.getType());
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.yaw = (byte)(entity.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(entity.pitch * 256.0f / 360.0f);
        this.headYaw = (byte)(entity.headYaw * 256.0f / 360.0f);
        double d = 3.9;
        Vec3d vec3d = entity.getVelocity();
        double e = MathHelper.clamp(vec3d.x, -3.9, 3.9);
        double f = MathHelper.clamp(vec3d.y, -3.9, 3.9);
        double g = MathHelper.clamp(vec3d.z, -3.9, 3.9);
        this.velocityX = (int)(e * 8000.0);
        this.velocityY = (int)(f * 8000.0);
        this.velocityZ = (int)(g * 8000.0);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.entityTypeId = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.headYaw = buf.readByte();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeVarInt(this.entityTypeId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeByte(this.headYaw);
        buf.writeShort(this.velocityX);
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onMobSpawn(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityTypeId() {
        return this.entityTypeId;
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
    public int getVelocityX() {
        return this.velocityX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getVelocityY() {
        return this.velocityY;
    }

    @Environment(value=EnvType.CLIENT)
    public int getVelocityZ() {
        return this.velocityZ;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getHeadYaw() {
        return this.headYaw;
    }
}

