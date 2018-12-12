package net.minecraft.entity.player;

import java.util.Comparator;
import net.minecraft.class_3902;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.ChunkPos;

public class ChunkTicketType<T> {
	private final String name;
	private final Comparator<T> argumentComparator;
	public static final ChunkTicketType<class_3902> START = create("start", (arg, arg2) -> 0);
	public static final ChunkTicketType<class_3902> DRAGON = create("dragon", (arg, arg2) -> 0);
	public static final ChunkTicketType<PlayerEntity> PLAYER = create("player", Comparator.comparingInt(Entity::getEntityId));
	public static final ChunkTicketType<ChunkPos> FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
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
