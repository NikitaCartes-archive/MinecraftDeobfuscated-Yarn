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
import net.minecraft.util.SystemUtil;

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

	private Collection<VillagerGossips.GossipEntry> pickGossips(Random random, int i) {
		List<VillagerGossips.GossipEntry> list = (List<VillagerGossips.GossipEntry>)this.entries().collect(Collectors.toList());
		if (list.isEmpty()) {
			return Collections.emptyList();
		} else {
			int[] is = new int[list.size()];
			int j = 0;

			for (int k = 0; k < list.size(); k++) {
				VillagerGossips.GossipEntry gossipEntry = (VillagerGossips.GossipEntry)list.get(k);
				j += Math.abs(gossipEntry.getValue());
				is[k] = j - 1;
			}

			Set<VillagerGossips.GossipEntry> set = Sets.newIdentityHashSet();

			for (int l = 0; l < i; l++) {
				int m = random.nextInt(j);
				int n = Arrays.binarySearch(is, m);
				set.add(list.get(n < 0 ? -n - 1 : n));
			}

			return set;
		}
	}

	private VillagerGossips.Reputation getReputationFor(UUID uUID) {
		return (VillagerGossips.Reputation)this.entityReputation.computeIfAbsent(uUID, uUIDx -> new VillagerGossips.Reputation());
	}

	public void shareGossipFrom(VillagerGossips villagerGossips, Random random, int i) {
		Collection<VillagerGossips.GossipEntry> collection = villagerGossips.pickGossips(random, i);
		collection.forEach(gossipEntry -> {
			int ix = gossipEntry.value - gossipEntry.type.shareDecrement;
			if (ix >= 2) {
				this.getReputationFor(gossipEntry.target).associatedGossip.mergeInt(gossipEntry.type, ix, VillagerGossips::max);
			}
		});
	}

	public int getReputationFor(UUID uUID, Predicate<VillageGossipType> predicate) {
		VillagerGossips.Reputation reputation = (VillagerGossips.Reputation)this.entityReputation.get(uUID);
		return reputation != null ? reputation.getValueFor(predicate) : 0;
	}

	public void startGossip(UUID uUID, VillageGossipType villageGossipType, int i) {
		VillagerGossips.Reputation reputation = this.getReputationFor(uUID);
		reputation.associatedGossip.mergeInt(villageGossipType, i, (integer, integer2) -> this.mergeReputation(villageGossipType, integer, integer2));
		reputation.clamp(villageGossipType);
		if (reputation.isObsolete()) {
			this.entityReputation.remove(uUID);
		}
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createList(this.entries().map(gossipEntry -> gossipEntry.serialize(dynamicOps)).map(Dynamic::getValue)));
	}

	public void deserialize(Dynamic<?> dynamic) {
		dynamic.asStream()
			.map(VillagerGossips.GossipEntry::deserialize)
			.flatMap(SystemUtil::stream)
			.forEach(gossipEntry -> this.getReputationFor(gossipEntry.target).associatedGossip.put(gossipEntry.type, gossipEntry.value));
	}

	private static int max(int i, int j) {
		return Math.max(i, j);
	}

	private int mergeReputation(VillageGossipType villageGossipType, int i, int j) {
		int k = i + j;
		return k > villageGossipType.maxValue ? Math.max(villageGossipType.maxValue, i) : k;
	}

	static class GossipEntry {
		public final UUID target;
		public final VillageGossipType type;
		public final int value;

		public GossipEntry(UUID uUID, VillageGossipType villageGossipType, int i) {
			this.target = uUID;
			this.type = villageGossipType;
			this.value = i;
		}

		public int getValue() {
			return this.value * this.type.multiplier;
		}

		public String toString() {
			return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
		}

		public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
			return SystemUtil.writeUuid(
				"Target",
				this.target,
				new Dynamic<>(
					dynamicOps,
					dynamicOps.createMap(
						ImmutableMap.of(
							dynamicOps.createString("Type"), dynamicOps.createString(this.type.key), dynamicOps.createString("Value"), dynamicOps.createInt(this.value)
						)
					)
				)
			);
		}

		public static Optional<VillagerGossips.GossipEntry> deserialize(Dynamic<?> dynamic) {
			return dynamic.get("Type")
				.asString()
				.map(VillageGossipType::byKey)
				.flatMap(
					villageGossipType -> SystemUtil.readUuid("Target", dynamic)
							.flatMap(uUID -> dynamic.get("Value").asNumber().map(number -> new VillagerGossips.GossipEntry(uUID, villageGossipType, number.intValue())))
				);
		}
	}

	static class Reputation {
		private final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap<>();

		private Reputation() {
		}

		public int getValueFor(Predicate<VillageGossipType> predicate) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.filter(entry -> predicate.test(entry.getKey()))
				.mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier)
				.sum();
		}

		public Stream<VillagerGossips.GossipEntry> entriesFor(UUID uUID) {
			return this.associatedGossip
				.object2IntEntrySet()
				.stream()
				.map(entry -> new VillagerGossips.GossipEntry(uUID, (VillageGossipType)entry.getKey(), entry.getIntValue()));
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

		public void clamp(VillageGossipType villageGossipType) {
			int i = this.associatedGossip.getInt(villageGossipType);
			if (i > villageGossipType.maxValue) {
				this.associatedGossip.put(villageGossipType, villageGossipType.maxValue);
			}

			if (i < 2) {
				this.remove(villageGossipType);
			}
		}

		public void remove(VillageGossipType villageGossipType) {
			this.associatedGossip.removeInt(villageGossipType);
		}
	}
}
