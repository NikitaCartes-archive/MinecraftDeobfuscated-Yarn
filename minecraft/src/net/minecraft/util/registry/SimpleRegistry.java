package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Int2ObjectBiMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap<>(256);
	protected final BiMap<Identifier, T> entries = HashBiMap.create();
	protected Object[] randomEntries;
	private int nextId;

	@Override
	public <V extends T> V set(int rawId, Identifier id, V entry) {
		this.indexedEntries.put((T)entry, rawId);
		Validate.notNull(id);
		Validate.notNull(entry);
		this.randomEntries = null;
		if (this.entries.containsKey(id)) {
			LOGGER.debug("Adding duplicate key '{}' to registry", id);
		}

		this.entries.put(id, (T)entry);
		if (this.nextId <= rawId) {
			this.nextId = rawId + 1;
		}

		return entry;
	}

	@Override
	public <V extends T> V add(Identifier id, V entry) {
		return this.set(this.nextId, id, entry);
	}

	@Nullable
	@Override
	public Identifier getId(T entry) {
		return (Identifier)this.entries.inverse().get(entry);
	}

	@Override
	public int getRawId(@Nullable T entry) {
		return this.indexedEntries.getId(entry);
	}

	@Nullable
	@Override
	public T get(int index) {
		return this.indexedEntries.get(index);
	}

	public Iterator<T> iterator() {
		return this.indexedEntries.iterator();
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier id) {
		return (T)this.entries.get(id);
	}

	@Override
	public Optional<T> getOrEmpty(@Nullable Identifier id) {
		return Optional.ofNullable(this.entries.get(id));
	}

	@Override
	public Set<Identifier> getIds() {
		return Collections.unmodifiableSet(this.entries.keySet());
	}

	@Override
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	@Nullable
	@Override
	public T getRandom(Random random) {
		if (this.randomEntries == null) {
			Collection<?> collection = this.entries.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.randomEntries = collection.toArray(new Object[collection.size()]);
		}

		return (T)this.randomEntries[random.nextInt(this.randomEntries.length)];
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean containsId(Identifier id) {
		return this.entries.containsKey(id);
	}
}
