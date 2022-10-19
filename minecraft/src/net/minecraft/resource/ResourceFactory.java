package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;

/**
 * Provides resource access.
 */
@FunctionalInterface
public interface ResourceFactory {
	/**
	 * Finds and returns the corresponding resource for a resource's identifier.
	 * 
	 * <p>Starts by scanning each resource pack from highest priority to lowest. If no resource packs were found
	 * to contain the requested entry, will return {@link Optional#empty()}.
	 * 
	 * <p>The returned resource must be closed to avoid resource leaks.
	 * 
	 * @param id the resource identifier to search for
	 */
	Optional<Resource> getResource(Identifier id);

	default Resource getResourceOrThrow(Identifier id) throws FileNotFoundException {
		return (Resource)this.getResource(id).orElseThrow(() -> new FileNotFoundException(id.toString()));
	}

	default InputStream open(Identifier id) throws IOException {
		return this.getResourceOrThrow(id).getInputStream();
	}

	default BufferedReader openAsReader(Identifier id) throws IOException {
		return this.getResourceOrThrow(id).getReader();
	}

	static ResourceFactory fromMap(Map<Identifier, Resource> map) {
		return id -> Optional.ofNullable((Resource)map.get(id));
	}
}
