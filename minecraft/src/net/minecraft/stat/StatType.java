package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;

public class StatType<T> implements Iterable<Stat<T>> {
	private final Registry<T> field_15323;
	private final Map<T, Stat<T>> field_15324 = new IdentityHashMap();

	public StatType(Registry<T> registry) {
		this.field_15323 = registry;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14958(T object) {
		return this.field_15324.containsKey(object);
	}

	public Stat<T> method_14955(T object, StatFormatter statFormatter) {
		return (Stat<T>)this.field_15324.computeIfAbsent(object, objectx -> new Stat<>(this, (T)objectx, statFormatter));
	}

	public Registry<T> method_14959() {
		return this.field_15323;
	}

	@Environment(EnvType.CLIENT)
	public int method_14960() {
		return this.field_15324.size();
	}

	public Iterator<Stat<T>> iterator() {
		return this.field_15324.values().iterator();
	}

	public Stat<T> method_14956(T object) {
		return this.method_14955(object, StatFormatter.DEFAULT);
	}

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return "stat_type." + Registry.STAT_TYPE.getId(this).toString().replace(':', '.');
	}
}
