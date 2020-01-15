package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;

public class ChunkTicketType<T> {
	private final String name;
	private final Comparator<T> argumentComparator;
	private final long expiryTicks;
	public static final ChunkTicketType<Unit> START = create("start", (unit, unit2) -> 0);
	public static final ChunkTicketType<Unit> DRAGON = create("dragon", (unit, unit2) -> 0);
	public static final ChunkTicketType<ChunkPos> PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> LIGHT = create("light", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<BlockPos> PORTAL = create("portal", Vec3i::compareTo, 300);
	public static final ChunkTicketType<Integer> POST_TELEPORT = create("post_teleport", Integer::compareTo, 5);
	public static final ChunkTicketType<ChunkPos> UNKNOWN = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

	public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator) {
		return new ChunkTicketType<>(name, comparator, 0L);
	}

	public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator, int expiryTicks) {
		return new ChunkTicketType<>(name, comparator, (long)expiryTicks);
	}

	protected ChunkTicketType(String name, Comparator<T> comparator, long expiryTicks) {
		this.name = name;
		this.argumentComparator = comparator;
		this.expiryTicks = expiryTicks;
	}

	public String toString() {
		return this.name;
	}

	public Comparator<T> getArgumentComparator() {
		return this.argumentComparator;
	}

	public long getExpiryTicks() {
		return this.expiryTicks;
	}
}
