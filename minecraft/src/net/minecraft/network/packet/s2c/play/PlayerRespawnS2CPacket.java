package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
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
	private DimensionType field_25322;
	private RegistryKey<World> dimension;
	private long sha256Seed;
	private GameMode gameMode;
	@Nullable
	private GameMode previousGameMode;
	private boolean debugWorld;
	private boolean flatWorld;
	private boolean keepPlayerAttributes;

	public PlayerRespawnS2CPacket() {
	}

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

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.field_25322 = (DimensionType)buf.decode(DimensionType.REGISTRY_CODEC).get();
		this.dimension = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
		this.sha256Seed = buf.readLong();
		this.gameMode = GameMode.byId(buf.readUnsignedByte());
		this.previousGameMode = GameMode.getOrNull(buf.readByte());
		this.debugWorld = buf.readBoolean();
		this.flatWorld = buf.readBoolean();
		this.keepPlayerAttributes = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.encode(DimensionType.REGISTRY_CODEC, () -> this.field_25322);
		buf.writeIdentifier(this.dimension.getValue());
		buf.writeLong(this.sha256Seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
		buf.writeBoolean(this.keepPlayerAttributes);
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
