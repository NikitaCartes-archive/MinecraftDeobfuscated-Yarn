package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

/**
 * A resource of binary data.
 * 
 * <p>The resource must be closed before disposal to avoid resource leaks.
 * 
 * @see ResourceFactory#getResource(Identifier)
 * @see ResourceManager#getAllResources(Identifier)
 */
public interface Resource extends Closeable {
	/**
	 * Returns the location of this resource.
	 * 
	 * <p>Within each resource pack, this location is a unique identifier for a
	 * resource; however, in a resource manager, there may be multiple resources
	 * with the same location available.
	 */
	Identifier getId();

	/**
	 * Returns the input stream of this resource.
	 * 
	 * <p>This input stream is closed when this resource is closed.
	 */
	InputStream getInputStream();

	/**
	 * Returns if this resource has any metadata.
	 */
	boolean hasMetadata();

	/**
	 * Returns a metadata of this resource by the {@code metaReader}, or {@code null}
	 * if no such metadata exists.
	 * 
	 * @param metaReader the metadata reader
	 */
	@Nullable
	<T> T getMetadata(ResourceMetadataReader<T> metaReader);

	/**
	 * Returns the user-friendly name of the pack this resource is from.
	 */
	String getResourcePackName();
}
