package net.minecraft;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record class_9814(Map<String, String> details) implements Packet<ClientCommonPacketListener> {
	private static final int field_52186 = 128;
	private static final int field_52187 = 4096;
	private static final int field_52188 = 32;
	private static final PacketCodec<ByteBuf, Map<String, String>> field_52189 = PacketCodecs.map(
		HashMap::new, PacketCodecs.string(128), PacketCodecs.string(4096), 32
	);
	public static final PacketCodec<ByteBuf, class_9814> field_52185 = PacketCodec.tuple(field_52189, class_9814::details, class_9814::new);

	@Override
	public PacketType<class_9814> getPacketId() {
		return CommonPackets.CUSTOM_REPORT_DETAILS;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.method_60883(this);
	}
}
