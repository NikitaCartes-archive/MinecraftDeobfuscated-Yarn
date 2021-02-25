package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdatePlayerAbilitiesC2SPacket implements Packet<ServerPlayPacketListener> {
	private final boolean flying;

	public UpdatePlayerAbilitiesC2SPacket(PlayerAbilities abilities) {
		this.flying = abilities.flying;
	}

	public UpdatePlayerAbilitiesC2SPacket(PacketByteBuf buf) {
		byte b = buf.readByte();
		this.flying = (b & 2) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) {
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
