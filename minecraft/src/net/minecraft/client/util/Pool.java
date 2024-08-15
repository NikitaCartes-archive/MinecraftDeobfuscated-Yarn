package net.minecraft.client.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Pool implements ObjectAllocator, AutoCloseable {
	private final int lifespan;
	private final Deque<Pool.Entry<?>> entries = new ArrayDeque();

	public Pool(int lifespan) {
		this.lifespan = lifespan;
	}

	public void decrementLifespan() {
		Iterator<? extends Pool.Entry<?>> iterator = this.entries.iterator();

		while (iterator.hasNext()) {
			Pool.Entry<?> entry = (Pool.Entry<?>)iterator.next();
			if (entry.lifespan-- == 0) {
				entry.close();
				iterator.remove();
			}
		}
	}

	@Override
	public <T> T acquire(ClosableFactory<T> factory) {
		Iterator<? extends Pool.Entry<?>> iterator = this.entries.iterator();

		while (iterator.hasNext()) {
			Pool.Entry<?> entry = (Pool.Entry<?>)iterator.next();
			if (entry.factory.equals(factory)) {
				iterator.remove();
				return (T)entry.object;
			}
		}

		return factory.create();
	}

	@Override
	public <T> void release(ClosableFactory<T> factory, T object) {
		this.entries.addFirst(new Pool.Entry<>(factory, object, this.lifespan));
	}

	public void clear() {
		this.entries.forEach(Pool.Entry::close);
		this.entries.clear();
	}

	public void close() {
		this.clear();
	}

	@VisibleForTesting
	protected Collection<Pool.Entry<?>> method_61951() {
		return this.entries;
	}

	@Environment(EnvType.CLIENT)
	@VisibleForTesting
	protected static final class Entry<T> implements AutoCloseable {
		final ClosableFactory<T> factory;
		final T object;
		int lifespan;

		Entry(ClosableFactory<T> factory, T object, int lifespan) {
			this.factory = factory;
			this.object = object;
			this.lifespan = lifespan;
		}

		public void close() {
			this.factory.close(this.object);
		}
	}
}
