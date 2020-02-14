package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.DynamicSerializable;

public class class_4831<T> implements DynamicSerializable {
	private final T field_22330;
	private final long field_22331;

	public class_4831(T object, long l) {
		this.field_22330 = object;
		this.field_22331 = l;
	}

	public class_4831(T object) {
		this(object, Long.MAX_VALUE);
	}

	public class_4831(Function<Dynamic<?>, T> function, Dynamic<?> dynamic) {
		this((T)function.apply(dynamic.get("value").get().orElseThrow(RuntimeException::new)), dynamic.get("expiry").asLong(Long.MAX_VALUE));
	}

	public static <T> class_4831<T> method_24635(T object) {
		return new class_4831<>(object);
	}

	public static <T> class_4831<T> method_24636(T object, long l) {
		return new class_4831<>(object, l);
	}

	public long method_24633() {
		return this.field_22331;
	}

	public T method_24637() {
		return this.field_22330;
	}

	public boolean method_24634(long l) {
		return this.method_24638(l) <= 0L;
	}

	public long method_24638(long l) {
		return this.field_22331 - l;
	}

	public String toString() {
		return this.field_22330.toString() + (this.method_24633() != Long.MAX_VALUE ? " (expiry: " + this.field_22331 + ")" : "");
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = Maps.<T, T>newHashMap();
		map.put(ops.createString("value"), ((DynamicSerializable)this.field_22330).serialize(ops));
		if (this.field_22331 != Long.MAX_VALUE) {
			map.put(ops.createString("expiry"), ops.createLong(this.field_22331));
		}

		return ops.createMap(map);
	}
}
