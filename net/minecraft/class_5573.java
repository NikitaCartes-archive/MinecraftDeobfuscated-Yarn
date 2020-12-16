/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.class_5572;
import net.minecraft.class_5575;
import net.minecraft.class_5584;
import net.minecraft.entity.EntityLike;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

public class class_5573<T extends EntityLike> {
    private final Class<T> field_27250;
    private final Long2ObjectFunction<class_5584> field_27251;
    private final Long2ObjectMap<class_5572<T>> field_27252 = new Long2ObjectOpenHashMap<class_5572<T>>();
    private final LongSortedSet field_27253 = new LongAVLTreeSet();

    public class_5573(Class<T> class_, Long2ObjectFunction<class_5584> long2ObjectFunction) {
        this.field_27250 = class_;
        this.field_27251 = long2ObjectFunction;
    }

    public static long method_31779(BlockPos blockPos) {
        return ChunkSectionPos.asLong(ChunkSectionPos.getSectionCoord(blockPos.getX()), ChunkSectionPos.getSectionCoord(blockPos.getY()), ChunkSectionPos.getSectionCoord(blockPos.getZ()));
    }

    public void method_31777(Box box, Consumer<class_5572<T>> consumer) {
        int i = ChunkSectionPos.method_32204(box.minX - 2.0);
        int j = ChunkSectionPos.method_32204(box.minY - 2.0);
        int k = ChunkSectionPos.method_32204(box.minZ - 2.0);
        int l = ChunkSectionPos.method_32204(box.maxX + 2.0);
        int m = ChunkSectionPos.method_32204(box.maxY + 2.0);
        int n = ChunkSectionPos.method_32204(box.maxZ + 2.0);
        for (int o = i; o <= l; ++o) {
            long p = ChunkSectionPos.asLong(o, 0, 0);
            long q = ChunkSectionPos.asLong(o, -1, -1);
            LongBidirectionalIterator longIterator = this.field_27253.subSet(p, q + 1L).iterator();
            while (longIterator.hasNext()) {
                class_5572 lv;
                long r = longIterator.nextLong();
                int s = ChunkSectionPos.unpackY(r);
                int t = ChunkSectionPos.unpackZ(r);
                if (s < j || s > m || t < k || t > n || (lv = (class_5572)this.field_27252.get(r)) == null || !lv.method_31768().shouldTrack()) continue;
                consumer.accept(lv);
            }
        }
    }

    public LongStream method_31772(long l) {
        int j;
        int i = ChunkPos.getPackedX(l);
        LongSortedSet longSortedSet = this.method_31771(i, j = ChunkPos.getPackedZ(l));
        if (longSortedSet.isEmpty()) {
            return LongStream.empty();
        }
        LongBidirectionalIterator ofLong = longSortedSet.iterator();
        return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(ofLong, 1301), false);
    }

    private LongSortedSet method_31771(int i, int j) {
        long l = ChunkSectionPos.asLong(i, 0, j);
        long m = ChunkSectionPos.asLong(i, -1, j);
        return this.field_27253.subSet(l, m + 1L);
    }

    public Stream<class_5572<T>> method_31782(long l) {
        return this.method_31772(l).mapToObj(this.field_27252::get).filter(Objects::nonNull);
    }

    private static long method_31787(long l) {
        return ChunkPos.toLong(ChunkSectionPos.unpackX(l), ChunkSectionPos.unpackZ(l));
    }

    public class_5572<T> method_31784(long l) {
        return this.field_27252.computeIfAbsent(l, this::method_31788);
    }

    @Nullable
    public class_5572<T> method_31785(long l) {
        return (class_5572)this.field_27252.get(l);
    }

    private class_5572<T> method_31788(long l) {
        long m = class_5573.method_31787(l);
        class_5584 lv = this.field_27251.get(m);
        this.field_27253.add(l);
        return new class_5572<T>(this.field_27250, lv);
    }

    public LongSet method_31770() {
        LongOpenHashSet longSet = new LongOpenHashSet();
        this.field_27252.keySet().forEach(l -> longSet.add(class_5573.method_31787(l)));
        return longSet;
    }

    private static <T extends EntityLike> Predicate<T> method_31775(Box box) {
        return entityLike -> entityLike.getBoundingBox().intersects(box);
    }

    public void method_31783(Box box, Consumer<T> consumer) {
        this.method_31777(box, arg -> arg.method_31765(class_5573.method_31775(box), consumer));
    }

    public <U extends T> void method_31773(class_5575<T, U> arg, Box box, Consumer<U> consumer) {
        this.method_31777(box, arg2 -> arg2.method_31762(arg, class_5573.method_31775(box), consumer));
    }

    public void method_31786(long l) {
        this.field_27252.remove(l);
        this.field_27253.remove(l);
    }

    public int method_31781() {
        return this.field_27253.size();
    }
}

