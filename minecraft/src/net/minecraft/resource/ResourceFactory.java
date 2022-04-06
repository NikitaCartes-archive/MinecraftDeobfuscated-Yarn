package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	 * to contain the requested entry, will throw a {@code FileNotFoundException}.
	 * 
	 * <p>The returned resource must be closed to avoid resource leaks.
	 * 
	 * @throws java.io.FileNotFoundException if the identified resource could not be found, or could not be loaded.
	 * @throws IOException if the identified resource was found but a stream to it could not be opened.
	 * 
	 * @param id the resource identifier to search for
	 */
	Optional<Resource> getResource(Identifier id);

	default Resource getResourceOrThrow(Identifier identifier) throws FileNotFoundException {
		return (Resource)this.getResource(identifier).orElseThrow(() -> new FileNotFoundException(identifier.toString()));
	}

	default InputStream open(Identifier identifier) throws IOException {
		return this.getResourceOrThrow(identifier).getInputStream();
	}

	default BufferedReader openAsReader(Identifier identifier) throws IOException {
		return this.getResourceOrThrow(identifier).getReader();
	}
}
