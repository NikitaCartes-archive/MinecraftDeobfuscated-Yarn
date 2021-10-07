package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public record SimulationDistanceS2CPacket() implements Packet<ClientPlayPacketListener> {
	private final int simulationDistance;

	public SimulationDistanceS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	public SimulationDistanceS2CPacket(int i) {
		this.simulationDistance = i;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.simulationDistance);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSimulationDistance(this);
	}
}
