package net.minecraft.client.search;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SearchableContainer<T> extends Searchable<T> {
	void add(T object);

	void clear();

	void reload();
}
