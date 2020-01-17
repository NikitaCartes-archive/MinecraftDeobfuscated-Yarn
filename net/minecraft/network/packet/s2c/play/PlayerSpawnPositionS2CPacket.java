/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnPositionS2CPacket
implements Packet<ClientPlayPacketListener> {
    private BlockPos pos;

    public PlayerSpawnPositionS2CPacket() {
    }

    public PlayerSpawnPositionS2CPacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerSpawnPosition(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}

