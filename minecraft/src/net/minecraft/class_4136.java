package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

public class class_4136 {
	private final Map<UUID, class_4136.class_4137> field_18419 = Maps.<UUID, class_4136.class_4137>newHashMap();

	private Stream<class_4136.class_4138> method_19074() {
		return this.field_18419.entrySet().stream().flatMap(entry -> ((class_4136.class_4137)entry.getValue()).method_19079((UUID)entry.getKey()));
	}

	private Collection<class_4136.class_4138> method_19070(Random random, int i) {
		List<class_4136.class_4138> list = (List<class_4136.class_4138>)this.method_19074().collect(Collectors.toList());
		if (list.isEmpty()) {
			return Collections.emptyList();
		} else {
			int[] is = new int[list.size()];
			int j = 0;

			for (int k = 0; k < list.size(); k++) {
				class_4136.class_4138 lv = (class_4136.class_4138)list.get(k);
				j += Math.abs(lv.method_19083());
				is[k] = j - 1;
			}

			Set<class_4136.class_4138> set = Sets.newIdentityHashSet();

			for (int l = 0; l < i; l++) {
				int m = random.nextInt(j);
				int n = Arrays.binarySearch(is, m);
				set.add(list.get(n < 0 ? -n - 1 : n));
			}

			return set;
		}
	}

	private class_4136.class_4137 method_19071(UUID uUID) {
		return (class_4136.class_4137)this.field_18419.computeIfAbsent(uUID, uUIDx -> new class_4136.class_4137());
	}

	public void method_19061(class_4136 arg, Random random, int i) {
		Collection<class_4136.class_4138> collection = arg.method_19070(random, i);
		collection.forEach(argx -> {
			int ix = argx.field_18423 - argx.field_18422.field_18434;
			if (ix > 2) {
				this.method_19071(argx.field_18421).field_18420.mergeInt(argx.field_18422, ix, class_4136::method_19059);
			}
		});
	}

	public int method_19073(UUID uUID, Predicate<class_4139> predicate) {
		class_4136.class_4137 lv = (class_4136.class_4137)this.field_18419.get(uUID);
		return lv != null ? lv.method_19081(predicate) : 0;
	}

	public long method_19062(class_4139 arg) {
		return this.field_18419.values().stream().filter(arg2 -> arg2.field_18420.containsKey(arg)).count();
	}

	public void method_19072(UUID uUID, class_4139 arg, int i) {
		this.method_19071(uUID).field_18420.mergeInt(arg, i, (integer, integer2) -> this.method_19063(arg, integer, integer2));
	}

	public <T> Dynamic<T> method_19067(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createList(this.method_19074().map(arg -> arg.method_19087(dynamicOps)).map(Dynamic::getValue)));
	}

	public void method_19066(Dynamic<?> dynamic) {
		dynamic.asStream()
			.map(class_4136.class_4138::method_19084)
			.flatMap(SystemUtil::method_17815)
			.forEach(arg -> this.method_19071(arg.field_18421).field_18420.put(arg.field_18422, arg.field_18423));
	}

	private static int method_19059(int i, int j) {
		return Math.max(i, j);
	}

	private int method_19063(class_4139 arg, int i, int j) {
		int k = i + j;
		return k > arg.field_18432 ? Math.max(arg.field_18432, i) : k;
	}

	static class class_4137 {
		private final Object2IntMap<class_4139> field_18420 = new Object2IntOpenHashMap<>();

		private class_4137() {
		}

		public int method_19081(Predicate<class_4139> predicate) {
			return this.field_18420
				.object2IntEntrySet()
				.stream()
				.filter(entry -> predicate.test(entry.getKey()))
				.mapToInt(entry -> entry.getIntValue() * ((class_4139)entry.getKey()).field_18431)
				.sum();
		}

		public Stream<class_4136.class_4138> method_19079(UUID uUID) {
			return this.field_18420.object2IntEntrySet().stream().map(entry -> new class_4136.class_4138(uUID, (class_4139)entry.getKey(), entry.getIntValue()));
		}
	}

	static class class_4138 {
		public final UUID field_18421;
		public final class_4139 field_18422;
		public final int field_18423;

		public class_4138(UUID uUID, class_4139 arg, int i) {
			this.field_18421 = uUID;
			this.field_18422 = arg;
			this.field_18423 = i;
		}

		public int method_19083() {
			return this.field_18423 * this.field_18422.field_18431;
		}

		public String toString() {
			return "GossipEntry{target=" + this.field_18421 + ", type=" + this.field_18422 + ", value=" + this.field_18423 + '}';
		}

		public <T> Dynamic<T> method_19087(DynamicOps<T> dynamicOps) {
			return SystemUtil.method_19482(
				"Target",
				this.field_18421,
				new Dynamic<>(
					dynamicOps,
					dynamicOps.createMap(
						ImmutableMap.of(
							dynamicOps.createString("Type"),
							dynamicOps.createString(this.field_18422.field_18430),
							dynamicOps.createString("Value"),
							dynamicOps.createInt(this.field_18423)
						)
					)
				)
			);
		}

		public static Optional<class_4136.class_4138> method_19084(Dynamic<?> dynamic) {
			return dynamic.get("Type")
				.asString()
				.map(class_4139::method_19090)
				.flatMap(
					arg -> SystemUtil.method_19481("Target", dynamic)
							.flatMap(uUID -> dynamic.get("Value").asNumber().map(number -> new class_4136.class_4138(uUID, arg, number.intValue())))
				);
		}
	}
}
