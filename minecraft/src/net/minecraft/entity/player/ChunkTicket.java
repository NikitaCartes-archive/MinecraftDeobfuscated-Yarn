package net.minecraft.entity.player;

import java.util.Objects;

public final class ChunkTicket<T> implements Comparable<ChunkTicket<?>> {
	private final ChunkTicketType<T> type;
	private final int field_14025;
	private final T argument;
	private final long location;

	public ChunkTicket(ChunkTicketType<T> chunkTicketType, int i, T object, long l) {
		this.type = chunkTicketType;
		this.field_14025 = i;
		this.argument = object;
		this.location = l;
	}

	public int compareTo(ChunkTicket<?> chunkTicket) {
		int i = Integer.compare(this.field_14025, chunkTicket.field_14025);
		if (i != 0) {
			return i;
		} else {
			int j = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(chunkTicket.type));
			return j != 0 ? j : this.type.getArgumentComparator().compare(this.argument, chunkTicket.argument);
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ChunkTicket)) {
			return false;
		} else {
			ChunkTicket<?> chunkTicket = (ChunkTicket<?>)object;
			return this.field_14025 == chunkTicket.field_14025 && Objects.equals(this.type, chunkTicket.type) && Objects.equals(this.argument, chunkTicket.argument);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.type, this.field_14025, this.argument});
	}

	public String toString() {
		return "Ticket[" + this.type + " " + this.field_14025 + " (" + this.argument + ")] at " + this.location;
	}

	public ChunkTicketType<T> method_14281() {
		return this.type;
	}

	public int method_14283() {
		return this.field_14025;
	}

	public long getLocation() {
		return this.location;
	}

	public ChunkTicket<T> method_14282(int i) {
		return new ChunkTicket<>(this.type, i, this.argument, this.location);
	}
}
