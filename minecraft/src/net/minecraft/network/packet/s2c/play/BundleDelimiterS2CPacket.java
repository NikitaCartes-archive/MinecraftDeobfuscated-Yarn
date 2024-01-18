package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class BundleDelimiterS2CPacket extends BundleSplitterPacket<ClientPlayPacketListener> {
	@Override
	public PacketType<BundleDelimiterS2CPacket> getPacketId() {
		return PlayPackets.BUNDLE_DELIMITER;
	}
}
