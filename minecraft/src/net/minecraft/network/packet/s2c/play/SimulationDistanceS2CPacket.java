package net.minecraft.network.packet.s2c.play;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public final class SimulationDistanceS2CPacket extends Record implements Packet<ClientPlayPacketListener> {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",SimulationDistanceS2CPacket,"simulationDistance",SimulationDistanceS2CPacket::simulationDistance>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",SimulationDistanceS2CPacket,"simulationDistance",SimulationDistanceS2CPacket::simulationDistance>(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",SimulationDistanceS2CPacket,"simulationDistance",SimulationDistanceS2CPacket::simulationDistance>(this, object);
	}

	public int simulationDistance() {
		return this.simulationDistance;
	}
}
