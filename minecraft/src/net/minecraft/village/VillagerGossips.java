package net.minecraft.village;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntBinaryOperator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.slf4j.Logger;

public class VillagerGossips {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_30236 = 2;
	private final Map<UUID, VillagerGossips.Reputation> entityReputation = Maps.<UUID, VillagerGossips.Reputation>newHashMap();

	@Debug
	public Map<UUID, Object2IntMap<VillageGossipType>> getEntityReputationAssociatedGossips() {
		Map<UUID, Object2IntMap<VillageGossipType>> map = Maps.<UUID, Object2IntMap<VillageGossipType>>newHashMap();
		this.entityReputation.keySet().forEach(uuid -> {
			VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(uuid);
			map.put(uuid, reputation.associatedGossip);
		});
		return map;
	}

	public void decay() {
		Iterator<VillagerGossips.Reputation> iterator = this.entityReputation.values().iterator();

		while (iterator.hasNext()) {
			VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)iterator.next();
			reputation.decay();
			if (reputation.isObsolete()) {
				iterator.remove();
			}
		}
	}

	private Stream<VillagerGossips.GossipEntry> entries() {
		return this.entityReputation.entrySet().stream().flatMap(entry -> ((VillagerGossips.Reputation)entry.getValue()).entriesFor((UUID)entry.getKey()));
	}

	private Collection<VillagerGossips.GossipEntry> pickGossips(Random random, int count) {
		List<VillagerGossips.GossipEntry> list = this.entries().toList();
		if (list.isEmpty()) {
			return Collections.emptyList();
		} else {
			int[] is = new int[list.size()];
			int i = 0;

			for (int j = 0; j < list.size(); j++) {
				VillagerGossips.GossipEntry gossipEntry = (VillagerGossips.GossipEntry)list.get(j);
				i += Math.abs(gossipEntry.getValue());
				is[j] = i - 1;
			}

			Set<VillagerGossips.GossipEntry> set = Sets.newIdentityHashSet();

			for (int k = 0; k < count; k++) {
				int l = random.nextInt(i);
				int m = Arrays.binarySearch(is, l);
				set.add((VillagerGossips.GossipEntry)list.get(m < 0 ? -m - 1 : m));
			}

			return set;
		}
	}

	private VillagerGossips.Reputation getReputationFor(UUID target) {
		return (VillagerGossips.Reputation)this.entityReputation.computeIfAbsent(target, uuid -> new VillagerGossips.Reputation());
	}

	public void shareGossipFrom(VillagerGossips from, Random random, int count) {
		Collection<VillagerGossips.GossipEntry> collection = from.pickGossips(random, count);
		collection.forEach(gossip -> {
			int i = gossip.value - gossip.type.shareDecrement;
			if (i >= 2) {
				this.getReputationFor(gossip.target).associatedGossip.mergeInt(gossip.type, i, VillagerGossips::max);
			}
		});
	}

	public int getReputationFor(UUID target, Predicate<VillageGossipType> gossipTypeFilter) {
		VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(target);
		return reputation != null ? reputation.getValueFor(gossipTypeFilter) : 0;
	}

	public long getReputationCount(VillageGossipType type, DoublePredicate predicate) {
		return this.entityReputation
			.values()
			.stream()
			.filter(reputation -> predicate.test((double)(reputation.associatedGossip.getOrDefault(type, 0) * type.multiplier)))
			.count();
	}

	public void startGossip(UUID target, VillageGossipType type, int value) {
		VillagerGossips.Reputation reputation = this.getReputationFor(target);
		reputation.associatedGossip.mergeInt(type, value, (IntBinaryOperator)((left, right) -> this.mergeReputation(type, left, right)));
		reputation.clamp(type);
		if (reputation.isObsolete()) {
			this.entityReputation.remove(target);
		}
	}

	public void removeGossip(UUID target, VillageGossipType type, int value) {
		this.startGossip(target, type, -value);
	}

	public void remove(UUID target, VillageGossipType type) {
		VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(target);
		if (reputation != null) {
			reputation.remove(type);
			if (reputation.isObsolete()) {
				this.entityReputation.remove(target);
			}
		}
	}

	public void remove(VillageGossipType type) {
		Iterator<VillagerGossips.Reputation> iterator = this.entityReputation.values().iterator();

		while (iterator.hasNext()) {
			VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)iterator.next();
			reputation.remove(type);
			if (reputation.isObsolete()) {
				iterator.remove();
			}
		}
	}

	public <T> T serialize(DynamicOps<T> ops) {
		return (T)VillagerGossips.GossipEntry.LIST_CODEC
			.encodeStart(ops, this.entries().toList())
			.resultOrPartial(error -> LOGGER.warn("Failed to serialize gossips: {}", error))
			.orElseGet(ops::emptyList);
	}

	public void deserialize(Dynamic<?> dynamic) {
		VillagerGossips.GossipEntry.LIST_CODEC
			.decode(dynamic)
			.resultOrPartial(error -> LOGGER.warn("Failed to deserialize gossips: {}", error))
			.stream()
			.flatMap(pair -> ((List)pair.getFirst()).stream())
			.forEach(entry -> this.getReputationFor(entry.target).associatedGossip.put(entry.type, entry.value));
	}

	private static int max(int left, int right) {
		return Math.max(left, right);
	}

	private int mergeReputation(VillageGossipType type, int left, int right) {
		int i = left + right;
		return i > type.maxValue ? Math.max(type.maxValue, left) : i;
	}

	static record GossipEntry(UUID target, VillageGossipType type, int value) {
		public static final Codec<VillagerGossips.GossipEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Uuids.INT_STREAM_CODEC.fieldOf("Target").forGetter(VillagerGossips.GossipEntry::target),
						VillageGossipType.CODEC.fieldOf("Type").forGetter(VillagerGossips.GossipEntry::type),
						Codecs.POSITIVE_INT.fieldOf("Value").forGetter(VillagerGossips.GossipEntry::value)
					)
					.apply(instance, VillagerGossips.GossipEntry::new)
		);
		public static final Codec<List<VillagerGossips.GossipEntry>> LIST_CODEC = CODEC.listOf();

		public int getValue() {
			return this.value * this.type.multiplier;
		}
	}

	static class Reputation {
		final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap<>();

		public int getValueFor(Predicate<VillageGossipType> gossipTypeFilter) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.filter(entry -> gossipTypeFilter.test((VillageGossipType)entry.getKey()))
				.mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier)
				.sum();
		}

		public Stream<VillagerGossips.GossipEntry> entriesFor(UUID target) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.map(entry -> new VillagerGossips.GossipEntry(target, (VillageGossipType)entry.getKey(), entry.getIntValue()));
		}

		public void decay() {
			ObjectIterator<Entry<VillageGossipType>> objectIterator = this.associatedGossip.object2IntEntrySet().iterator();

			while (objectIterator.hasNext()) {
				Entry<VillageGossipType> entry = (Entry<VillageGossipType>)objectIterator.next();
				int i = entry.getIntValue() - ((VillageGossipType)entry.getKey()).decay;
				if (i < 2) {
					objectIterator.remove();
				} else {
					entry.setValue(i);
				}
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
}
