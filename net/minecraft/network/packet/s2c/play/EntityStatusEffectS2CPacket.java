/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityStatusEffectS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int entityId;
    private final byte effectId;
    private final byte amplifier;
    private final int duration;
    private final byte flags;

    public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect) {
        this.entityId = entityId;
        this.effectId = (byte)(StatusEffect.getRawId(effect.getEffectType()) & 0xFF);
        this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
        this.duration = effect.getDuration() > Short.MAX_VALUE ? Short.MAX_VALUE : effect.getDuration();
        byte b = 0;
        if (effect.isAmbient()) {
            b = (byte)(b | 1);
        }
        if (effect.shouldShowParticles()) {
            b = (byte)(b | 2);
        }
        if (effect.shouldShowIcon()) {
            b = (byte)(b | 4);
        }
        this.flags = b;
    }

    public EntityStatusEffectS2CPacket(PacketByteBuf packetByteBuf) {
        this.entityId = packetByteBuf.readVarInt();
        this.effectId = packetByteBuf.readByte();
        this.amplifier = packetByteBuf.readByte();
        this.duration = packetByteBuf.readVarInt();
        this.flags = packetByteBuf.readByte();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.effectId);
        buf.writeByte(this.amplifier);
        buf.writeVarInt(this.duration);
        buf.writeByte(this.flags);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isPermanent() {
        return this.duration == Short.MAX_VALUE;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityPotionEffect(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getEffectId() {
        return this.effectId;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getAmplifier() {
        return this.amplifier;
    }

    @Environment(value=EnvType.CLIENT)
    public int getDuration() {
        return this.duration;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowParticles() {
        return (this.flags & 2) == 2;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isAmbient() {
        return (this.flags & 1) == 1;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowIcon() {
        return (this.flags & 4) == 4;
    }
}

