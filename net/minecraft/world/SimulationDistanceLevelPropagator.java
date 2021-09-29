/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;

public class SimulationDistanceLevelPropagator
extends ChunkPosDistanceLevelPropagator {
    private static final int field_34889 = 4;
    protected final Long2ByteMap levels = new Long2ByteOpenHashMap();
    private final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> tickets = new Long2ObjectOpenHashMap();

    public SimulationDistanceLevelPropagator() {
        super(34, 16, 256);
        this.levels.defaultReturnValue((byte)33);
    }

    private SortedArraySet<ChunkTicket<?>> getTickets(long pos) {
        return this.tickets.computeIfAbsent(pos, p -> SortedArraySet.create(4));
    }

    private int getLevel(SortedArraySet<ChunkTicket<?>> ticket) {
        return ticket.isEmpty() ? 34 : ticket.first().getLevel();
    }

    public void add(long pos, ChunkTicket<?> ticket) {
        SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTickets(pos);
        int i = this.getLevel(sortedArraySet);
        sortedArraySet.add(ticket);
        if (ticket.getLevel() < i) {
            this.updateLevel(pos, ticket.getLevel(), true);
        }
    }

    public void remove(long pos, ChunkTicket<?> ticket) {
        SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTickets(pos);
        sortedArraySet.remove(ticket);
        if (sortedArraySet.isEmpty()) {
            this.tickets.remove(pos);
        }
        this.updateLevel(pos, this.getLevel(sortedArraySet), false);
    }

    public <T> void add(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        this.add(pos.toLong(), new ChunkTicket<T>(type, level, argument));
    }

    public <T> void remove(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(type, level, argument);
        this.remove(pos.toLong(), chunkTicket);
    }

    public void updatePlayerTickets(int level) {
        ArrayList<Pair<ChunkTicket, Long>> list = new ArrayList<Pair<ChunkTicket, Long>>();
        for (Long2ObjectMap.Entry entry : this.tickets.long2ObjectEntrySet()) {
            for (ChunkTicket chunkTicket : (SortedArraySet)entry.getValue()) {
                if (chunkTicket.getType() != ChunkTicketType.PLAYER) continue;
                list.add(Pair.of(chunkTicket, entry.getLongKey()));
            }
        }
        for (Pair pair : list) {
            ChunkTicket chunkTicket;
            Long long_ = (Long)pair.getSecond();
            chunkTicket = (ChunkTicket)pair.getFirst();
            this.remove(long_, chunkTicket);
            ChunkPos chunkPos = new ChunkPos(long_);
            ChunkTicketType chunkTicketType = chunkTicket.getType();
            this.add(chunkTicketType, chunkPos, level, chunkPos);
        }
    }

    @Override
    protected int getInitialLevel(long id) {
        SortedArraySet<ChunkTicket<?>> sortedArraySet = this.tickets.get(id);
        if (sortedArraySet == null || sortedArraySet.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        return sortedArraySet.first().getLevel();
    }

    public int getLevel(ChunkPos pos) {
        return this.getLevel(pos.toLong());
    }

    @Override
    protected int getLevel(long id) {
        return this.levels.get(id);
    }

    @Override
    protected void setLevel(long id, int level) {
        if (level > 33) {
            this.levels.remove(id);
        } else {
            this.levels.put(id, (byte)level);
        }
    }

    public void updateLevels() {
        this.applyPendingUpdates(Integer.MAX_VALUE);
    }

    public String getTickingTicket(long pos) {
        SortedArraySet<ChunkTicket<?>> sortedArraySet = this.tickets.get(pos);
        if (sortedArraySet == null || sortedArraySet.isEmpty()) {
            return "no_ticket";
        }
        return sortedArraySet.first().toString();
    }
}

