package net.minecraft.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

/**
 * A resource pack, providing resources to resource managers.
 * 
 * <p>They are single-use in the lifecycle of a {@linkplain LifecycledResourceManagerImpl
 * lifecycled resource manager}. A {@link ResourcePackProfile} is a persistent
 * representation of the resource packs, and can be used to recreate the packs
 * on demand.
 */
public interface ResourcePack extends AutoCloseable {
	String METADATA_PATH_SUFFIX = ".mcmeta";
	String PACK_METADATA_NAME = "pack.mcmeta";

	@Nullable
	InputSupplier<InputStream> openRoot(String... segments);

	@Nullable
	InputSupplier<InputStream> open(ResourceType type, Identifier id);

	void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer);

	Set<String> getNamespaces(ResourceType type);

	@Nullable
	<T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException;

	String getName();

	/**
	 * {@return whether the dynamic registry entries from this pack are always
	 * "stable"/not experimental}
	 */
	default boolean isAlwaysStable() {
		return false;
	}

	void close();

	@FunctionalInterface
	public interface ResultConsumer extends BiConsumer<Identifier, InputSupplier<InputStream>> {
	}
}
