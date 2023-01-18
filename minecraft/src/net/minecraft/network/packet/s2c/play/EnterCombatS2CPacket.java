package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class EnterCombatS2CPacket implements Packet<ClientPlayPacketListener> {
	public EnterCombatS2CPacket() {
	}

	public EnterCombatS2CPacket(PacketByteBuf buf) {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEnterCombat(this);
	}
}
