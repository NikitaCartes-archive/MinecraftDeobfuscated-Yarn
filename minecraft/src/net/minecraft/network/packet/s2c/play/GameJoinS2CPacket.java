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
	private boolean field_24618;
	private boolean field_24619;
	private boolean reducedDebugInfo;
	private boolean showsDeathScreen;

	public GameJoinS2CPacket() {
	}

	public GameJoinS2CPacket(
		int playerEntityId,
		GameMode gameMode,
		long seed,
		boolean hardcore,
		DimensionType dimensionType,
		int maxPlayers,
		int i,
		boolean bl,
		boolean bl2,
		boolean bl3,
		boolean bl4
	) {
		this.playerEntityId = playerEntityId;
		this.dimension = dimensionType;
		this.seed = seed;
		this.gameMode = gameMode;
		this.maxPlayers = maxPlayers;
		this.hardcore = hardcore;
		this.chunkLoadDistance = i;
		this.field_24618 = bl;
		this.field_24619 = bl2;
		this.reducedDebugInfo = bl3;
		this.showsDeathScreen = bl4;
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
		this.field_24618 = buf.readBoolean();
		this.field_24619 = buf.readBoolean();
		this.reducedDebugInfo = buf.readBoolean();
		this.showsDeathScreen = buf.readBoolean();
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
		buf.writeBoolean(this.field_24618);
		buf.writeBoolean(this.field_24619);
		buf.writeBoolean(this.reducedDebugInfo);
		buf.writeBoolean(this.showsDeathScreen);
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
	public boolean method_28118() {
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
		return this.field_24618;
	}

	@Environment(EnvType.CLIENT)
	public boolean showsDeathScreen() {
		return this.field_24619;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_28119() {
		return this.reducedDebugInfo;
	}

	@Environment(EnvType.CLIENT)
	public boolean isHardcore() {
		return this.showsDeathScreen;
	}
}
