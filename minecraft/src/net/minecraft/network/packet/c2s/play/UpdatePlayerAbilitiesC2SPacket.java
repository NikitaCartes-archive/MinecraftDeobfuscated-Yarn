package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdatePlayerAbilitiesC2SPacket implements Packet<ServerPlayPacketListener> {
	private boolean flying;

	public UpdatePlayerAbilitiesC2SPacket() {
	}

	public UpdatePlayerAbilitiesC2SPacket(PlayerAbilities abilities) {
		this.flying = abilities.flying;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		byte b = buf.readByte();
		this.flying = (b & 2) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		byte b = 0;
		if (this.flying) {
			b = (byte)(b | 2);
		}

		buf.writeByte(b);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerAbilities(this);
	}

	public boolean isFlying() {
		return this.flying;
	}
}
