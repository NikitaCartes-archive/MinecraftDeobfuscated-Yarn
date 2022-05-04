package net.minecraft.client.search;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A functional interface that provides searching.
 */
@Environment(EnvType.CLIENT)
public interface SearchProvider<T> {
	/**
	 * {@return the search result of {@code text}}
	 */
	List<T> findAll(String text);
}
