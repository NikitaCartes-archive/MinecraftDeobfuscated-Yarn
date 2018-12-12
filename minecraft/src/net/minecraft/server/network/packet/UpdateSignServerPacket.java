package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class UpdateSignServerPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private String[] text;

	public UpdateSignServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateSignServerPacket(
		BlockPos blockPos, TextComponent textComponent, TextComponent textComponent2, TextComponent textComponent3, TextComponent textComponent4
	) {
		this.pos = blockPos;
		this.text = new String[]{textComponent.getString(), textComponent2.getString(), textComponent3.getString(), textComponent4.getString()};
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.text = new String[4];

		for (int i = 0; i < 4; i++) {
			this.text[i] = packetByteBuf.readString(384);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);

		for (int i = 0; i < 4; i++) {
			packetByteBuf.writeString(this.text[i]);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSignUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public String[] getText() {
		return this.text;
	}
}
