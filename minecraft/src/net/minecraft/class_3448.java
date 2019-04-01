package net.minecraft;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3448<T> implements Iterable<class_3445<T>> {
	private final class_2378<T> field_15323;
	private final Map<T, class_3445<T>> field_15324 = new IdentityHashMap();

	public class_3448(class_2378<T> arg) {
		this.field_15323 = arg;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_14958(T object) {
		return this.field_15324.containsKey(object);
	}

	public class_3445<T> method_14955(T object, class_3446 arg) {
		return (class_3445<T>)this.field_15324.computeIfAbsent(object, objectx -> new class_3445<>(this, (T)objectx, arg));
	}

	public class_2378<T> method_14959() {
		return this.field_15323;
	}

	public Iterator<class_3445<T>> iterator() {
		return this.field_15324.values().iterator();
	}

	public class_3445<T> method_14956(T object) {
		return this.method_14955(object, class_3446.field_16975);
	}

	@Environment(EnvType.CLIENT)
	public String method_14957() {
		return "stat_type." + class_2378.field_11152.method_10221(this).toString().replace(':', '.');
	}
}
