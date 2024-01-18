package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.ChunkSectionPos;

public record DebugVillageSectionsCustomPayload(Set<ChunkSectionPos> villageChunks, Set<ChunkSectionPos> notVillageChunks) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugVillageSectionsCustomPayload> CODEC = CustomPayload.codecOf(
		DebugVillageSectionsCustomPayload::write, DebugVillageSectionsCustomPayload::new
	);
	public static final CustomPayload.Id<DebugVillageSectionsCustomPayload> ID = CustomPayload.id("debug/village_sections");

	private DebugVillageSectionsCustomPayload(PacketByteBuf buf) {
		this(buf.readCollection(HashSet::new, PacketByteBuf::readChunkSectionPos), buf.readCollection(HashSet::new, PacketByteBuf::readChunkSectionPos));
	}

	private void write(PacketByteBuf buf) {
		buf.writeCollection(this.villageChunks, PacketByteBuf::writeChunkSectionPos);
		buf.writeCollection(this.notVillageChunks, PacketByteBuf::writeChunkSectionPos);
	}

	@Override
	public CustomPayload.Id<DebugVillageSectionsCustomPayload> getId() {
		return ID;
	}
}
