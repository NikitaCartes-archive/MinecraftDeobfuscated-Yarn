package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private String[] text;

	public UpdateSignC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateSignC2SPacket(BlockPos blockPos, Text text, Text text2, Text text3, Text text4) {
		this.pos = blockPos;
		this.text = new String[]{text.getString(), text2.getString(), text3.getString(), text4.getString()};
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

	public void method_12509(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onSignUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public String[] getText() {
		return this.text;
	}
}
