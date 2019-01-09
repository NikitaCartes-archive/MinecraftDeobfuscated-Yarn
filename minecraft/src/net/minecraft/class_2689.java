package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_2689<O, S extends class_2688<S>> {
	private static final Pattern field_12314 = Pattern.compile("^[a-z0-9_]+$");
	private final O field_12317;
	private final ImmutableSortedMap<String, class_2769<?>> field_12316;
	private final ImmutableList<S> field_12315;

	protected <A extends class_2679<O, S>> class_2689(O object, class_2689.class_2691<O, S, A> arg, Map<String, class_2769<?>> map) {
		this.field_12317 = object;
		this.field_12316 = ImmutableSortedMap.copyOf(map);
		Map<Map<class_2769<?>, Comparable<?>>, A> map2 = Maps.<Map<class_2769<?>, Comparable<?>>, A>newLinkedHashMap();
		List<A> list = Lists.<A>newArrayList();
		Stream<List<Comparable<?>>> stream = Stream.of(Collections.emptyList());

		for (class_2769<?> lv : this.field_12316.values()) {
			stream = stream.flatMap(listx -> lv.method_11898().stream().map(comparable -> {
					List<Comparable<?>> list2 = Lists.<Comparable<?>>newArrayList(listx);
					list2.add(comparable);
					return list2;
				}));
		}

		stream.forEach(list2 -> {
			Map<class_2769<?>, Comparable<?>> map2x = class_2367.method_10209(this.field_12316.values(), list2);
			A lv = arg.create(object, ImmutableMap.copyOf(map2x));
			map2.put(map2x, lv);
			list.add(lv);
		});

		for (A lv2 : list) {
			lv2.method_11571(map2);
		}

		this.field_12315 = ImmutableList.copyOf(list);
	}

	public ImmutableList<S> method_11662() {
		return this.field_12315;
	}

	public S method_11664() {
		return (S)this.field_12315.get(0);
	}

	public O method_11660() {
		return this.field_12317;
	}

	public Collection<class_2769<?>> method_11659() {
		return this.field_12316.values();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("block", this.field_12317)
			.add("properties", this.field_12316.values().stream().map(class_2769::method_11899).collect(Collectors.toList()))
			.toString();
	}

	@Nullable
	public class_2769<?> method_11663(String string) {
		return this.field_12316.get(string);
	}

	public static class class_2690<O, S extends class_2688<S>> {
		private final O field_12318;
		private final Map<String, class_2769<?>> field_12319 = Maps.<String, class_2769<?>>newHashMap();

		public class_2690(O object) {
			this.field_12318 = object;
		}

		public class_2689.class_2690<O, S> method_11667(class_2769<?>... args) {
			for (class_2769<?> lv : args) {
				this.method_11669(lv);
				this.field_12319.put(lv.method_11899(), lv);
			}

			return this;
		}

		private <T extends Comparable<T>> void method_11669(class_2769<T> arg) {
			String string = arg.method_11899();
			if (!class_2689.field_12314.matcher(string).matches()) {
				throw new IllegalArgumentException(this.field_12318 + " has invalidly named property: " + string);
			} else {
				Collection<T> collection = arg.method_11898();
				if (collection.size() <= 1) {
					throw new IllegalArgumentException(this.field_12318 + " attempted use property " + string + " with <= 1 possible values");
				} else {
					for (T comparable : collection) {
						String string2 = arg.method_11901(comparable);
						if (!class_2689.field_12314.matcher(string2).matches()) {
							throw new IllegalArgumentException(this.field_12318 + " has property: " + string + " with invalidly named value: " + string2);
						}
					}

					if (this.field_12319.containsKey(string)) {
						throw new IllegalArgumentException(this.field_12318 + " has duplicate property: " + string);
					}
				}
			}
		}

		public <A extends class_2679<O, S>> class_2689<O, S> method_11668(class_2689.class_2691<O, S, A> arg) {
			return new class_2689<>(this.field_12318, arg, this.field_12319);
		}
	}

	public interface class_2691<O, S extends class_2688<S>, A extends class_2679<O, S>> {
		A create(O object, ImmutableMap<class_2769<?>, Comparable<?>> immutableMap);
	}
}
