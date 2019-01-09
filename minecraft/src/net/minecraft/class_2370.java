package net.minecraft;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2370<T> extends class_2385<T> {
	protected static final Logger field_11111 = LogManager.getLogger();
	protected final class_3513<T> field_11110 = new class_3513<>(256);
	protected final BiMap<class_2960, T> field_11107 = HashBiMap.create();
	protected Object[] field_11108;
	private int field_11109;

	@Override
	public <V extends T> V method_10273(int i, class_2960 arg, V object) {
		this.field_11110.method_15230((T)object, i);
		Validate.notNull(arg);
		Validate.notNull(object);
		this.field_11108 = null;
		if (this.field_11107.containsKey(arg)) {
			field_11111.debug("Adding duplicate key '{}' to registry", arg);
		}

		this.field_11107.put(arg, (T)object);
		if (this.field_11109 <= i) {
			this.field_11109 = i + 1;
		}

		return object;
	}

	@Override
	public <V extends T> V method_10272(class_2960 arg, V object) {
		return this.method_10273(this.field_11109, arg, object);
	}

	@Nullable
	@Override
	public class_2960 method_10221(T object) {
		return (class_2960)this.field_11107.inverse().get(object);
	}

	@Override
	public int method_10249(@Nullable T object) {
		return this.field_11110.method_15231(object);
	}

	@Nullable
	@Override
	public T method_10200(int i) {
		return this.field_11110.method_10200(i);
	}

	public Iterator<T> iterator() {
		return this.field_11110.iterator();
	}

	@Nullable
	@Override
	public T method_10223(@Nullable class_2960 arg) {
		return (T)this.field_11107.get(arg);
	}

	@Override
	public Set<class_2960> method_10235() {
		return Collections.unmodifiableSet(this.field_11107.keySet());
	}

	@Override
	public boolean method_10274() {
		return this.field_11107.isEmpty();
	}

	@Nullable
	@Override
	public T method_10240(Random random) {
		if (this.field_11108 == null) {
			Collection<?> collection = this.field_11107.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.field_11108 = collection.toArray(new Object[collection.size()]);
		}

		return (T)this.field_11108[random.nextInt(this.field_11108.length)];
	}

	@Override
	public boolean method_10250(class_2960 arg) {
		return this.field_11107.containsKey(arg);
	}
}
