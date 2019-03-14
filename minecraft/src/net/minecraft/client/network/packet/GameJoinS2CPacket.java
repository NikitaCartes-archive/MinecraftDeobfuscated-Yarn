package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private int playerEntityId;
	private boolean hardcore;
	private GameMode gameMode;
	private DimensionType field_12284;
	private int maxPlayers;
	private LevelGeneratorType generatorType;
	private boolean reducedDebugInfo;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(int i, GameMode gameMode, boolean bl, DimensionType dimensionType, int j, LevelGeneratorType levelGeneratorType, boolean bl2) {
		this.playerEntityId = i;
		this.field_12284 = dimensionType;
		this.gameMode = gameMode;
		this.maxPlayers = j;
		this.hardcore = bl;
		this.generatorType = levelGeneratorType;
		this.reducedDebugInfo = bl2;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.playerEntityId = packetByteBuf.readInt();
		int i = packetByteBuf.readUnsignedByte();
		this.hardcore = (i & 8) == 8;
		i &= -9;
		this.gameMode = GameMode.byId(i);
		this.field_12284 = DimensionType.byRawId(packetByteBuf.readInt());
		this.maxPlayers = packetByteBuf.readUnsignedByte();
		this.generatorType = LevelGeneratorType.getTypeFromName(packetByteBuf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}

		this.reducedDebugInfo = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.playerEntityId);
		int i = this.gameMode.getId();
		if (this.hardcore) {
			i |= 8;
		}

		packetByteBuf.writeByte(i);
		packetByteBuf.writeInt(this.field_12284.getRawId());
		packetByteBuf.writeByte(this.maxPlayers);
		packetByteBuf.writeString(this.generatorType.getName());
		packetByteBuf.writeBoolean(this.reducedDebugInfo);
	}

	public void method_11567(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.playerEntityId;
	}

	@Environment(EnvType.CLIENT)
	public boolean isHardcore() {
		return this.hardcore;
	}

	@Environment(EnvType.CLIENT)
	public GameMode getGameMode() {
		return this.gameMode;
	}

	@Environment(EnvType.CLIENT)
	public DimensionType getDimension() {
		return this.field_12284;
	}

	@Environment(EnvType.CLIENT)
	public LevelGeneratorType method_11563() {
		return this.generatorType;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}
}
