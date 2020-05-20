package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private Identifier dimension;
	private long sha256Seed;
	private GameMode gameMode;
	private boolean debugWorld;
	private boolean flatWorld;
	private boolean keepPlayerAttributes;

	public PlayerRespawnS2CPacket() {
	}

	public PlayerRespawnS2CPacket(Identifier identifier, long sha256Seed, GameMode gameMode, boolean debugWorld, boolean flatWorld, boolean keepPlayerAttributes) {
		this.dimension = identifier;
		this.sha256Seed = sha256Seed;
		this.gameMode = gameMode;
		this.debugWorld = debugWorld;
		this.flatWorld = flatWorld;
		this.keepPlayerAttributes = keepPlayerAttributes;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.dimension = buf.readIdentifier();
		this.sha256Seed = buf.readLong();
		this.gameMode = GameMode.byId(buf.readUnsignedByte());
		this.debugWorld = buf.readBoolean();
		this.flatWorld = buf.readBoolean();
		this.keepPlayerAttributes = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(this.dimension);
		buf.writeLong(this.sha256Seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
		buf.writeBoolean(this.keepPlayerAttributes);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getDimension() {
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
