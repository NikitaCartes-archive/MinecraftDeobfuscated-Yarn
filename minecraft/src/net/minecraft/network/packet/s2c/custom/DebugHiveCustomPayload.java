package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugHiveCustomPayload(DebugHiveCustomPayload.HiveInfo hiveInfo) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugHiveCustomPayload> CODEC = CustomPayload.codecOf(
		DebugHiveCustomPayload::write, DebugHiveCustomPayload::new
	);
	public static final CustomPayload.Id<DebugHiveCustomPayload> ID = CustomPayload.id("debug/hive");

	private DebugHiveCustomPayload(PacketByteBuf buf) {
		this(new DebugHiveCustomPayload.HiveInfo(buf));
	}

	private void write(PacketByteBuf buf) {
		this.hiveInfo.write(buf);
	}

	@Override
	public CustomPayload.Id<DebugHiveCustomPayload> getId() {
		return ID;
	}

	public static record HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
		public HiveInfo(PacketByteBuf buf) {
			this(buf.readBlockPos(), buf.readString(), buf.readInt(), buf.readInt(), buf.readBoolean());
		}

		public void write(PacketByteBuf buf) {
			buf.writeBlockPos(this.pos);
			buf.writeString(this.hiveType);
			buf.writeInt(this.occupantCount);
			buf.writeInt(this.honeyLevel);
			buf.writeBoolean(this.sedated);
		}
	}
}
