package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record SimulationDistanceS2CPacket(int simulationDistance) implements Packet<ClientPlayPacketListener> {
	public SimulationDistanceS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.simulationDistance);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSimulationDistance(this);
	}
}
