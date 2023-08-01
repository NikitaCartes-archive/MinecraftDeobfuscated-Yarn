package net.minecraft.network.packet.c2s.common;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.s2c.custom.BrandCustomPayload;
import net.minecraft.util.Identifier;

public record CustomPayloadC2SPacket(CustomPayload payload) implements Packet<ServerCommonPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 32767;
	private static final Map<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>> ID_TO_READER = ImmutableMap.<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>>builder()
		.put(BrandCustomPayload.ID, BrandCustomPayload::new)
		.build();

	public CustomPayloadC2SPacket(PacketByteBuf buf) {
		this(readPayload(buf.readIdentifier(), buf));
	}

	private static CustomPayload readPayload(Identifier id, PacketByteBuf buf) {
		PacketByteBuf.PacketReader<? extends CustomPayload> packetReader = (PacketByteBuf.PacketReader<? extends CustomPayload>)ID_TO_READER.get(id);
		return (CustomPayload)(packetReader != null ? (CustomPayload)packetReader.apply(buf) : readUnknownPayload(id, buf));
	}

	private static UnknownCustomPayload readUnknownPayload(Identifier id, PacketByteBuf buf) {
		int i = buf.readableBytes();
		if (i >= 0 && i <= 32767) {
			buf.skipBytes(i);
			return new UnknownCustomPayload(id);
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.payload.id());
		this.payload.write(buf);
	}

	public void apply(ServerCommonPacketListener serverCommonPacketListener) {
		serverCommonPacketListener.onCustomPayload(this);
	}
}
