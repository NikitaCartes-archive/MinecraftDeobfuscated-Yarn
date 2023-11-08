package net.minecraft.network.packet.s2c.custom;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;

public record DebugBreezeCustomPayload(DebugBreezeCustomPayload.BreezeInfo breezeInfo) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/breeze");

	public DebugBreezeCustomPayload(PacketByteBuf buf) {
		this(new DebugBreezeCustomPayload.BreezeInfo(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.breezeInfo.write(buf);
	}

	@Override
	public Identifier id() {
		return ID;
	}

	public static record BreezeInfo(UUID uuid, int id, Integer attackTarget, BlockPos jumpTarget) {
		public BreezeInfo(PacketByteBuf buf) {
			this(buf.readUuid(), buf.readInt(), buf.readNullable(PacketByteBuf::readInt), buf.readNullable(PacketByteBuf::readBlockPos));
		}

		public void write(PacketByteBuf buf) {
			buf.writeUuid(this.uuid);
			buf.writeInt(this.id);
			buf.writeNullable(this.attackTarget, PacketByteBuf::writeInt);
			buf.writeNullable(this.jumpTarget, PacketByteBuf::writeBlockPos);
		}

		public String getName() {
			return NameGenerator.name(this.uuid);
		}

		public String toString() {
			return this.getName();
		}
	}
}
