package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public class UpdateSignC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, UpdateSignC2SPacket> CODEC = Packet.createCodec(UpdateSignC2SPacket::write, UpdateSignC2SPacket::new);
	private static final int MAX_LINE_LENGTH = 384;
	private final BlockPos pos;
	private final String[] text;
	private final boolean front;

	public UpdateSignC2SPacket(BlockPos pos, boolean front, String line1, String line2, String line3, String line4) {
		this.pos = pos;
		this.front = front;
		this.text = new String[]{line1, line2, line3, line4};
	}

	private UpdateSignC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.front = buf.readBoolean();
		this.text = new String[4];

		for (int i = 0; i < 4; i++) {
			this.text[i] = buf.readString(384);
		}
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeBoolean(this.front);

		for (int i = 0; i < 4; i++) {
			buf.writeString(this.text[i]);
		}
	}

	@Override
	public PacketType<UpdateSignC2SPacket> getPacketId() {
		return PlayPackets.SIGN_UPDATE;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateSign(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public boolean isFront() {
		return this.front;
	}

	public String[] getText() {
		return this.text;
	}
}
