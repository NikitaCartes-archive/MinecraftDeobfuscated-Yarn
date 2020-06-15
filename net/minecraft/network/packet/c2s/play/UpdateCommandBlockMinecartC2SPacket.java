/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UpdateCommandBlockMinecartC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int entityId;
    private String command;
    private boolean trackOutput;

    public UpdateCommandBlockMinecartC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateCommandBlockMinecartC2SPacket(int entityId, String command, boolean trackOutput) {
        this.entityId = entityId;
        this.command = command;
        this.trackOutput = trackOutput;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.command = buf.readString(Short.MAX_VALUE);
        this.trackOutput = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeString(this.command);
        buf.writeBoolean(this.trackOutput);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onUpdateCommandBlockMinecart(this);
    }

    @Nullable
    public CommandBlockExecutor getMinecartCommandExecutor(World world) {
        Entity entity = world.getEntityById(this.entityId);
        if (entity instanceof CommandBlockMinecartEntity) {
            return ((CommandBlockMinecartEntity)entity).getCommandExecutor();
        }
        return null;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }
}

