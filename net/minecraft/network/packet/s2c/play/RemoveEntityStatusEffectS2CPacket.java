/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RemoveEntityStatusEffectS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int entityId;
    private final StatusEffect effectType;

    public RemoveEntityStatusEffectS2CPacket(int entityId, StatusEffect effectType) {
        this.entityId = entityId;
        this.effectType = effectType;
    }

    public RemoveEntityStatusEffectS2CPacket(PacketByteBuf packetByteBuf) {
        this.entityId = packetByteBuf.readVarInt();
        this.effectType = StatusEffect.byRawId(packetByteBuf.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeByte(StatusEffect.getRawId(this.effectType));
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onRemoveEntityEffect(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StatusEffect getEffectType() {
        return this.effectType;
    }
}

