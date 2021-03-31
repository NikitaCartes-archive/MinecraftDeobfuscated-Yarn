package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket implements Packet<ServerPlayPacketListener> {
	private static final int MAX_LINE_LENGTH = 384;
	private final BlockPos pos;
	private final String[] text;

	public UpdateSignC2SPacket(BlockPos pos, String line1, String line2, String line3, String line4) {
		this.pos = pos;
		this.text = new String[]{line1, line2, line3, line4};
	}

	public UpdateSignC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.text = new String[4];

		for (int i = 0; i < 4; i++) {
			this.text[i] = buf.readString(384);
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);

		for (int i = 0; i < 4; i++) {
			buf.writeString(this.text[i]);
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
