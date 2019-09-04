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
	private long field_20665;
	private boolean hardcore;
	private GameMode gameMode;
	private DimensionType dimension;
	private int maxPlayers;
	private LevelGeneratorType generatorType;
	private int chunkLoadDistance;
	private boolean reducedDebugInfo;
	private boolean field_20666;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(
		int i, GameMode gameMode, long l, boolean bl, DimensionType dimensionType, int j, LevelGeneratorType levelGeneratorType, int k, boolean bl2, boolean bl3
	) {
		this.playerEntityId = i;
		this.dimension = dimensionType;
		this.field_20665 = l;
		this.gameMode = gameMode;
		this.maxPlayers = j;
		this.hardcore = bl;
		this.generatorType = levelGeneratorType;
		this.chunkLoadDistance = k;
		this.reducedDebugInfo = bl2;
		this.field_20666 = bl3;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.playerEntityId = packetByteBuf.readInt();
		int i = packetByteBuf.readUnsignedByte();
		this.hardcore = (i & 8) == 8;
		i &= -9;
		this.gameMode = GameMode.byId(i);
		this.dimension = DimensionType.byRawId(packetByteBuf.readInt());
		this.field_20665 = packetByteBuf.readLong();
		this.maxPlayers = packetByteBuf.readUnsignedByte();
		this.generatorType = LevelGeneratorType.getTypeFromName(packetByteBuf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}

		this.chunkLoadDistance = packetByteBuf.readVarInt();
		this.reducedDebugInfo = packetByteBuf.readBoolean();
		this.field_20666 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.playerEntityId);
		int i = this.gameMode.getId();
		if (this.hardcore) {
			i |= 8;
		}

		packetByteBuf.writeByte(i);
		packetByteBuf.writeInt(this.dimension.getRawId());
		packetByteBuf.writeLong(this.field_20665);
		packetByteBuf.writeByte(this.maxPlayers);
		packetByteBuf.writeString(this.generatorType.getName());
		packetByteBuf.writeVarInt(this.chunkLoadDistance);
		packetByteBuf.writeBoolean(this.reducedDebugInfo);
		packetByteBuf.writeBoolean(this.field_20666);
	}

	public void method_11567(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.playerEntityId;
	}

	@Environment(EnvType.CLIENT)
	public long method_22423() {
		return this.field_20665;
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
		return this.dimension;
	}

	@Environment(EnvType.CLIENT)
	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}

	@Environment(EnvType.CLIENT)
	public int getChunkLoadDistance() {
		return this.chunkLoadDistance;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_22424() {
		return this.field_20666;
	}
}
