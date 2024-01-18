package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderWarningTimeChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldBorderWarningTimeChangedS2CPacket> CODEC = Packet.createCodec(
		WorldBorderWarningTimeChangedS2CPacket::write, WorldBorderWarningTimeChangedS2CPacket::new
	);
	private final int warningTime;

	public WorldBorderWarningTimeChangedS2CPacket(WorldBorder worldBorder) {
		this.warningTime = worldBorder.getWarningTime();
	}

	private WorldBorderWarningTimeChangedS2CPacket(PacketByteBuf buf) {
		this.warningTime = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.warningTime);
	}

	@Override
	public PacketType<WorldBorderWarningTimeChangedS2CPacket> getPacketId() {
		return PlayPackets.SET_BORDER_WARNING_DELAY;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderWarningTimeChanged(this);
	}

	public int getWarningTime() {
		return this.warningTime;
	}
}
