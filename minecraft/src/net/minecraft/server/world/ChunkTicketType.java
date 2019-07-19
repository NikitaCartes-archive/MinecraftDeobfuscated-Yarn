package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;

public class ChunkTicketType<T> {
	private final String name;
	private final Comparator<T> argumentComparator;
	private final long field_19348;
	public static final ChunkTicketType<Unit> START = create("start", (unit, unit2) -> 0);
	public static final ChunkTicketType<Unit> DRAGON = create("dragon", (unit, unit2) -> 0);
	public static final ChunkTicketType<ChunkPos> PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> LIGHT = create("light", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ColumnPos> PORTAL = create("portal", Comparator.comparingLong(ColumnPos::toLong));
	public static final ChunkTicketType<Integer> POST_TELEPORT = method_20628("post_teleport", Integer::compareTo, 5);
	public static final ChunkTicketType<ChunkPos> UNKNOWN = method_20628("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

	public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator) {
		return new ChunkTicketType<>(name, comparator, 0L);
	}

	public static <T> ChunkTicketType<T> method_20628(String string, Comparator<T> comparator, int i) {
		return new ChunkTicketType<>(string, comparator, (long)i);
	}

	protected ChunkTicketType(String name, Comparator<T> comparator, long expiryTicks) {
		this.name = name;
		this.argumentComparator = comparator;
		this.field_19348 = expiryTicks;
	}

	public String toString() {
		return this.name;
	}

	public Comparator<T> getArgumentComparator() {
		return this.argumentComparator;
	}

	public long method_20629() {
		return this.field_19348;
	}
}
