package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SimulationDistanceS2CPacket(int simulationDistance) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SimulationDistanceS2CPacket> CODEC = Packet.createCodec(
		SimulationDistanceS2CPacket::write, SimulationDistanceS2CPacket::new
	);

	private SimulationDistanceS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.simulationDistance);
	}

	@Override
	public PacketType<SimulationDistanceS2CPacket> getPacketId() {
		return PlayPackets.SET_SIMULATION_DISTANCE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSimulationDistance(this);
	}
}
