package net.minecraft.resource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface ResourcePack extends Closeable {
	@Environment(EnvType.CLIENT)
	InputStream openRoot(String string) throws IOException;

	InputStream open(ResourceType resourceType, Identifier identifier) throws IOException;

	Collection<Identifier> findResources(ResourceType resourceType, String string, int i, Predicate<String> predicate);

	boolean contains(ResourceType resourceType, Identifier identifier);

	Set<String> getNamespaces(ResourceType resourceType);

	@Nullable
	<T> T parseMetadata(ResourceMetadataReader<T> resourceMetadataReader) throws IOException;

	String getName();
}
