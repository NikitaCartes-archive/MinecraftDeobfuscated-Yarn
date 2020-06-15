package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private int playerEntityId;
	private long sha256Seed;
	private boolean hardcore;
	private GameMode gameMode;
	private GameMode field_25713;
	private Set<RegistryKey<World>> field_25320;
	private RegistryTracker.Modifiable dimensionTracker;
	private RegistryKey<DimensionType> field_25321;
	private RegistryKey<World> dimensionId;
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
		GameMode gameMode2,
		long sha256Seed,
		boolean hardcore,
		Set<RegistryKey<World>> set,
		RegistryTracker.Modifiable modifiable,
		RegistryKey<DimensionType> registryKey,
		RegistryKey<World> registryKey2,
		int maxPlayers,
		int chunkLoadDistance,
		boolean reducedDebugInfo,
		boolean showDeathScreen,
		boolean debugWorld,
		boolean flatWorld
	) {
		this.playerEntityId = playerEntityId;
		this.field_25320 = set;
		this.dimensionTracker = modifiable;
		this.field_25321 = registryKey;
		this.dimensionId = registryKey2;
		this.sha256Seed = sha256Seed;
		this.gameMode = gameMode;
		this.field_25713 = gameMode2;
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
		this.field_25713 = GameMode.byId(buf.readUnsignedByte());
		int j = buf.readVarInt();
		this.field_25320 = Sets.<RegistryKey<World>>newHashSet();

		for (int k = 0; k < j; k++) {
			this.field_25320.add(RegistryKey.of(Registry.DIMENSION, buf.readIdentifier()));
		}

		this.dimensionTracker = buf.decode(RegistryTracker.Modifiable.CODEC);
		this.field_25321 = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, buf.readIdentifier());
		this.dimensionId = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
		this.sha256Seed = buf.readLong();
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
		buf.writeByte(this.field_25713.getId());
		buf.writeVarInt(this.field_25320.size());

		for (RegistryKey<World> registryKey : this.field_25320) {
			buf.writeIdentifier(registryKey.getValue());
		}

		buf.encode(RegistryTracker.Modifiable.CODEC, this.dimensionTracker);
		buf.writeIdentifier(this.field_25321.getValue());
		buf.writeIdentifier(this.dimensionId.getValue());
		buf.writeLong(this.sha256Seed);
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
	public long getSha256Seed() {
		return this.sha256Seed;
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
	public GameMode method_30116() {
		return this.field_25713;
	}

	@Environment(EnvType.CLIENT)
	public Set<RegistryKey<World>> method_29443() {
		return this.field_25320;
	}

	@Environment(EnvType.CLIENT)
	public RegistryTracker getDimension() {
		return this.dimensionTracker;
	}

	@Environment(EnvType.CLIENT)
	public RegistryKey<DimensionType> method_29444() {
		return this.field_25321;
	}

	@Environment(EnvType.CLIENT)
	public RegistryKey<World> getDimensionId() {
		return this.dimensionId;
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
