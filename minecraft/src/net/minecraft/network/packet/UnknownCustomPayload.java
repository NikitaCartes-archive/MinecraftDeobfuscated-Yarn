package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record UnknownCustomPayload(Identifier id) implements CustomPayload {
	public static <T extends PacketByteBuf> PacketCodec<T, UnknownCustomPayload> createCodec(Identifier id, int maxBytes) {
		return CustomPayload.codecOf((value, buf) -> {
		}, buf -> {
			int j = buf.readableBytes();
			if (j >= 0 && j <= maxBytes) {
				buf.skipBytes(j);
				return new UnknownCustomPayload(id);
			} else {
				throw new IllegalArgumentException("Payload may not be larger than " + maxBytes + " bytes");
			}
		});
	}

	@Override
	public CustomPayload.Id<UnknownCustomPayload> getId() {
		return new CustomPayload.Id<>(this.id);
	}
}
