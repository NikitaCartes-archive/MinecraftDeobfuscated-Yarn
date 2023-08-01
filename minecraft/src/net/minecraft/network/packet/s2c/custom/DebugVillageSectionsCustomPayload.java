package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;

public record DebugVillageSectionsCustomPayload(Set<ChunkSectionPos> villageChunks, Set<ChunkSectionPos> notVillageChunks) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/village_sections");

	public DebugVillageSectionsCustomPayload(PacketByteBuf buf) {
		this(buf.readCollection(HashSet::new, PacketByteBuf::readChunkSectionPos), buf.readCollection(HashSet::new, PacketByteBuf::readChunkSectionPos));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.villageChunks, PacketByteBuf::writeChunkSectionPos);
		buf.writeCollection(this.notVillageChunks, PacketByteBuf::writeChunkSectionPos);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
