package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private DimensionType dimension;
	private Difficulty difficulty;
	private GameMode gameMode;
	private LevelGeneratorType generatorType;

	public PlayerRespawnS2CPacket() {
	}

	public PlayerRespawnS2CPacket(DimensionType dimensionType, Difficulty difficulty, LevelGeneratorType levelGeneratorType, GameMode gameMode) {
		this.dimension = dimensionType;
		this.difficulty = difficulty;
		this.gameMode = gameMode;
		this.generatorType = levelGeneratorType;
	}

	public void method_11782(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.dimension = DimensionType.byRawId(packetByteBuf.readInt());
		this.difficulty = Difficulty.getDifficulty(packetByteBuf.readUnsignedByte());
		this.gameMode = GameMode.byId(packetByteBuf.readUnsignedByte());
		this.generatorType = LevelGeneratorType.getTypeFromName(packetByteBuf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.dimension.getRawId());
		packetByteBuf.writeByte(this.difficulty.getId());
		packetByteBuf.writeByte(this.gameMode.getId());
		packetByteBuf.writeString(this.generatorType.getName());
	}

	@Environment(EnvType.CLIENT)
	public DimensionType getDimension() {
		return this.dimension;
	}

	@Environment(EnvType.CLIENT)
	public Difficulty getDifficulty() {
		return this.difficulty;
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
