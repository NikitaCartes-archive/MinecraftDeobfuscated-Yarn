package net.minecraft.world.storage;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public record StorageKey(String level, RegistryKey<World> dimension, String type) {
	public StorageKey withSuffix(String suffix) {
		return new StorageKey(this.level, this.dimension, this.type + suffix);
	}
}
