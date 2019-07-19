/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerActionC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private Direction direction;
    private Action action;

    public PlayerActionC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerActionC2SPacket(Action action, BlockPos blockPos, Direction direction) {
        this.action = action;
        this.pos = blockPos.toImmutable();
        this.direction = direction;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.action = packetByteBuf.readEnumConstant(Action.class);
        this.pos = packetByteBuf.readBlockPos();
        this.direction = Direction.byId(packetByteBuf.readUnsignedByte());
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.action);
        packetByteBuf.writeBlockPos(this.pos);
        packetByteBuf.writeByte(this.direction.getId());
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerAction(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS;

    }
}

