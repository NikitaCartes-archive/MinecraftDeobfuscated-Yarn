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

public class SetCameraEntityS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int entityId;

    public SetCameraEntityS2CPacket(Entity entity) {
        this.entityId = entity.getId();
    }

    public SetCameraEntityS2CPacket(PacketByteBuf packetByteBuf) {
        this.entityId = packetByteBuf.readVarInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onSetCameraEntity(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }
}

