/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class PlayerInteractBlockC2SPacket
implements Packet<ServerPlayPacketListener> {
    private BlockHitResult blockHitResult;
    private Hand hand;

    public PlayerInteractBlockC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInteractBlockC2SPacket(Hand hand, BlockHitResult blockHitResult) {
        this.hand = hand;
        this.blockHitResult = blockHitResult;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.hand = buf.readEnumConstant(Hand.class);
        this.blockHitResult = buf.readBlockHitResult();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.hand);
        buf.writeBlockHitResult(this.blockHitResult);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerInteractBlock(this);
    }

    public Hand getHand() {
        return this.hand;
    }

    public BlockHitResult getBlockHitResult() {
        return this.blockHitResult;
    }
}

