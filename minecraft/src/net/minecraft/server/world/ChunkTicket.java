package net.minecraft.server.world;

import java.util.Objects;

public final class ChunkTicket<T> implements Comparable<ChunkTicket<?>> {
	private final ChunkTicketType<T> type;
	private final int level;
	private final T argument;
	private long location;

	protected ChunkTicket(ChunkTicketType<T> type, int level, T argument) {
		this.type = type;
		this.level = level;
		this.argument = argument;
	}

	public int compareTo(ChunkTicket<?> chunkTicket) {
		int i = Integer.compare(this.level, chunkTicket.level);
		if (i != 0) {
			return i;
		} else {
			int j = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(chunkTicket.type));
			return j != 0 ? j : this.type.getArgumentComparator().compare(this.argument, chunkTicket.argument);
		}
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChunkTicket)) {
			return false;
		} else {
			ChunkTicket<?> chunkTicket = (ChunkTicket<?>)obj;
			return this.level == chunkTicket.level && Objects.equals(this.type, chunkTicket.type) && Objects.equals(this.argument, chunkTicket.argument);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.type, this.level, this.argument});
	}

	public String toString() {
		return "Ticket[" + this.type + " " + this.level + " (" + this.argument + ")] at " + this.location;
	}

	public ChunkTicketType<T> getType() {
		return this.type;
	}

	public int getLevel() {
		return this.level;
	}

	protected void method_23956(long l) {
		this.location = l;
	}

	protected boolean method_20627(long l) {
		long m = this.type.method_20629();
		return m != 0L && l - this.location > m;
	}
}
