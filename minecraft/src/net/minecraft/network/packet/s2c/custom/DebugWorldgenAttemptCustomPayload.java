package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugWorldgenAttemptCustomPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugWorldgenAttemptCustomPayload> CODEC = CustomPayload.codecOf(
		DebugWorldgenAttemptCustomPayload::write, DebugWorldgenAttemptCustomPayload::new
	);
	public static final CustomPayload.Id<DebugWorldgenAttemptCustomPayload> ID = CustomPayload.id("debug/worldgen_attempt");

	private DebugWorldgenAttemptCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeFloat(this.scale);
		buf.writeFloat(this.red);
		buf.writeFloat(this.green);
		buf.writeFloat(this.blue);
		buf.writeFloat(this.alpha);
	}

	@Override
	public CustomPayload.Id<DebugWorldgenAttemptCustomPayload> getId() {
		return ID;
	}
}
