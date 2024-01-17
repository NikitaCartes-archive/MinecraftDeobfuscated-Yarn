package net.minecraft.network.packet.c2s.common;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.UnknownCustomPayload;

public record CustomPayloadC2SPacket(CustomPayload payload) implements Packet<ServerCommonPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 32767;
	public static final PacketCodec<PacketByteBuf, CustomPayloadC2SPacket> CODEC = CustomPayload.<PacketByteBuf>createCodec(
			identifier -> UnknownCustomPayload.createCodec(identifier, 32767), List.of(new CustomPayload.Type<>(BrandCustomPayload.KEY, BrandCustomPayload.CODEC))
		)
		.xmap(CustomPayloadC2SPacket::new, CustomPayloadC2SPacket::payload);

	@Override
	public PacketIdentifier<CustomPayloadC2SPacket> getPacketId() {
		return CommonPackets.CUSTOM_PAYLOAD_C2S;
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onCustomPayload(this);
	}
}
