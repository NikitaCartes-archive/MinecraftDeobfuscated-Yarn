package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private DimensionType dimension;
	private long sha256Seed;
	private GameMode gameMode;
	private LevelGeneratorType generatorType;

	public PlayerRespawnS2CPacket() {
	}

	public PlayerRespawnS2CPacket(DimensionType dimension, long sha256Seed, LevelGeneratorType generatorType, GameMode gameMode) {
		this.dimension = dimension;
		this.sha256Seed = sha256Seed;
		this.gameMode = gameMode;
		this.generatorType = generatorType;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.dimension = DimensionType.byRawId(buf.readInt());
		this.sha256Seed = buf.readLong();
		this.gameMode = GameMode.byId(buf.readUnsignedByte());
		this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.dimension.getRawId());
		buf.writeLong(this.sha256Seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeString(this.generatorType.getName());
	}

	@Environment(EnvType.CLIENT)
	public DimensionType getDimension() {
		return this.dimension;
	}

	@Environment(EnvType.CLIENT)
	public long getSha256Seed() {
		return this.sha256Seed;
	}

	@Environment(EnvType.CLIENT)
	public GameMode getGameMode() {
		return this.gameMode;
	}

	@Environment(EnvType.CLIENT)
	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}
}
