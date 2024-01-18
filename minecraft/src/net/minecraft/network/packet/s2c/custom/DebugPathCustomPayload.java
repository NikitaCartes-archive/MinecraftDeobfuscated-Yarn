package net.minecraft.network.packet.s2c.custom;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DebugPathCustomPayload(int entityId, Path path, float maxNodeDistance) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugPathCustomPayload> CODEC = CustomPayload.codecOf(
		DebugPathCustomPayload::write, DebugPathCustomPayload::new
	);
	public static final CustomPayload.Id<DebugPathCustomPayload> ID = CustomPayload.id("debug/path");

	private DebugPathCustomPayload(PacketByteBuf buf) {
		this(buf.readInt(), Path.fromBuf(buf), buf.readFloat());
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.entityId);
		this.path.toBuf(buf);
		buf.writeFloat(this.maxNodeDistance);
	}

	@Override
	public CustomPayload.Id<DebugPathCustomPayload> getId() {
		return ID;
	}
}
