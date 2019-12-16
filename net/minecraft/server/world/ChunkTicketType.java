/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
    public static final ChunkTicketType<Unit> START = ChunkTicketType.create("start", (unit, unit2) -> 0);
    public static final ChunkTicketType<Unit> DRAGON = ChunkTicketType.create("dragon", (unit, unit2) -> 0);
    public static final ChunkTicketType<ChunkPos> PLAYER = ChunkTicketType.create("player", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ChunkPos> FORCED = ChunkTicketType.create("forced", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ChunkPos> LIGHT = ChunkTicketType.create("light", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<BlockPos> PORTAL = ChunkTicketType.create("portal", Vec3i::compareTo, 300);
    public static final ChunkTicketType<Integer> POST_TELEPORT = ChunkTicketType.create("post_teleport", Integer::compareTo, 5);
    public static final ChunkTicketType<ChunkPos> UNKNOWN = ChunkTicketType.create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

    public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator) {
        return new ChunkTicketType<T>(name, comparator, 0L);
    }

    public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator, int expiryTicks) {
        return new ChunkTicketType<T>(name, comparator, expiryTicks);
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

