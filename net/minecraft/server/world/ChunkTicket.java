/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import java.util.Objects;
import net.minecraft.server.world.ChunkTicketType;

public final class ChunkTicket<T>
implements Comparable<ChunkTicket<?>> {
    private final ChunkTicketType<T> type;
    private final int level;
    private final T argument;
    private final long location;

    protected ChunkTicket(ChunkTicketType<T> chunkTicketType, int i, T object, long l) {
        this.type = chunkTicketType;
        this.level = i;
        this.argument = object;
        this.location = l;
    }

    public int method_14285(ChunkTicket<?> chunkTicket) {
        int i = Integer.compare(this.level, chunkTicket.level);
        if (i != 0) {
            return i;
        }
        int j = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(chunkTicket.type));
        if (j != 0) {
            return j;
        }
        return this.type.getArgumentComparator().compare(this.argument, chunkTicket.argument);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ChunkTicket)) {
            return false;
        }
        ChunkTicket chunkTicket = (ChunkTicket)object;
        return this.level == chunkTicket.level && Objects.equals(this.type, chunkTicket.type) && Objects.equals(this.argument, chunkTicket.argument);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.level, this.argument);
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

    public boolean method_20627(long l) {
        long m = this.type.method_20629();
        return m != 0L && l - this.location > m;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.method_14285((ChunkTicket)object);
    }
}

