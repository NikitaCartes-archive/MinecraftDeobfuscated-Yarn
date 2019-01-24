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

public class IdRegistry<T> extends ModifiableRegistry<T> {
	protected static final Logger ID_LOGGER = LogManager.getLogger();
	protected final Int2ObjectBiMap<T> idStore = new Int2ObjectBiMap<>(256);
	protected final BiMap<Identifier, T> objectMap = HashBiMap.create();
	protected Object[] randomValueArray;
	private int nextId;

	@Override
	public <V extends T> V set(int i, Identifier identifier, V object) {
		this.idStore.put((T)object, i);
		Validate.notNull(identifier);
		Validate.notNull(object);
		this.randomValueArray = null;
		if (this.objectMap.containsKey(identifier)) {
			ID_LOGGER.debug("Adding duplicate key '{}' to registry", identifier);
		}

		this.objectMap.put(identifier, (T)object);
		if (this.nextId <= i) {
			this.nextId = i + 1;
		}

		return object;
	}

	@Override
	public <V extends T> V register(Identifier identifier, V object) {
		return this.set(this.nextId, identifier, object);
	}

	@Nullable
	@Override
	public Identifier getId(T object) {
		return (Identifier)this.objectMap.inverse().get(object);
	}

	@Override
	public int getRawId(@Nullable T object) {
		return this.idStore.getId(object);
	}

	@Nullable
	@Override
	public T getInt(int i) {
		return this.idStore.getInt(i);
	}

	public Iterator<T> iterator() {
		return this.idStore.iterator();
	}

	@Nullable
	@Override
	public T get(@Nullable Identifier identifier) {
		return (T)this.objectMap.get(identifier);
	}

	@Override
	public Optional<T> method_17966(@Nullable Identifier identifier) {
		return Optional.ofNullable(this.objectMap.get(identifier));
	}

	@Override
	public Set<Identifier> keys() {
		return Collections.unmodifiableSet(this.objectMap.keySet());
	}

	@Override
	public boolean isEmpty() {
		return this.objectMap.isEmpty();
	}

	@Nullable
	@Override
	public T getRandom(Random random) {
		if (this.randomValueArray == null) {
			Collection<?> collection = this.objectMap.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.randomValueArray = collection.toArray(new Object[collection.size()]);
		}

		return (T)this.randomValueArray[random.nextInt(this.randomValueArray.length)];
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean contains(Identifier identifier) {
		return this.objectMap.containsKey(identifier);
	}
}
