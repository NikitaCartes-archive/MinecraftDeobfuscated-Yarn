package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;

public class VillagerGossips {
	private final Map<UUID, VillagerGossips.Reputation> entityReputation = Maps.<UUID, VillagerGossips.Reputation>newHashMap();

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
		List<VillagerGossips.GossipEntry> list = (List<VillagerGossips.GossipEntry>)this.entries().collect(Collectors.toList());
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
				set.add(list.get(m < 0 ? -m - 1 : m));
			}

			return set;
		}
	}

	private VillagerGossips.Reputation getReputationFor(UUID target) {
		return (VillagerGossips.Reputation)this.entityReputation.computeIfAbsent(target, uUID -> new VillagerGossips.Reputation());
	}

	public void shareGossipFrom(VillagerGossips from, Random random, int count) {
		Collection<VillagerGossips.GossipEntry> collection = from.pickGossips(random, count);
		collection.forEach(gossipEntry -> {
			int i = gossipEntry.value - gossipEntry.type.shareDecrement;
			if (i >= 2) {
				this.getReputationFor(gossipEntry.target).associatedGossip.mergeInt(gossipEntry.type, i, VillagerGossips::max);
			}
		});
	}

	public int getReputationFor(UUID target, Predicate<VillageGossipType> gossipTypeFilter) {
		VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(target);
		return reputation != null ? reputation.getValueFor(gossipTypeFilter) : 0;
	}

	public void startGossip(UUID target, VillageGossipType type, int value) {
		VillagerGossips.Reputation reputation = this.getReputationFor(target);
		reputation.associatedGossip.mergeInt(type, value, (integer, integer2) -> this.mergeReputation(type, integer, integer2));
		reputation.clamp(type);
		if (reputation.isObsolete()) {
			this.entityReputation.remove(target);
		}
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createList(this.entries().map(gossipEntry -> gossipEntry.serialize(ops)).map(Dynamic::getValue)));
	}

	public void deserialize(Dynamic<?> dynamic) {
		dynamic.asStream()
			.map(VillagerGossips.GossipEntry::deserialize)
			.flatMap(Util::stream)
			.forEach(gossipEntry -> this.getReputationFor(gossipEntry.target).associatedGossip.put(gossipEntry.type, gossipEntry.value));
	}

	private static int max(int left, int right) {
		return Math.max(left, right);
	}

	private int mergeReputation(VillageGossipType type, int left, int right) {
		int i = left + right;
		return i > type.maxValue ? Math.max(type.maxValue, left) : i;
	}

	static class GossipEntry {
		public final UUID target;
		public final VillageGossipType type;
		public final int value;

		public GossipEntry(UUID target, VillageGossipType type, int value) {
			this.target = target;
			this.type = type;
			this.value = value;
		}

		public int getValue() {
			return this.value * this.type.multiplier;
		}

		public String toString() {
			return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
		}

		public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
			return Util.writeUuid(
				"Target",
				this.target,
				new Dynamic<>(
					ops, ops.createMap(ImmutableMap.of(ops.createString("Type"), ops.createString(this.type.key), ops.createString("Value"), ops.createInt(this.value)))
				)
			);
		}

		public static Optional<VillagerGossips.GossipEntry> deserialize(Dynamic<?> dynamic) {
			return dynamic.get("Type")
				.asString()
				.map(VillageGossipType::byKey)
				.flatMap(
					villageGossipType -> Util.readUuid("Target", dynamic)
							.flatMap(uUID -> dynamic.get("Value").asNumber().map(number -> new VillagerGossips.GossipEntry(uUID, villageGossipType, number.intValue())))
				);
		}
	}

	static class Reputation {
		private final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap<>();

		private Reputation() {
		}

		public int getValueFor(Predicate<VillageGossipType> gossipTypeFilter) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.filter(entry -> gossipTypeFilter.test(entry.getKey()))
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
