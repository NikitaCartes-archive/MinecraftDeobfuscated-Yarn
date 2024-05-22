package net.minecraft.network.packet.s2c.common;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record CustomReportDetailsS2CPacket(Map<String, String> details) implements Packet<ClientCommonPacketListener> {
	private static final int MAX_KEY_LENGTH = 128;
	private static final int MAX_VALUE_LENGTH = 4096;
	private static final int MAX_DETAILS_SIZE = 32;
	private static final PacketCodec<ByteBuf, Map<String, String>> DETAILS_CODEC = PacketCodecs.map(
		HashMap::new, PacketCodecs.string(128), PacketCodecs.string(4096), 32
	);
	public static final PacketCodec<ByteBuf, CustomReportDetailsS2CPacket> CODEC = PacketCodec.tuple(
		DETAILS_CODEC, CustomReportDetailsS2CPacket::details, CustomReportDetailsS2CPacket::new
	);

	@Override
	public PacketType<CustomReportDetailsS2CPacket> getPacketId() {
		return CommonPackets.CUSTOM_REPORT_DETAILS;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onCustomReportDetails(this);
	}
}
