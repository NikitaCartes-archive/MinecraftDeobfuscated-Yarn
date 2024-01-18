package net.minecraft.advancement;

import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record AdvancementEntry(Identifier id, Advancement value) {
	public static final PacketCodec<RegistryByteBuf, AdvancementEntry> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, AdvancementEntry::id, Advancement.PACKET_CODEC, AdvancementEntry::value, AdvancementEntry::new
	);
	public static final PacketCodec<RegistryByteBuf, List<AdvancementEntry>> LIST_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs.toList());

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof AdvancementEntry advancementEntry && this.id.equals(advancementEntry.id)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public String toString() {
		return this.id.toString();
	}
}
