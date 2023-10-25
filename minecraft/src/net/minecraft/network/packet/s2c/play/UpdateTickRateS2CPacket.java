package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.tick.TickManager;

public record UpdateTickRateS2CPacket(float tickRate, boolean isFrozen) implements Packet<ClientPlayPacketListener> {
	public UpdateTickRateS2CPacket(PacketByteBuf buf) {
		this(buf.readFloat(), buf.readBoolean());
	}

	public static UpdateTickRateS2CPacket create(TickManager tickManager) {
		return new UpdateTickRateS2CPacket(tickManager.getTickRate(), tickManager.isFrozen());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.tickRate);
		buf.writeBoolean(this.isFrozen);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUpdateTickRate(this);
	}
}
