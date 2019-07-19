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

public class UpdateDifficultyLockC2SPacket
implements Packet<ServerPlayPacketListener> {
    private boolean difficultyLocked;

    public UpdateDifficultyLockC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public UpdateDifficultyLockC2SPacket(boolean bl) {
        this.difficultyLocked = bl;
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onUpdateDifficultyLock(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.difficultyLocked = packetByteBuf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeBoolean(this.difficultyLocked);
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }
}

