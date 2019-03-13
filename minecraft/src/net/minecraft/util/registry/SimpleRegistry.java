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
	protected final Int2ObjectBiMap<T> field_11110 = new Int2ObjectBiMap<>(256);
	protected final BiMap<Identifier, T> entries = HashBiMap.create();
	protected Object[] randomEntries;
	private int nextId;

	@Override
	public <V extends T> V method_10273(int i, Identifier identifier, V object) {
		this.field_11110.put((T)object, i);
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
	public <V extends T> V method_10272(Identifier identifier, V object) {
		return this.method_10273(this.nextId, identifier, object);
	}

	@Nullable
	@Override
	public Identifier method_10221(T object) {
		return (Identifier)this.entries.inverse().get(object);
	}

	@Override
	public int getRawId(@Nullable T object) {
		return this.field_11110.getId(object);
	}

	@Nullable
	@Override
	public T get(int i) {
		return this.field_11110.get(i);
	}

	public Iterator<T> iterator() {
		return this.field_11110.iterator();
	}

	@Nullable
	@Override
	public T method_10223(@Nullable Identifier identifier) {
		return (T)this.entries.get(identifier);
	}

	@Override
	public Optional<T> method_17966(@Nullable Identifier identifier) {
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
	public boolean method_10250(Identifier identifier) {
		return this.entries.containsKey(identifier);
	}
}
