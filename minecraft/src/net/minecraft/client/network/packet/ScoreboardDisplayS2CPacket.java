package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardDisplayS2CPacket implements Packet<ClientPlayPacketListener> {
	private int slot;
	private String name;

	public ScoreboardDisplayS2CPacket() {
	}

	public ScoreboardDisplayS2CPacket(int slot, @Nullable ScoreboardObjective scoreboardObjective) {
		this.slot = slot;
		if (scoreboardObjective == null) {
			this.name = "";
		} else {
			this.name = scoreboardObjective.getName();
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.slot = buf.readByte();
		this.name = buf.readString(16);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.slot);
		buf.writeString(this.name);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardDisplay(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return this.slot;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getName() {
		return Objects.equals(this.name, "") ? null : this.name;
	}
}
