package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;

public class StatType<T> implements Iterable<Stat<T>> {
	private final Registry<T> registry;
	private final Map<T, Stat<T>> stats = new IdentityHashMap();

	public StatType(Registry<T> registry) {
		this.registry = registry;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasStat(T object) {
		return this.stats.containsKey(object);
	}

	public Stat<T> getOrCreateStat(T object, StatFormatter statFormatter) {
		return (Stat<T>)this.stats.computeIfAbsent(object, objectx -> new Stat<>(this, (T)objectx, statFormatter));
	}

	public Registry<T> getRegistry() {
		return this.registry;
	}

	public Iterator<Stat<T>> iterator() {
		return this.stats.values().iterator();
	}

	public Stat<T> getOrCreateStat(T object) {
		return this.getOrCreateStat(object, StatFormatter.DEFAULT);
	}

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return "stat_type." + Registry.STAT_TYPE.method_10221(this).toString().replace(':', '.');
	}
}
