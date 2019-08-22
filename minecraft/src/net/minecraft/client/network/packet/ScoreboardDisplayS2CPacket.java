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

	public ScoreboardDisplayS2CPacket(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		this.slot = i;
		if (scoreboardObjective == null) {
			this.name = "";
		} else {
			this.name = scoreboardObjective.getName();
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.slot = packetByteBuf.readByte();
		this.name = packetByteBuf.readString(16);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.slot);
		packetByteBuf.writeString(this.name);
	}

	public void method_11805(ClientPlayPacketListener clientPlayPacketListener) {
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
