package net.minecraft.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadata;

/**
 * A resource of binary data.
 * 
 * <p>The resource must be closed before disposal to avoid resource leaks.
 * 
 * @see ResourceFactory#getResource(Identifier)
 * @see ResourceManager#getAllResources(Identifier)
 */
public class Resource {
	private final String resourcePackName;
	private final Resource.InputSupplier<InputStream> inputSupplier;
	private final Resource.InputSupplier<ResourceMetadata> metadataSupplier;
	@Nullable
	private ResourceMetadata metadata;

	public Resource(String resourcePackName, Resource.InputSupplier<InputStream> inputSupplier, Resource.InputSupplier<ResourceMetadata> metadataSupplier) {
		this.resourcePackName = resourcePackName;
		this.inputSupplier = inputSupplier;
		this.metadataSupplier = metadataSupplier;
	}

	public Resource(String resourcePackName, Resource.InputSupplier<InputStream> inputSupplier) {
		this.resourcePackName = resourcePackName;
		this.inputSupplier = inputSupplier;
		this.metadataSupplier = () -> ResourceMetadata.NONE;
		this.metadata = ResourceMetadata.NONE;
	}

	/**
	 * Returns the user-friendly name of the pack this resource is from.
	 */
	public String getResourcePackName() {
		return this.resourcePackName;
	}

	/**
	 * Returns the input stream of this resource.
	 * 
	 * <p>This input stream is closed when this resource is closed.
	 */
	public InputStream getInputStream() throws IOException {
		return this.inputSupplier.get();
	}

	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
	}

	/**
	 * {@return the metadata for the resource}
	 * 
	 * <p>The metadata must then be decoded using
	 * {@link ResourceMetadata#decode(ResourceMetadataReader)} before using.
	 */
	public ResourceMetadata getMetadata() throws IOException {
		if (this.metadata == null) {
			this.metadata = this.metadataSupplier.get();
		}

		return this.metadata;
	}

	@FunctionalInterface
	public interface InputSupplier<T> {
		T get() throws IOException;
	}
}
