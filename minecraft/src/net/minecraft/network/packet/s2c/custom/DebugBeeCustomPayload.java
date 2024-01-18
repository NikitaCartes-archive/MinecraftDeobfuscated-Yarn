package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record DebugBeeCustomPayload(DebugBeeCustomPayload.Bee beeInfo) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugBeeCustomPayload> CODEC = CustomPayload.codecOf(DebugBeeCustomPayload::write, DebugBeeCustomPayload::new);
	public static final CustomPayload.Id<DebugBeeCustomPayload> ID = CustomPayload.id("debug/bee");

	private DebugBeeCustomPayload(PacketByteBuf buf) {
		this(new DebugBeeCustomPayload.Bee(buf));
	}

	private void write(PacketByteBuf buf) {
		this.beeInfo.write(buf);
	}

	@Override
	public CustomPayload.Id<DebugBeeCustomPayload> getId() {
		return ID;
	}

	public static record Bee(
		UUID uuid,
		int entityId,
		Vec3d pos,
		@Nullable Path path,
		@Nullable BlockPos hivePos,
		@Nullable BlockPos flowerPos,
		int travelTicks,
		Set<String> goals,
		List<BlockPos> disallowedHives
	) {
		public Bee(PacketByteBuf buf) {
			this(
				buf.readUuid(),
				buf.readInt(),
				buf.readVec3d(),
				buf.readNullable(Path::fromBuf),
				buf.readNullable(BlockPos.PACKET_CODEC),
				buf.readNullable(BlockPos.PACKET_CODEC),
				buf.readInt(),
				buf.readCollection(HashSet::new, PacketByteBuf::readString),
				buf.readList(BlockPos.PACKET_CODEC)
			);
		}

		public void write(PacketByteBuf buf) {
			buf.writeUuid(this.uuid);
			buf.writeInt(this.entityId);
			buf.writeVec3d(this.pos);
			buf.writeNullable(this.path, (bufx, path) -> path.toBuf(bufx));
			buf.writeNullable(this.hivePos, BlockPos.PACKET_CODEC);
			buf.writeNullable(this.flowerPos, BlockPos.PACKET_CODEC);
			buf.writeInt(this.travelTicks);
			buf.writeCollection(this.goals, PacketByteBuf::writeString);
			buf.writeCollection(this.disallowedHives, BlockPos.PACKET_CODEC);
		}

		public boolean isHiveAt(BlockPos pos) {
			return Objects.equals(pos, this.hivePos);
		}

		public String getName() {
			return NameGenerator.name(this.uuid);
		}

		public String toString() {
			return this.getName();
		}
	}
}
