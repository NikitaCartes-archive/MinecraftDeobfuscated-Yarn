package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class StatType<T> implements Iterable<Stat<T>> {
	private final Registry<T> registry;
	private final Map<T, Stat<T>> stats = new IdentityHashMap();
	@Nullable
	@Environment(EnvType.CLIENT)
	private Text field_26382;

	public StatType(Registry<T> registry) {
		this.registry = registry;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasStat(T key) {
		return this.stats.containsKey(key);
	}

	public Stat<T> getOrCreateStat(T key, StatFormatter formatter) {
		return (Stat<T>)this.stats.computeIfAbsent(key, object -> new Stat<>(this, (T)object, formatter));
	}

	public Registry<T> getRegistry() {
		return this.registry;
	}

	public Iterator<Stat<T>> iterator() {
		return this.stats.values().iterator();
	}

	public Stat<T> getOrCreateStat(T key) {
		return this.getOrCreateStat(key, StatFormatter.DEFAULT);
	}

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return "stat_type." + Registry.STAT_TYPE.getId(this).toString().replace(':', '.');
	}

	@Environment(EnvType.CLIENT)
	public Text method_30739() {
		if (this.field_26382 == null) {
			this.field_26382 = new TranslatableText(this.getTranslationKey());
		}

		return this.field_26382;
	}
}
