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
import javax.annotation.Nullable;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4095<E extends LivingEntity> implements class_4213 {
	private final Map<class_4140<?>, Optional<?>> field_18322 = Maps.<class_4140<?>, Optional<?>>newHashMap();
	private final Map<class_4149<? extends class_4148<? super E>>, class_4148<? super E>> field_18323 = Maps.<class_4149<? extends class_4148<? super E>>, class_4148<? super E>>newLinkedHashMap();
	private final Map<Integer, Map<class_4168, Set<class_4097<? super E>>>> field_18324 = Maps.newTreeMap();
	private class_4170 field_18325 = class_4170.field_18603;
	private Map<class_4168, Set<Pair<class_4140<?>, class_4141>>> field_18326 = Maps.<class_4168, Set<Pair<class_4140<?>, class_4141>>>newHashMap();
	private Set<class_4168> field_18327 = Sets.<class_4168>newHashSet();
	private Set<class_4168> field_18328 = Sets.<class_4168>newHashSet();
	private class_4168 field_18329 = class_4168.field_18595;

	public <T> class_4095(Collection<class_4140<?>> collection, Collection<class_4149<? extends class_4148<? super E>>> collection2, Dynamic<T> dynamic) {
		collection.forEach(arg -> {
			Optional var10000 = (Optional)this.field_18322.put(arg, Optional.empty());
		});
		collection2.forEach(arg -> {
			class_4148 var10000 = (class_4148)this.field_18323.put(arg, arg.method_19102());
		});

		for (Entry<Dynamic<T>, Dynamic<T>> entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
			this.method_18877(Registry.field_18793.method_10223(new Identifier(((Dynamic)entry.getKey()).asString(""))), (Dynamic<T>)entry.getValue());
		}
	}

	private <T, U> void method_18877(class_4140<U> arg, Dynamic<T> dynamic) {
		this.method_18878(arg, (U)((Function)arg.method_19093().orElseThrow(RuntimeException::new)).apply(dynamic));
	}

	public void method_18875(class_4140<?> arg) {
		this.method_18879(arg, Optional.empty());
	}

	public boolean method_18896(class_4140<?> arg) {
		return this.field_18322.containsKey(arg);
	}

	public <U> void method_18878(class_4140<U> arg, @Nullable U object) {
		this.method_18879(arg, Optional.ofNullable(object));
	}

	public <U> void method_18879(class_4140<U> arg, Optional<U> optional) {
		if (this.field_18322.containsKey(arg)) {
			this.field_18322.put(arg, optional);
		}
	}

	public <U> Optional<U> method_18904(class_4140<U> arg) {
		return (Optional<U>)this.field_18322.get(arg);
	}

	public boolean method_18876(class_4140<?> arg, class_4141 arg2) {
		return this.field_18322.containsKey(arg)
			&& (
				arg2 == class_4141.field_18458
					|| arg2 == class_4141.field_18456 && ((Optional)this.field_18322.get(arg)).isPresent()
					|| arg2 == class_4141.field_18457 && !((Optional)this.field_18322.get(arg)).isPresent()
			);
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

	public void method_18880(class_4168 arg) {
		this.field_18328.clear();
		this.field_18328.addAll(this.field_18327);
		boolean bl = this.field_18326.keySet().contains(arg)
			&& ((Set)this.field_18326.get(arg)).stream().allMatch(pair -> this.method_18876((class_4140<?>)pair.getFirst(), (class_4141)pair.getSecond()));
		this.field_18328.add(bl ? arg : this.field_18329);
	}

	public void method_18871(long l) {
		this.method_18880(this.method_18894().method_19213((int)(l % 24000L)));
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
		class_4095<E> lv = new class_4095<>(this.field_18322.keySet(), this.field_18323.keySet(), new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));
		this.field_18322.forEach((arg2, optional) -> optional.ifPresent(object -> {
				Optional var10000 = (Optional)lv.field_18322.put(arg2, Optional.of(object));
			}));
		return lv;
	}

	public void method_18891(ServerWorld serverWorld, E livingEntity) {
		this.field_18323.values().stream().filter(arg -> arg.method_19100(serverWorld, livingEntity)).forEach(arg -> arg.method_19101(serverWorld, livingEntity));
		long l = serverWorld.getTime();
		this.field_18324
			.values()
			.stream()
			.flatMap(map -> map.entrySet().stream())
			.filter(entry -> this.field_18328.contains(entry.getKey()))
			.map(Entry::getValue)
			.flatMap(Collection::stream)
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18337)
			.forEach(arg -> arg.method_18922(serverWorld, livingEntity, l));
		this.field_18324
			.values()
			.stream()
			.flatMap(map -> map.values().stream())
			.flatMap(Collection::stream)
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338)
			.forEach(arg -> arg.method_18923(serverWorld, livingEntity, l));
	}

	public void method_18900(ServerWorld serverWorld, E livingEntity) {
		long l = livingEntity.field_6002.getTime();
		this.field_18324
			.values()
			.stream()
			.flatMap(map -> map.values().stream())
			.flatMap(Collection::stream)
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338)
			.forEach(arg -> arg.method_18925(serverWorld, livingEntity, l));
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
}
