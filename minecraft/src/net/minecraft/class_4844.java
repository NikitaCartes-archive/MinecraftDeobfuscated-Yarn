package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.UUID;
import net.minecraft.util.DynamicSerializable;

public final class class_4844 implements DynamicSerializable {
	private final UUID field_22412;

	public class_4844(UUID uUID) {
		this.field_22412 = uUID;
	}

	public UUID method_24814() {
		return this.field_22412;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createString(this.field_22412.toString());
	}

	public static class_4844 method_24815(Dynamic<?> dynamic) {
		String string = (String)dynamic.asString().orElseThrow(() -> new IllegalArgumentException("Could not parse UUID"));
		return new class_4844(UUID.fromString(string));
	}

	public String toString() {
		return this.field_22412.toString();
	}
}
