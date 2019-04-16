package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Void;
import net.minecraft.world.chunk.ChunkPos;

public class ChunkTicketType<T> {
	private final String name;
	private final Comparator<T> argumentComparator;
	public static final ChunkTicketType<Void> START = create("start", (void_, void2) -> 0);
	public static final ChunkTicketType<Void> DRAGON = create("dragon", (void_, void2) -> 0);
	public static final ChunkTicketType<ChunkPos> PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_19270 = create("light", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> UNKNOWN = create("unknown", Comparator.comparingLong(ChunkPos::toLong));

	public static <T> ChunkTicketType<T> create(String string, Comparator<T> comparator) {
		return new ChunkTicketType<>(string, comparator);
	}

	protected ChunkTicketType(String string, Comparator<T> comparator) {
		this.name = string;
		this.argumentComparator = comparator;
	}

	public String toString() {
		return this.name;
	}

	public Comparator<T> getArgumentComparator() {
		return this.argumentComparator;
	}
}
