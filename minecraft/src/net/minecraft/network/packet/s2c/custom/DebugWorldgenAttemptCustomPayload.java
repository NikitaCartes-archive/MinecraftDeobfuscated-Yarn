package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugWorldgenAttemptCustomPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/worldgen_attempt");

	public DebugWorldgenAttemptCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeFloat(this.scale);
		buf.writeFloat(this.red);
		buf.writeFloat(this.green);
		buf.writeFloat(this.blue);
		buf.writeFloat(this.alpha);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
