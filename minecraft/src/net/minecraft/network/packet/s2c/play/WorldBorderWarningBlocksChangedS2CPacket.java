package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderWarningBlocksChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldBorderWarningBlocksChangedS2CPacket> CODEC = Packet.createCodec(
		WorldBorderWarningBlocksChangedS2CPacket::write, WorldBorderWarningBlocksChangedS2CPacket::new
	);
	private final int warningBlocks;

	public WorldBorderWarningBlocksChangedS2CPacket(WorldBorder worldBorder) {
		this.warningBlocks = worldBorder.getWarningBlocks();
	}

	private WorldBorderWarningBlocksChangedS2CPacket(PacketByteBuf buf) {
		this.warningBlocks = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.warningBlocks);
	}

	@Override
	public PacketType<WorldBorderWarningBlocksChangedS2CPacket> getPacketId() {
		return PlayPackets.SET_BORDER_WARNING_DISTANCE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderWarningBlocksChanged(this);
	}

	public int getWarningBlocks() {
		return this.warningBlocks;
	}
}
