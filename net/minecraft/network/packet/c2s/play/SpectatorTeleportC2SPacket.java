/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class SpectatorTeleportC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final UUID targetUuid;

    public SpectatorTeleportC2SPacket(UUID targetUuid) {
        this.targetUuid = targetUuid;
    }

    public SpectatorTeleportC2SPacket(PacketByteBuf buf) {
        this.targetUuid = buf.readUuid();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.targetUuid);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onSpectatorTeleport(this);
    }

    @Nullable
    public Entity getTarget(ServerWorld world) {
        return world.getEntity(this.targetUuid);
    }
}

