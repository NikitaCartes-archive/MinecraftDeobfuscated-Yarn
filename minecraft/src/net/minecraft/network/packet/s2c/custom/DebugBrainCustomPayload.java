package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record DebugBrainCustomPayload(DebugBrainCustomPayload.Brain brainDump) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/brain");

	public DebugBrainCustomPayload(PacketByteBuf buf) {
		this(new DebugBrainCustomPayload.Brain(buf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.brainDump.write(buf);
	}

	@Override
	public Identifier id() {
		return ID;
	}

	public static record Brain(
		UUID uuid,
		int entityId,
		String name,
		String profession,
		int xp,
		float health,
		float maxHealth,
		Vec3d pos,
		String inventory,
		@Nullable Path path,
		boolean wantsGolem,
		int angerLevel,
		List<String> possibleActivities,
		List<String> runningTasks,
		List<String> memories,
		List<String> gossips,
		Set<BlockPos> pois,
		Set<BlockPos> potentialPois
	) {
		public Brain(PacketByteBuf buf) {
			this(
				buf.readUuid(),
				buf.readInt(),
				buf.readString(),
				buf.readString(),
				buf.readInt(),
				buf.readFloat(),
				buf.readFloat(),
				buf.readVec3d(),
				buf.readString(),
				buf.readNullable(Path::fromBuf),
				buf.readBoolean(),
				buf.readInt(),
				buf.readList(PacketByteBuf::readString),
				buf.readList(PacketByteBuf::readString),
				buf.readList(PacketByteBuf::readString),
				buf.readList(PacketByteBuf::readString),
				buf.readCollection(HashSet::new, PacketByteBuf::readBlockPos),
				buf.readCollection(HashSet::new, PacketByteBuf::readBlockPos)
			);
		}

		public void write(PacketByteBuf buf) {
			buf.writeUuid(this.uuid);
			buf.writeInt(this.entityId);
			buf.writeString(this.name);
			buf.writeString(this.profession);
			buf.writeInt(this.xp);
			buf.writeFloat(this.health);
			buf.writeFloat(this.maxHealth);
			buf.writeVec3d(this.pos);
			buf.writeString(this.inventory);
			buf.writeNullable(this.path, (bufx, path) -> path.toBuf(bufx));
			buf.writeBoolean(this.wantsGolem);
			buf.writeInt(this.angerLevel);
			buf.writeCollection(this.possibleActivities, PacketByteBuf::writeString);
			buf.writeCollection(this.runningTasks, PacketByteBuf::writeString);
			buf.writeCollection(this.memories, PacketByteBuf::writeString);
			buf.writeCollection(this.gossips, PacketByteBuf::writeString);
			buf.writeCollection(this.pois, PacketByteBuf::writeBlockPos);
			buf.writeCollection(this.potentialPois, PacketByteBuf::writeBlockPos);
		}

		public boolean isPointOfInterest(BlockPos pos) {
			return this.pois.contains(pos);
		}

		public boolean isPotentialJobSite(BlockPos pos) {
			return this.potentialPois.contains(pos);
		}
	}
}
