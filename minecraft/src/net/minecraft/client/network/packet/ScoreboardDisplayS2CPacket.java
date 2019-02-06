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
	private int location;
	private String name;

	public ScoreboardDisplayS2CPacket() {
	}

	public ScoreboardDisplayS2CPacket(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		this.location = i;
		if (scoreboardObjective == null) {
			this.name = "";
		} else {
			this.name = scoreboardObjective.getName();
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.location = packetByteBuf.readByte();
		this.name = packetByteBuf.readString(16);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.location);
		packetByteBuf.writeString(this.name);
	}

	public void method_11805(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11159(this);
	}

	@Environment(EnvType.CLIENT)
	public int getLocation() {
		return this.location;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11804() {
		return Objects.equals(this.name, "") ? null : this.name;
	}
}
