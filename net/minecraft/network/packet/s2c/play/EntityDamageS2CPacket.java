/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record EntityDamageS2CPacket(int entityId, int sourceTypeId, int sourceCauseId, int sourceDirectId, Optional<Vec3d> sourcePosition) implements Packet<ClientPlayPacketListener>
{
    public EntityDamageS2CPacket(Entity entity, DamageSource damageSource) {
        this(entity.getId(), entity.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getRawId(damageSource.getType()), damageSource.getAttacker() != null ? damageSource.getAttacker().getId() : -1, damageSource.getSource() != null ? damageSource.getSource().getId() : -1, Optional.ofNullable(damageSource.getStoredPosition()));
    }

    public EntityDamageS2CPacket(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), EntityDamageS2CPacket.readOffsetVarInt(buf), EntityDamageS2CPacket.readOffsetVarInt(buf), buf.readOptional(pos -> new Vec3d(pos.readDouble(), pos.readDouble(), pos.readDouble())));
    }

    private static void writeOffsetVarInt(PacketByteBuf buf, int value) {
        buf.writeVarInt(value + 1);
    }

    private static int readOffsetVarInt(PacketByteBuf buf) {
        return buf.readVarInt() - 1;
    }

    @Override
    public void write(PacketByteBuf buf2) {
        buf2.writeVarInt(this.entityId);
        buf2.writeVarInt(this.sourceTypeId);
        EntityDamageS2CPacket.writeOffsetVarInt(buf2, this.sourceCauseId);
        EntityDamageS2CPacket.writeOffsetVarInt(buf2, this.sourceDirectId);
        buf2.writeOptional(this.sourcePosition, (buf, pos) -> {
            buf.writeDouble(pos.getX());
            buf.writeDouble(pos.getY());
            buf.writeDouble(pos.getZ());
        });
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityDamage(this);
    }

    public DamageSource createDamageSource(World world) {
        RegistryEntry registryEntry = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(this.sourceTypeId).get();
        if (this.sourcePosition.isPresent()) {
            return new DamageSource((RegistryEntry<DamageType>)registryEntry, this.sourcePosition.get());
        }
        Entity entity = world.getEntityById(this.sourceCauseId);
        Entity entity2 = world.getEntityById(this.sourceDirectId);
        return new DamageSource(registryEntry, entity2, entity);
    }
}

