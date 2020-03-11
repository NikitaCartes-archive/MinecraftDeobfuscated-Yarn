package net.minecraft.util.dynamic;

import com.mojang.datafixers.types.DynamicOps;

public interface DynamicSerializable {
	<T> T serialize(DynamicOps<T> ops);
}
