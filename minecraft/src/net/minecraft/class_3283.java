package net.minecraft;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class class_3283<T extends class_3288> {
	private final Set<class_3285> field_14227 = Sets.<class_3285>newHashSet();
	private final Map<String, T> field_14226 = Maps.<String, T>newLinkedHashMap();
	private final List<T> field_14225 = Lists.<T>newLinkedList();
	private final class_3288.class_3290<T> field_14228;

	public class_3283(class_3288.class_3290<T> arg) {
		this.field_14228 = arg;
	}

	public void method_14445() {
		Set<String> set = (Set<String>)this.field_14225.stream().map(class_3288::method_14463).collect(Collectors.toCollection(LinkedHashSet::new));
		this.field_14226.clear();
		this.field_14225.clear();

		for (class_3285 lv : this.field_14227) {
			lv.method_14453(this.field_14226, this.field_14228);
		}

		this.method_14448();
		this.field_14225.addAll((Collection)set.stream().map(this.field_14226::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));

		for (T lv2 : this.field_14226.values()) {
			if (lv2.method_14464() && !this.field_14225.contains(lv2)) {
				lv2.method_14466().method_14468(this.field_14225, lv2, Functions.identity(), false);
			}
		}
	}

	private void method_14448() {
		List<Entry<String, T>> list = Lists.<Entry<String, T>>newArrayList(this.field_14226.entrySet());
		this.field_14226.clear();
		list.stream().sorted(Entry.comparingByKey()).forEachOrdered(entry -> {
			class_3288 var10000 = (class_3288)this.field_14226.put(entry.getKey(), entry.getValue());
		});
	}

	public void method_14447(Collection<T> collection) {
		this.field_14225.clear();
		this.field_14225.addAll(collection);

		for (T lv : this.field_14226.values()) {
			if (lv.method_14464() && !this.field_14225.contains(lv)) {
				lv.method_14466().method_14468(this.field_14225, lv, Functions.identity(), false);
			}
		}
	}

	public Collection<T> method_14441() {
		return this.field_14226.values();
	}

	public Collection<T> method_14442() {
		Collection<T> collection = Lists.<T>newArrayList(this.field_14226.values());
		collection.removeAll(this.field_14225);
		return collection;
	}

	public Collection<T> method_14444() {
		return this.field_14225;
	}

	@Nullable
	public T method_14449(String string) {
		return (T)this.field_14226.get(string);
	}

	public void method_14443(class_3285 arg) {
		this.field_14227.add(arg);
	}
}
