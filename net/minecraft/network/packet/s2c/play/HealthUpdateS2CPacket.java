/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class HealthUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final float health;
    private final int food;
    private final float saturation;

    public HealthUpdateS2CPacket(float health, int food, float saturation) {
        this.health = health;
        this.food = food;
        this.saturation = saturation;
    }

    public HealthUpdateS2CPacket(PacketByteBuf buf) {
        this.health = buf.readFloat();
        this.food = buf.readVarInt();
        this.saturation = buf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.health);
        buf.writeVarInt(this.food);
        buf.writeFloat(this.saturation);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onHealthUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHealth() {
        return this.health;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFood() {
        return this.food;
    }

    @Environment(value=EnvType.CLIENT)
    public float getSaturation() {
        return this.saturation;
    }
}

