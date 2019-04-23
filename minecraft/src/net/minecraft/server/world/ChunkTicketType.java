package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;

public class ChunkTicketType<T> {
	private final String name;
	private final Comparator<T> argumentComparator;
	public static final ChunkTicketType<Unit> field_14030 = create("start", (unit, unit2) -> 0);
	public static final ChunkTicketType<Unit> field_17264 = create("dragon", (unit, unit2) -> 0);
	public static final ChunkTicketType<ChunkPos> field_14033 = create("player", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_14031 = create("forced", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_19270 = create("light", Comparator.comparingLong(ChunkPos::toLong));
	public static final ChunkTicketType<ColumnPos> field_19280 = create("portal", Comparator.comparingLong(ColumnPos::toLong));
	public static final ChunkTicketType<ChunkPos> field_14032 = create("unknown", Comparator.comparingLong(ChunkPos::toLong));

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
