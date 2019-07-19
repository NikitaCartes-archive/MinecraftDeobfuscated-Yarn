/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerActionResponseS2CPacket
implements Packet<ClientPlayPacketListener> {
    private static final Logger LOGGER = LogManager.getLogger();
    private BlockPos pos;
    private BlockState state;
    PlayerActionC2SPacket.Action action;
    private boolean field_20323;

    public PlayerActionResponseS2CPacket() {
    }

    public PlayerActionResponseS2CPacket(BlockPos blockPos, BlockState blockState, PlayerActionC2SPacket.Action action, boolean bl) {
        this.pos = blockPos.toImmutable();
        this.state = blockState;
        this.action = action;
        this.field_20323 = bl;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.pos = packetByteBuf.readBlockPos();
        this.state = Block.STATE_IDS.get(packetByteBuf.readVarInt());
        this.action = packetByteBuf.readEnumConstant(PlayerActionC2SPacket.Action.class);
        this.field_20323 = packetByteBuf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeBlockPos(this.pos);
        packetByteBuf.writeVarInt(Block.getRawIdFromState(this.state));
        packetByteBuf.writeEnumConstant(this.action);
        packetByteBuf.writeBoolean(this.field_20323);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.method_21707(this);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockState getBlockState() {
        return this.state;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getBlockPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_21711() {
        return this.field_20323;
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerActionC2SPacket.Action getAction() {
        return this.action;
    }
}

