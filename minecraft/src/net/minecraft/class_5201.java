package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;

public abstract class class_5201 {
	protected final class_5202<?> field_24145;
	private final OptionalInt field_24146;

	public class_5201(class_5202<?> arg, OptionalInt optionalInt) {
		this.field_24145 = arg;
		this.field_24146 = optionalInt;
	}

	public abstract int method_27378(int i, int j);

	public OptionalInt method_27377() {
		return this.field_24146;
	}

	public <T> T method_27380(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.FEATURE_SIZE_TYPE.getId(this.field_24145).toString()));
		this.field_24146.ifPresent(i -> builder.put(dynamicOps.createString("min_clipped_height"), dynamicOps.createInt(i)));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
	}
}
