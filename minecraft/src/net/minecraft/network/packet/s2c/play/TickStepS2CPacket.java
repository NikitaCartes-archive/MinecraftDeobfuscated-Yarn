package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.tick.TickManager;

public record TickStepS2CPacket(int tickSteps) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, TickStepS2CPacket> CODEC = Packet.createCodec(TickStepS2CPacket::write, TickStepS2CPacket::new);

	private TickStepS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt());
	}

	public static TickStepS2CPacket create(TickManager tickManager) {
		return new TickStepS2CPacket(tickManager.getStepTicks());
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.tickSteps);
	}

	@Override
	public PacketType<TickStepS2CPacket> getPacketId() {
		return PlayPackets.TICKING_STEP;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTickStep(this);
	}
}
