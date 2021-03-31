package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class SignEditorOpenS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;

	public SignEditorOpenS2CPacket(BlockPos pos) {
		this.pos = pos;
	}

	public SignEditorOpenS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSignEditorOpen(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
