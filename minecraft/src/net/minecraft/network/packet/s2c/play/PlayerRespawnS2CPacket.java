package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;

public class PlayerRespawnS2CPacket implements Packet<ClientPlayPacketListener> {
	private DimensionType dimension;
	private long sha256Seed;
	private GameMode gameMode;
	private boolean field_24620;
	private boolean field_24621;
	private boolean field_24451;

	public PlayerRespawnS2CPacket() {
	}

	public PlayerRespawnS2CPacket(DimensionType dimension, long sha256Seed, GameMode gameMode, boolean bl, boolean bl2, boolean bl3) {
		this.dimension = dimension;
		this.sha256Seed = sha256Seed;
		this.gameMode = gameMode;
		this.field_24620 = bl;
		this.field_24621 = bl2;
		this.field_24451 = bl3;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerRespawn(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.dimension = DimensionType.byRawId(buf.readInt());
		this.sha256Seed = buf.readLong();
		this.gameMode = GameMode.byId(buf.readUnsignedByte());
		this.field_24620 = buf.readBoolean();
		this.field_24621 = buf.readBoolean();
		this.field_24451 = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeInt(this.dimension.getRawId());
		buf.writeLong(this.sha256Seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeBoolean(this.field_24620);
		buf.writeBoolean(this.field_24621);
		buf.writeBoolean(this.field_24451);
	}

	@Environment(EnvType.CLIENT)
	public DimensionType getDimension() {
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
	public boolean method_28120() {
		return this.field_24620;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_28121() {
		return this.field_24621;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_27904() {
		return this.field_24451;
	}
}
