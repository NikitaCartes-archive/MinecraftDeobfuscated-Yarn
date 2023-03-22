package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class SignEditorOpenS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final boolean front;

	public SignEditorOpenS2CPacket(BlockPos pos, boolean front) {
		this.pos = pos;
		this.front = front;
	}

	public SignEditorOpenS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.front = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeBoolean(this.front);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSignEditorOpen(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public boolean isFront() {
		return this.front;
	}
}
