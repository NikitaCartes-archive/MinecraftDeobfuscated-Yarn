package net.minecraft.network.packet.s2c.custom;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DebugPathCustomPayload(int entityId, Path path, float maxNodeDistance) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/path");

	public DebugPathCustomPayload(PacketByteBuf buf) {
		this(buf.readInt(), Path.fromBuf(buf), buf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.entityId);
		this.path.toBuf(buf);
		buf.writeFloat(this.maxNodeDistance);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
