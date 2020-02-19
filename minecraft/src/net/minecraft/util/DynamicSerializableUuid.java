package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.UUID;

public final class DynamicSerializableUuid implements DynamicSerializable {
	private final UUID uuid;

	public DynamicSerializableUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createString(this.uuid.toString());
	}

	public static DynamicSerializableUuid of(Dynamic<?> dynamic) {
		String string = (String)dynamic.asString().orElseThrow(() -> new IllegalArgumentException("Could not parse UUID"));
		return new DynamicSerializableUuid(UUID.fromString(string));
	}

	public String toString() {
		return this.uuid.toString();
	}
}
