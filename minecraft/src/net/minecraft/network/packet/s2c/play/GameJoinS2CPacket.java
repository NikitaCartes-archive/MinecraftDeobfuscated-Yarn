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

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private int playerEntityId;
	private boolean hardcore;
	private GameMode gameMode;
	private DimensionType dimension;
	private int maxPlayers;
	private LevelGeneratorType generatorType;
	private int chunkLoadDistance;
	private boolean reducedDebugInfo;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(
		int playerEntityId,
		GameMode gameMode,
		boolean hardcore,
		DimensionType dimension,
		int maxPlayers,
		LevelGeneratorType generatorType,
		int chunkLoadDistance,
		boolean reducedDebugInfo
	) {
		this.playerEntityId = playerEntityId;
		this.dimension = dimension;
		this.gameMode = gameMode;
		this.maxPlayers = maxPlayers;
		this.hardcore = hardcore;
		this.generatorType = generatorType;
		this.chunkLoadDistance = chunkLoadDistance;
		this.reducedDebugInfo = reducedDebugInfo;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.playerEntityId = buf.readInt();
		int i = buf.readUnsignedByte();
		this.hardcore = (i & 8) == 8;
		i &= -9;
		this.gameMode = GameMode.byId(i);
		this.dimension = DimensionType.byRawId(buf.readInt());
		this.maxPlayers = buf.readUnsignedByte();
		this.generatorType = LevelGeneratorType.getTypeFromName(buf.readString(16));
		if (this.generatorType == null) {
			this.generatorType = LevelGeneratorType.DEFAULT;
		}

		this.chunkLoadDistance = buf.readVarInt();
		this.reducedDebugInfo = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.playerEntityId);
		int i = this.gameMode.getId();
		if (this.hardcore) {
			i |= 8;
		}

		buf.writeByte(i);
		buf.writeInt(this.dimension.getRawId());
		buf.writeByte(this.maxPlayers);
		buf.writeString(this.generatorType.getName());
		buf.writeVarInt(this.chunkLoadDistance);
		buf.writeBoolean(this.reducedDebugInfo);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
}
