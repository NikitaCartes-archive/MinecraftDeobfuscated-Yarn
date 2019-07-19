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
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class PlayerRespawnS2CPacket
implements Packet<ClientPlayPacketListener> {
    private DimensionType dimension;
    private GameMode gameMode;
    private LevelGeneratorType generatorType;

    public PlayerRespawnS2CPacket() {
    }

    public PlayerRespawnS2CPacket(DimensionType dimensionType, LevelGeneratorType levelGeneratorType, GameMode gameMode) {
        this.dimension = dimensionType;
        this.gameMode = gameMode;
        this.generatorType = levelGeneratorType;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerRespawn(this);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.dimension = DimensionType.byRawId(packetByteBuf.readInt());
        this.gameMode = GameMode.byId(packetByteBuf.readUnsignedByte());
        this.generatorType = LevelGeneratorType.getTypeFromName(packetByteBuf.readString(16));
        if (this.generatorType == null) {
            this.generatorType = LevelGeneratorType.DEFAULT;
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeInt(this.dimension.getRawId());
        packetByteBuf.writeByte(this.gameMode.getId());
        packetByteBuf.writeString(this.generatorType.getName());
    }

    @Environment(value=EnvType.CLIENT)
    public DimensionType getDimension() {
        return this.dimension;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }
}

