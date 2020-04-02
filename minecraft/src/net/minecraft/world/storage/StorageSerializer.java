package net.minecraft.world.storage;

import com.mojang.datafixers.types.DynamicOps;

public interface StorageSerializer<O> {
	<T> T serialize(O object, DynamicOps<T> ops);
}
