package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private final DimensionType field_25322;
	private final RegistryKey<World> dimension;
	private final long sha256Seed;
	private final GameMode gameMode;
	@Nullable
	private final GameMode previousGameMode;
	private final boolean debugWorld;
	private final boolean flatWorld;
	private final boolean keepPlayerAttributes;

	public PlayerRespawnS2CPacket(
		DimensionType dimensionType,
		RegistryKey<World> registryKey,
		long l,
		GameMode gameMode,
		@Nullable GameMode previousGameMode,
		boolean bl,
		boolean bl2,
		boolean bl3
	) {
		this.field_25322 = dimensionType;
		this.dimension = registryKey;
		this.sha256Seed = l;
		this.gameMode = gameMode;
		this.previousGameMode = previousGameMode;
		this.debugWorld = bl;
		this.flatWorld = bl2;
		this.keepPlayerAttributes = bl3;
	}

	public PlayerRespawnS2CPacket(PacketByteBuf packetByteBuf) {
		this.field_25322 = (DimensionType)packetByteBuf.decode(DimensionType.REGISTRY_CODEC).get();
		this.dimension = RegistryKey.of(Registry.DIMENSION, packetByteBuf.readIdentifier());
		this.sha256Seed = packetByteBuf.readLong();
		this.gameMode = GameMode.byId(packetByteBuf.readUnsignedByte());
		this.previousGameMode = GameMode.getOrNull(packetByteBuf.readByte());
		this.debugWorld = packetByteBuf.readBoolean();
		this.flatWorld = packetByteBuf.readBoolean();
		this.keepPlayerAttributes = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.encode(DimensionType.REGISTRY_CODEC, () -> this.field_25322);
		buf.writeIdentifier(this.dimension.getValue());
		buf.writeLong(this.sha256Seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
		buf.writeBoolean(this.keepPlayerAttributes);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Environment(EnvType.CLIENT)
	public DimensionType method_29445() {
		return this.field_25322;
	}

	@Environment(EnvType.CLIENT)
	public RegistryKey<World> getDimension() {
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

	@Nullable
	@Environment(EnvType.CLIENT)
	public GameMode getPreviousGameMode() {
		return this.previousGameMode;
	}

	@Environment(EnvType.CLIENT)
	public boolean isDebugWorld() {
		return this.debugWorld;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFlatWorld() {
		return this.flatWorld;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldKeepPlayerAttributes() {
		return this.keepPlayerAttributes;
	}
}
