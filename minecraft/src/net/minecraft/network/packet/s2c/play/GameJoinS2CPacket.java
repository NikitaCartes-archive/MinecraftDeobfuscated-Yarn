package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private int playerEntityId;
	private long seed;
	private boolean hardcore;
	private GameMode gameMode;
	private DimensionType dimension;
	private int maxPlayers;
	private int chunkLoadDistance;
	private boolean reducedDebugInfo;
	private boolean showDeathScreen;
	private boolean debugWorld;
	private boolean flatWorld;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(
		int playerEntityId,
		GameMode gameMode,
		long seed,
		boolean hardcore,
		DimensionType dimensionType,
		int maxPlayers,
		int chunkLoadDistance,
		boolean reducedDebugInfo,
		boolean showDeathScreen,
		boolean debugWorld,
		boolean flatWorld
	) {
		this.playerEntityId = playerEntityId;
		this.dimension = dimensionType;
		this.seed = seed;
		this.gameMode = gameMode;
		this.maxPlayers = maxPlayers;
		this.hardcore = hardcore;
		this.chunkLoadDistance = chunkLoadDistance;
		this.reducedDebugInfo = reducedDebugInfo;
		this.showDeathScreen = showDeathScreen;
		this.debugWorld = debugWorld;
		this.flatWorld = flatWorld;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.playerEntityId = buf.readInt();
		int i = buf.readUnsignedByte();
		this.hardcore = (i & 8) == 8;
		i &= -9;
		this.gameMode = GameMode.byId(i);
		this.dimension = DimensionType.byRawId(buf.readInt());
		this.seed = buf.readLong();
		this.maxPlayers = buf.readUnsignedByte();
		this.chunkLoadDistance = buf.readVarInt();
		this.reducedDebugInfo = buf.readBoolean();
		this.showDeathScreen = buf.readBoolean();
		this.debugWorld = buf.readBoolean();
		this.flatWorld = buf.readBoolean();
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
		buf.writeLong(this.seed);
		buf.writeByte(this.maxPlayers);
		buf.writeVarInt(this.chunkLoadDistance);
		buf.writeBoolean(this.reducedDebugInfo);
		buf.writeBoolean(this.showDeathScreen);
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.playerEntityId;
	}

	@Environment(EnvType.CLIENT)
	public long getSeed() {
		return this.seed;
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
	public int getChunkLoadDistance() {
		return this.chunkLoadDistance;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	@Environment(EnvType.CLIENT)
	public boolean showsDeathScreen() {
		return this.showDeathScreen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isDebugWorld() {
		return this.debugWorld;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFlatWorld() {
		return this.flatWorld;
	}
}
