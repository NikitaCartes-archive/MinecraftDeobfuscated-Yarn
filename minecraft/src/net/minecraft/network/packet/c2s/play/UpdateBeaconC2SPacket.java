package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdateBeaconC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int primaryEffectId;
	private final int secondaryEffectId;

	public UpdateBeaconC2SPacket(int primaryEffectId, int secondaryEffectId) {
		this.primaryEffectId = primaryEffectId;
		this.secondaryEffectId = secondaryEffectId;
	}

	public UpdateBeaconC2SPacket(PacketByteBuf buf) {
		this.primaryEffectId = buf.readVarInt();
		this.secondaryEffectId = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.primaryEffectId);
		buf.writeVarInt(this.secondaryEffectId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateBeacon(this);
	}

	public int getPrimaryEffectId() {
		return this.primaryEffectId;
	}

	public int getSecondaryEffectId() {
		return this.secondaryEffectId;
	}
}
