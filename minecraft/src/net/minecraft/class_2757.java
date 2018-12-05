package net.minecraft;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.PacketByteBuf;

public class class_2757 implements Packet<ClientPlayPacketListener> {
	private String field_12610 = "";
	@Nullable
	private String field_12613;
	private int field_12611;
	private ServerScoreboard.class_2996 field_12612;

	public class_2757() {
	}

	public class_2757(ServerScoreboard.class_2996 arg, @Nullable String string, String string2, int i) {
		if (arg != ServerScoreboard.class_2996.field_13430 && string == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.field_12610 = string2;
			this.field_12613 = string;
			this.field_12611 = i;
			this.field_12612 = arg;
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12610 = packetByteBuf.readString(40);
		this.field_12612 = packetByteBuf.readEnumConstant(ServerScoreboard.class_2996.class);
		String string = packetByteBuf.readString(16);
		this.field_12613 = Objects.equals(string, "") ? null : string;
		if (this.field_12612 != ServerScoreboard.class_2996.field_13430) {
			this.field_12611 = packetByteBuf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.field_12610);
		packetByteBuf.writeEnumConstant(this.field_12612);
		packetByteBuf.writeString(this.field_12613 == null ? "" : this.field_12613);
		if (this.field_12612 != ServerScoreboard.class_2996.field_13430) {
			packetByteBuf.writeVarInt(this.field_12611);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11118(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_11862() {
		return this.field_12610;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11864() {
		return this.field_12613;
	}

	@Environment(EnvType.CLIENT)
	public int method_11865() {
		return this.field_12611;
	}

	@Environment(EnvType.CLIENT)
	public ServerScoreboard.class_2996 method_11863() {
		return this.field_12612;
	}
}
