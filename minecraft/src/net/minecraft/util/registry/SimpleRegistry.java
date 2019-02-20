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
	public <V extends T> V set(int i, Identifier identifier, V object) {
		this.indexedEntries.put((T)object, i);
		Validate.notNull(identifier);
		Validate.notNull(object);
		this.randomEntries = null;
		if (this.entries.containsKey(identifier)) {
			LOGGER.debug("Adding duplicate key '{}' to registry", identifier);
		}

		this.entries.put(identifier, (T)object);
		if (this.nextId <= i) {
			this.nextId = i + 1;
		}

		return object;
	}

	@Override
	public <V extends T> V add(Identifier identifier, V object) {
		return this.set(this.nextId, identifier, object);
	}

	@Nullable
	@Override
	public Identifier getId(T object) {
		return (Identifier)this.entries.inverse().get(object);
	}

	@Override
	public int getRawId(@Nullable T object) {
		return this.indexedEntries.getId(object);
	}

	@Nullable
	@Override
	public T get(int i) {
		return this.indexedEntries.get(i);
	}

	public Iterator<T> iterator() {
		return this.indexedEntries.iterator();
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier identifier) {
		return (T)this.entries.get(identifier);
	}

	@Override
	public Optional<T> getOrEmpty(@Nullable Identifier identifier) {
		return Optional.ofNullable(this.entries.get(identifier));
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
	public boolean containsId(Identifier identifier) {
		return this.entries.containsKey(identifier);
	}
}
