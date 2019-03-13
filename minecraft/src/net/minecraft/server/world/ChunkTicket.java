package net.minecraft.server.world;

import java.util.Objects;
import net.minecraft.entity.player.ChunkTicketType;

final class ChunkTicket<T> implements Comparable<ChunkTicket<?>> {
	private final ChunkTicketType<T> field_14023;
	private final int level;
	private final T argument;
	private final long location;

	ChunkTicket(ChunkTicketType<T> chunkTicketType, int i, T object, long l) {
		this.field_14023 = chunkTicketType;
		this.level = i;
		this.argument = object;
		this.location = l;
	}

	public int method_14285(ChunkTicket<?> chunkTicket) {
		int i = Integer.compare(this.level, chunkTicket.level);
		if (i != 0) {
			return i;
		} else {
			int j = Integer.compare(System.identityHashCode(this.field_14023), System.identityHashCode(chunkTicket.field_14023));
			return j != 0 ? j : this.field_14023.getArgumentComparator().compare(this.argument, chunkTicket.argument);
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ChunkTicket)) {
			return false;
		} else {
			ChunkTicket<?> chunkTicket = (ChunkTicket<?>)object;
			return this.level == chunkTicket.level && Objects.equals(this.field_14023, chunkTicket.field_14023) && Objects.equals(this.argument, chunkTicket.argument);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_14023, this.level, this.argument});
	}

	public String toString() {
		return "Ticket[" + this.field_14023 + " " + this.level + " (" + this.argument + ")] at " + this.location;
	}

	public ChunkTicketType<T> method_14281() {
		return this.field_14023;
	}

	public int getLevel() {
		return this.level;
	}

	public long getLocation() {
		return this.location;
	}
}
