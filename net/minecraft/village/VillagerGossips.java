/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Uuids;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.VillageGossipType;
import org.slf4j.Logger;

public class VillagerGossips {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_30236 = 2;
    private final Map<UUID, Reputation> entityReputation = Maps.newHashMap();

    @Debug
    public Map<UUID, Object2IntMap<VillageGossipType>> getEntityReputationAssociatedGossips() {
        HashMap<UUID, Object2IntMap<VillageGossipType>> map = Maps.newHashMap();
        this.entityReputation.keySet().forEach(uuid -> {
            Reputation reputation = this.entityReputation.get(uuid);
            map.put((UUID)uuid, reputation.associatedGossip);
        });
        return map;
    }

    public void decay() {
        Iterator<Reputation> iterator = this.entityReputation.values().iterator();
        while (iterator.hasNext()) {
            Reputation reputation = iterator.next();
            reputation.decay();
            if (!reputation.isObsolete()) continue;
            iterator.remove();
        }
    }

    private Stream<GossipEntry> entries() {
        return this.entityReputation.entrySet().stream().flatMap(entry -> ((Reputation)entry.getValue()).entriesFor((UUID)entry.getKey()));
    }

    private Collection<GossipEntry> pickGossips(Random random, int count) {
        List<GossipEntry> list = this.entries().toList();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        int[] is = new int[list.size()];
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            GossipEntry gossipEntry = list.get(j);
            is[j] = (i += Math.abs(gossipEntry.getValue())) - 1;
        }
        Set<GossipEntry> set = Sets.newIdentityHashSet();
        for (int k = 0; k < count; ++k) {
            int l = random.nextInt(i);
            int m = Arrays.binarySearch(is, l);
            set.add(list.get(m < 0 ? -m - 1 : m));
        }
        return set;
    }

    private Reputation getReputationFor(UUID target) {
        return this.entityReputation.computeIfAbsent(target, uuid -> new Reputation());
    }

    public void shareGossipFrom(VillagerGossips from, Random random, int count) {
        Collection<GossipEntry> collection = from.pickGossips(random, count);
        collection.forEach(gossip -> {
            int i = gossip.value - gossip.type.shareDecrement;
            if (i >= 2) {
                this.getReputationFor((UUID)gossip.target).associatedGossip.mergeInt(gossip.type, i, VillagerGossips::max);
            }
        });
    }

    public int getReputationFor(UUID target, Predicate<VillageGossipType> gossipTypeFilter) {
        Reputation reputation = this.entityReputation.get(target);
        return reputation != null ? reputation.getValueFor(gossipTypeFilter) : 0;
    }

    public long getReputationCount(VillageGossipType type, DoublePredicate predicate) {
        return this.entityReputation.values().stream().filter(reputation -> predicate.test(reputation.associatedGossip.getOrDefault((Object)type, 0) * villageGossipType.multiplier)).count();
    }

    public void startGossip(UUID target, VillageGossipType type, int value) {
        Reputation reputation = this.getReputationFor(target);
        reputation.associatedGossip.mergeInt(type, value, (left, right) -> this.mergeReputation(type, left, right));
        reputation.clamp(type);
        if (reputation.isObsolete()) {
            this.entityReputation.remove(target);
        }
    }

    public void removeGossip(UUID target, VillageGossipType type, int value) {
        this.startGossip(target, type, -value);
    }

    public void remove(UUID target, VillageGossipType type) {
        Reputation reputation = this.entityReputation.get(target);
        if (reputation != null) {
            reputation.remove(type);
            if (reputation.isObsolete()) {
                this.entityReputation.remove(target);
            }
        }
    }

    public void remove(VillageGossipType type) {
        Iterator<Reputation> iterator = this.entityReputation.values().iterator();
        while (iterator.hasNext()) {
            Reputation reputation = iterator.next();
            reputation.remove(type);
            if (!reputation.isObsolete()) continue;
            iterator.remove();
        }
    }

    public <T> T serialize(DynamicOps<T> ops) {
        return (T)GossipEntry.LIST_CODEC.encodeStart(ops, this.entries().toList()).resultOrPartial(error -> LOGGER.warn("Failed to serialize gossips: {}", error)).orElseGet(ops::emptyList);
    }

    public void deserialize(Dynamic<?> dynamic) {
        GossipEntry.LIST_CODEC.decode(dynamic).resultOrPartial(error -> LOGGER.warn("Failed to deserialize gossips: {}", error)).stream().flatMap(pair -> ((List)pair.getFirst()).stream()).forEach(entry -> this.getReputationFor((UUID)entry.target).associatedGossip.put(entry.type, entry.value));
    }

    private static int max(int left, int right) {
        return Math.max(left, right);
    }

    private int mergeReputation(VillageGossipType type, int left, int right) {
        int i = left + right;
        return i > type.maxValue ? Math.max(type.maxValue, left) : i;
    }

    static class Reputation {
        final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap<VillageGossipType>();

        Reputation() {
        }

        public int getValueFor(Predicate<VillageGossipType> gossipTypeFilter) {
            return this.associatedGossip.object2IntEntrySet().stream().filter(entry -> gossipTypeFilter.test((VillageGossipType)entry.getKey())).mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier).sum();
        }

        public Stream<GossipEntry> entriesFor(UUID target) {
            return this.associatedGossip.object2IntEntrySet().stream().map(entry -> new GossipEntry(target, (VillageGossipType)entry.getKey(), entry.getIntValue()));
        }

        public void decay() {
            Iterator objectIterator = this.associatedGossip.object2IntEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
                int i = entry.getIntValue() - ((VillageGossipType)entry.getKey()).decay;
                if (i < 2) {
                    objectIterator.remove();
                    continue;
                }
                entry.setValue(i);
            }
        }

        public boolean isObsolete() {
            return this.associatedGossip.isEmpty();
        }

        public void clamp(VillageGossipType gossipType) {
            int i = this.associatedGossip.getInt(gossipType);
            if (i > gossipType.maxValue) {
                this.associatedGossip.put(gossipType, gossipType.maxValue);
            }
            if (i < 2) {
                this.remove(gossipType);
            }
        }

        public void remove(VillageGossipType gossipType) {
            this.associatedGossip.removeInt(gossipType);
        }
    }

    record GossipEntry(UUID target, VillageGossipType type, int value) {
        public static final Codec<GossipEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Uuids.INT_STREAM_CODEC.fieldOf("Target")).forGetter(GossipEntry::target), ((MapCodec)VillageGossipType.CODEC.fieldOf("Type")).forGetter(GossipEntry::type), ((MapCodec)Codecs.POSITIVE_INT.fieldOf("Value")).forGetter(GossipEntry::value)).apply((Applicative<GossipEntry, ?>)instance, GossipEntry::new));
        public static final Codec<List<GossipEntry>> LIST_CODEC = CODEC.listOf();

        public int getValue() {
            return this.value * this.type.multiplier;
        }
    }
}

