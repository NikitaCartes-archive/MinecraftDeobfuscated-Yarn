/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;

public class ChunkTicketType<T> {
    private final String name;
    private final Comparator<T> argumentComparator;
    public static final ChunkTicketType<Unit> START = ChunkTicketType.create("start", (unit, unit2) -> 0);
    public static final ChunkTicketType<Unit> DRAGON = ChunkTicketType.create("dragon", (unit, unit2) -> 0);
    public static final ChunkTicketType<ChunkPos> PLAYER = ChunkTicketType.create("player", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ChunkPos> FORCED = ChunkTicketType.create("forced", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ChunkPos> LIGHT = ChunkTicketType.create("light", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ColumnPos> PORTAL = ChunkTicketType.create("portal", Comparator.comparingLong(ColumnPos::toLong));
    public static final ChunkTicketType<ChunkPos> UNKNOWN = ChunkTicketType.create("unknown", Comparator.comparingLong(ChunkPos::toLong));

    public static <T> ChunkTicketType<T> create(String string, Comparator<T> comparator) {
        return new ChunkTicketType<T>(string, comparator);
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

