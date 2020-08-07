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
	public static final ChunkTicketType<Unit> field_14030 = create("start", (unit, unit2) -> 0);
	/**
	 * Used by the ender dragon to load the central end island during the boss battle.
	 */
	public static final ChunkTicketType<Unit> field_17264 = create("dragon", (unit, unit2) -> 0);
	public static final ChunkTicketType<ChunkPos> field_14033 = create("player", Comparator.comparingLong(ChunkPos::toLong));
	/**
	 * Used to force load chunks.
	 */
	public static final ChunkTicketType<ChunkPos> field_14031 = create("forced", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_19270 = create("light", Comparator.comparingLong(ChunkPos::toLong));
	/**
	 * Used by a nether portal to load chunks in the other dimension.
	 */
	public static final ChunkTicketType<BlockPos> field_19280 = create("portal", Vec3i::method_10265, 300);
	/**
	 * Used to load the chunks at the destination of teleportation.
	 */
	public static final ChunkTicketType<Integer> field_19347 = create("post_teleport", Integer::compareTo, 5);
	/**
	 * Represents a type of ticket that has an unknown cause for loading chunks.
	 */
	public static final ChunkTicketType<ChunkPos> field_14032 = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

	/**
	 * Creates a new ticket type that does not expire.
	 */
	public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator) {
		return new ChunkTicketType<>(name, comparator, 0L);
	}

	/**
	 * Create a new ticket type that expires after an amount of ticks.
	 * 
	 * @param expiryTicks the expiry time in ticks, does not expire if 0
	 */
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
