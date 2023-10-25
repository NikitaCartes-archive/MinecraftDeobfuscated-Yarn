package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.tick.TickManager;

public record TickStepS2CPacket(int tickSteps) implements Packet<ClientPlayPacketListener> {
	public TickStepS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	public static TickStepS2CPacket create(TickManager tickManager) {
		return new TickStepS2CPacket(tickManager.getStepTicks());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.tickSteps);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTickStep(this);
	}
}
