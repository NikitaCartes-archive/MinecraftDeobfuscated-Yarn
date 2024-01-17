package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;

public class BundleDelimiterS2CPacket extends BundleSplitterPacket<ClientPlayPacketListener> {
	@Override
	public PacketIdentifier<BundleDelimiterS2CPacket> getPacketId() {
		return PlayPackets.BUNDLE_DELIMITER;
	}
}
