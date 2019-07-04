/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class LevelPrioritizedQueue<T> {
    public static final int LEVEL_COUNT = ThreadedAnvilChunkStorage.MAX_LEVEL + 2;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> levelToPosToElements = IntStream.range(0, LEVEL_COUNT).mapToObj(i -> new Long2ObjectLinkedOpenHashMap()).collect(Collectors.toList());
    private volatile int firstNonEmptyLevel = LEVEL_COUNT;
    private final String name;
    private final LongSet chunkPositions = new LongOpenHashSet();
    private final int maxSize;

    public LevelPrioritizedQueue(String string, int i2) {
        this.name = string;
        this.maxSize = i2;
    }

    protected void updateLevel(int i, ChunkPos chunkPos, int j) {
        if (i >= LEVEL_COUNT) {
            return;
        }
        Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = this.levelToPosToElements.get(i);
        List<Optional<T>> list = long2ObjectLinkedOpenHashMap.remove(chunkPos.toLong());
        if (i == this.firstNonEmptyLevel) {
            while (this.firstNonEmptyLevel < LEVEL_COUNT && this.levelToPosToElements.get(this.firstNonEmptyLevel).isEmpty()) {
                ++this.firstNonEmptyLevel;
            }
        }
        if (list != null && !list.isEmpty()) {
            this.levelToPosToElements.get(j).computeIfAbsent(chunkPos.toLong(), l -> Lists.newArrayList()).addAll(list);
            this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, j);
        }
    }

    protected void add(Optional<T> optional, long l2, int i) {
        this.levelToPosToElements.get(i).computeIfAbsent(l2, l -> Lists.newArrayList()).add(optional);
        this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, i);
    }

    protected void clearPosition(long l, boolean bl) {
        for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap : this.levelToPosToElements) {
            List<Optional<T>> list = long2ObjectLinkedOpenHashMap.get(l);
            if (list == null) continue;
            if (bl) {
                list.clear();
            } else {
                list.removeIf(optional -> !optional.isPresent());
            }
            if (!list.isEmpty()) continue;
            long2ObjectLinkedOpenHashMap.remove(l);
        }
        while (this.firstNonEmptyLevel < LEVEL_COUNT && this.levelToPosToElements.get(this.firstNonEmptyLevel).isEmpty()) {
            ++this.firstNonEmptyLevel;
        }
        this.chunkPositions.remove(l);
    }

    private Runnable createPositionAdder(long l) {
        return () -> this.chunkPositions.add(l);
    }

    @Nullable
    public Stream<Either<T, Runnable>> poll() {
        if (this.chunkPositions.size() >= this.maxSize) {
            return null;
        }
        if (this.firstNonEmptyLevel < LEVEL_COUNT) {
            int i = this.firstNonEmptyLevel;
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = this.levelToPosToElements.get(i);
            long l = long2ObjectLinkedOpenHashMap.firstLongKey();
            List<Optional<T>> list = long2ObjectLinkedOpenHashMap.removeFirst();
            while (this.firstNonEmptyLevel < LEVEL_COUNT && this.levelToPosToElements.get(this.firstNonEmptyLevel).isEmpty()) {
                ++this.firstNonEmptyLevel;
            }
            return list.stream().map(optional -> optional.map(Either::left).orElseGet(() -> Either.right(this.createPositionAdder(l))));
        }
        return null;
    }

    public String toString() {
        return this.name + " " + this.firstNonEmptyLevel + "...";
    }

    @VisibleForTesting
    LongSet method_21679() {
        return new LongOpenHashSet(this.chunkPositions);
    }
}

