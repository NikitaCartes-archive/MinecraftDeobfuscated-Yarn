package net.minecraft.advancement;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record AdvancementEntry(Identifier id, Advancement value) {
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.id);
		this.value.write(buf);
	}

	public static AdvancementEntry read(PacketByteBuf buf) {
		return new AdvancementEntry(buf.readIdentifier(), Advancement.read(buf));
	}

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
