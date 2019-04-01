package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_4095<E extends class_1309> implements class_4213 {
	private final Map<class_4140<?>, Optional<?>> field_18322 = Maps.<class_4140<?>, Optional<?>>newHashMap();
	private final Map<class_4149<? extends class_4148<? super E>>, class_4148<? super E>> field_18323 = Maps.<class_4149<? extends class_4148<? super E>>, class_4148<? super E>>newLinkedHashMap();
	private final Map<Integer, Map<class_4168, Set<class_4097<? super E>>>> field_18324 = Maps.newTreeMap();
	private class_4170 field_18325 = class_4170.field_18603;
	private final Map<class_4168, Set<Pair<class_4140<?>, class_4141>>> field_18326 = Maps.<class_4168, Set<Pair<class_4140<?>, class_4141>>>newHashMap();
	private Set<class_4168> field_18327 = Sets.<class_4168>newHashSet();
	private final Set<class_4168> field_18328 = Sets.<class_4168>newHashSet();
	private class_4168 field_18329 = class_4168.field_18595;
	private long field_18853 = 0L;

	public <T> class_4095(Collection<class_4140<?>> collection, Collection<class_4149<? extends class_4148<? super E>>> collection2, Dynamic<T> dynamic) {
		collection.forEach(arg -> {
			Optional var10000 = (Optional)this.field_18322.put(arg, Optional.empty());
		});
		collection2.forEach(arg -> {
			class_4148 var10000 = (class_4148)this.field_18323.put(arg, arg.method_19102());
		});
		this.field_18323.values().forEach(arg -> {
			for (class_4140<?> lv : arg.method_19099()) {
				this.field_18322.put(lv, Optional.empty());
			}
		});

		for (Entry<Dynamic<T>, Dynamic<T>> entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
			this.method_18877(class_2378.field_18793.method_10223(new class_2960(((Dynamic)entry.getKey()).asString(""))), (Dynamic<T>)entry.getValue());
		}
	}

	public boolean method_18896(class_4140<?> arg) {
		return this.method_18876(arg, class_4141.field_18456);
	}

	private <T, U> void method_18877(class_4140<U> arg, Dynamic<T> dynamic) {
		this.method_18878(arg, (U)((Function)arg.method_19093().orElseThrow(RuntimeException::new)).apply(dynamic));
	}

	public void method_18875(class_4140<?> arg) {
		this.method_18879(arg, Optional.empty());
	}

	public <U> void method_18878(class_4140<U> arg, @Nullable U object) {
		this.method_18879(arg, Optional.ofNullable(object));
	}

	public <U> void method_18879(class_4140<U> arg, Optional<U> optional) {
		if (this.field_18322.containsKey(arg)) {
			if (optional.isPresent() && this.method_19948(optional.get())) {
				this.method_18875(arg);
			} else {
				this.field_18322.put(arg, optional);
			}
		}
	}

	public <U> Optional<U> method_19543(class_4140<U> arg) {
		Optional<?> optional = (Optional<?>)this.field_18322.get(arg);
		if (optional == null) {
			throw new IllegalStateException("No memory of type " + arg.method_19091() + " found");
		} else {
			return (Optional<U>)optional;
		}
	}

	public <U> Optional<U> method_18904(class_4140<U> arg) {
		return (Optional<U>)this.field_18322.getOrDefault(arg, Optional.empty());
	}

	public boolean method_18876(class_4140<?> arg, class_4141 arg2) {
		Optional<?> optional = (Optional<?>)this.field_18322.get(arg);
		return optional == null
			? false
			: arg2 == class_4141.field_18458 || arg2 == class_4141.field_18456 && optional.isPresent() || arg2 == class_4141.field_18457 && !optional.isPresent();
	}

	public class_4170 method_18894() {
		return this.field_18325;
	}

	public void method_18884(class_4170 arg) {
		this.field_18325 = arg;
	}

	public void method_18890(Set<class_4168> set) {
		this.field_18327 = set;
	}

	@Deprecated
	public Stream<class_4097<? super E>> method_18899() {
		return this.field_18324
			.values()
			.stream()
			.flatMap(map -> map.values().stream())
			.flatMap(Collection::stream)
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338);
	}

	public void method_18880(class_4168 arg) {
		this.field_18328.clear();
		this.field_18328.addAll(this.field_18327);
		boolean bl = this.field_18326.keySet().contains(arg) && this.method_18874(arg);
		this.field_18328.add(bl ? arg : this.field_18329);
	}

	public void method_18871(long l, long m) {
		if (m - this.field_18853 > 20L) {
			this.field_18853 = m;
			class_4168 lv = this.method_18894().method_19213((int)(l % 24000L));
			if (!this.field_18328.contains(lv)) {
				this.method_18880(lv);
			}
		}
	}

	public void method_18897(class_4168 arg) {
		this.field_18329 = arg;
	}

	public void method_18881(class_4168 arg, ImmutableList<Pair<Integer, ? extends class_4097<? super E>>> immutableList) {
		this.method_18882(arg, immutableList, ImmutableSet.of());
	}

	public void method_18882(class_4168 arg, ImmutableList<Pair<Integer, ? extends class_4097<? super E>>> immutableList, Set<Pair<class_4140<?>, class_4141>> set) {
		this.field_18326.put(arg, set);
		immutableList.forEach(
			pair -> ((Set)((Map)this.field_18324.computeIfAbsent(pair.getFirst(), integer -> Maps.newHashMap())).computeIfAbsent(arg, argxx -> Sets.newLinkedHashSet()))
					.add(pair.getSecond())
		);
	}

	public boolean method_18906(class_4168 arg) {
		return this.field_18328.contains(arg);
	}

	public class_4095<E> method_18911() {
		class_4095<E> lv = new class_4095<>(this.field_18322.keySet(), this.field_18323.keySet(), new Dynamic<>(class_2509.field_11560, new class_2487()));
		this.field_18322.forEach((arg2, optional) -> optional.ifPresent(object -> {
				Optional var10000 = (Optional)lv.field_18322.put(arg2, Optional.of(object));
			}));
		return lv;
	}

	public void method_19542(class_3218 arg, E arg2) {
		this.method_19544(arg, arg2);
		this.method_18891(arg, arg2);
		this.method_19545(arg, arg2);
	}

	public void method_18900(class_3218 arg, E arg2) {
		long l = arg2.field_6002.method_8510();
		this.method_18899().forEach(arg3 -> arg3.method_18925(arg, arg2, l));
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createMap(
			(Map<T, T>)this.field_18322
				.entrySet()
				.stream()
				.filter(entry -> ((class_4140)entry.getKey()).method_19093().isPresent() && ((Optional)entry.getValue()).isPresent())
				.map(
					entry -> Pair.of(
							dynamicOps.createString(((class_4140)entry.getKey()).method_19091().toString()),
							((class_4213)((Optional)entry.getValue()).get()).method_19508(dynamicOps)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("memories"), object));
	}

	private void method_19544(class_3218 arg, E arg2) {
		this.field_18323.values().stream().filter(arg3 -> arg3.method_19100(arg, arg2)).forEach(arg3 -> arg3.method_19101(arg, arg2));
	}

	private void method_18891(class_3218 arg, E arg2) {
		long l = arg.method_8510();
		this.field_18324
			.values()
			.stream()
			.flatMap(map -> map.entrySet().stream())
			.filter(entry -> this.field_18328.contains(entry.getKey()))
			.map(Entry::getValue)
			.flatMap(Collection::stream)
			.filter(argx -> argx.method_18921() == class_4097.class_4098.field_18337)
			.forEach(arg3 -> arg3.method_18922(arg, arg2, l));
	}

	private void method_19545(class_3218 arg, E arg2) {
		long l = arg.method_8510();
		this.method_18899().forEach(arg3 -> arg3.method_18923(arg, arg2, l));
	}

	private boolean method_18874(class_4168 arg) {
		return ((Set)this.field_18326.get(arg)).stream().allMatch(pair -> {
			class_4140<?> lv = (class_4140<?>)pair.getFirst();
			class_4141 lv2 = (class_4141)pair.getSecond();
			return this.method_18876(lv, lv2);
		});
	}

	private boolean method_19948(Object object) {
		return object instanceof Collection && ((Collection)object).isEmpty();
	}
}
